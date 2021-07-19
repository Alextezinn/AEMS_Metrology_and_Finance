package ru.sfedu.aems.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.beans.*;
import ru.sfedu.aems.enums.*;
import ru.sfedu.aems.utils.ConfigurationUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DataProviderXML implements DataProvider {
    private final static Logger log = LogManager.getLogger(DataProviderXML.class);
    private final String PATH = ConfigurationUtil.getConfigurationEntry("path_xml_file");
    private final String FILE_EXTENTION = ConfigurationUtil.getConfigurationEntry("xml_file_extention");

    public DataProviderXML() throws IOException {
    }

    //API
    @Override
    public Status outfit(String operation, long idOutfit, List<Long> idEmployees, Date dateStartWork, long idCustomer,
                         long idHeadOfDepartment, long idPlaceWork, String typeWork,
                         String typeInstrument, long idTypeMeasurementInstrument, StatusWork statusWork) {
        try {
            switch (operation.trim().toUpperCase()) {
                case Constants.CREATE_OUTFIT:
                    if ((boolean) createOutfit(idOutfit, dateStartWork, idCustomer, idHeadOfDepartment, idPlaceWork,
                            typeWork, typeInstrument, idTypeMeasurementInstrument, idEmployees, statusWork).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
                case Constants.DELETE_OUTFIT:
                    if (deleteOutfit(idOutfit, idHeadOfDepartment)) {
                        return Status.SUCCESS;
                    }
                    break;
                case Constants.EDIT_OUTFIT:
                    if ((boolean) editOutfit(idOutfit, idEmployees, idHeadOfDepartment).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            log.error(Constants.COMMAND_ERROR);
            return Status.FAIL;
        }
        log.error(Constants.COMMAND_ERROR);
        return Status.FAIL;
    }

    @Override
    public List<Object> createOutfit(long idOutfit, Date dateStartWork, long idCustomer, long idHeadOfDepartment, long idPlaceWork, String typeWork, String typeInstrument, long idTypeMeasurementInstrument, List<Long> idEmployees, StatusWork statusWork) {
        Outfit outfit;
        try {
            checkFileExists(Outfit.class);
            TypeOfWork typeOfWork = chooseTypeOfWork(typeWork);
            TypeMeasurementInstrument typeMeasurementInstrument = chooseTypeMeasurementInstrument(typeInstrument);
            List<Employee> listEmployeeFromXml = getRecordsFromXmlFile(Employee.class);
            List getHeadDepartment = findExistsHeadOfDepartmentId(idHeadOfDepartment);
            List getEmployeesOutfit = findExistsEmployeeId(idEmployees);
            List getCustomerOutfit = findExistsCustomerId(idCustomer);
            List getPlaceWorkInOutfit = findPlaceWorkId(idPlaceWork);
            List getMeasuremantInstrument = checkIdTypeMeasurementInstrument(typeMeasurementInstrument, idTypeMeasurementInstrument);
            if ((boolean) getHeadDepartment.get(0) || (boolean) getEmployeesOutfit.get(0) || (boolean) getCustomerOutfit.get(0) || (boolean) getPlaceWorkInOutfit.get(0) || checkIdEmployeesNotNull(idEmployees) || checkTypeOfWorkNotNull(typeOfWork) || checkTypeMeasurementInstrumentNotNull(typeMeasurementInstrument) || checkCountEmployees(idEmployees) || checkAllEmployeesId(listEmployeeFromXml, idEmployees) || (boolean) getMeasuremantInstrument.get(0)) {
                return Arrays.asList(false, null);
            }
            List<Outfit> listOutfitFromXml = getRecordsFromXmlFile(Outfit.class);
            if (!checkForDuplicatesOutfitId(listOutfitFromXml, idOutfit)) {
                log.error(Constants.ERROR_ID_OUTFIT_EXISTS);
                return Arrays.asList(false, null);
            }
            if (checkForDuplicatesEmployeeId(idEmployees)) {
                log.error(Constants.ERROR_DUPLICATES_EMPLOYEE_ID);
                return Arrays.asList(false, null);
            }
            List<HeadOfDepartment> listHeadOfDepartment = getRecordsFromXmlFile(HeadOfDepartment.class);
            HeadOfDepartment headOfDepartment = listHeadOfDepartment.stream()
                    .filter(e -> e.getId() == idHeadOfDepartment).findFirst().get();
            Executor executor = headOfDepartment.getExecutor();
            outfit = new Outfit();
            outfit.setId(idOutfit);
            outfit.setDateStartWork(dateStartWork);
            outfit.setCustomer((Customer) getCustomerOutfit.get(1));
            outfit.setExecutor(executor);
            outfit.setPlaceWork((PlaceOfWork) getPlaceWorkInOutfit.get(1));
            outfit.setTypeWork(typeOfWork);
            outfit.setMeasurementInstrument((MeansOfMeasurement) getMeasuremantInstrument.get(1));
            outfit.setEmployees((List<Employee>) getEmployeesOutfit.get(1));
            outfit.setStatusWork(statusWork);
            log.debug(outfit.toString());
            listOutfitFromXml.add(outfit);
            setRecordsToXmlFile(listOutfitFromXml);
            log.info(Constants.CREATE_OUTFIT_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_CREATE_OUTFIT);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(outfit));
    }

    @Override
    public boolean deleteOutfit(long idOutfit, long idHeadOfDepartment) {
        List<Outfit> newListOutfit;
        try {
            checkFileExists(Outfit.class);
            List<Outfit> listOutfitFromXml = getRecordsFromXmlFile(Outfit.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || findExistsOutfitId(idOutfit) || checkOutfitsNotNull(listOutfitFromXml)) {
                return false;
            }
            newListOutfit = listOutfitFromXml.stream()
                    .filter(e -> e.getId() != idOutfit)
                    .collect(Collectors.toList());
            if (newListOutfit.isEmpty()) {
                rewriteFile(Outfit.class);
                return true;
            }
            log.debug(newListOutfit.toString());
            setRecordsToXmlFile(newListOutfit);
            log.info(Constants.DELETE_OUTFIT_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_DELETE_OUTFIT);
            log.error(e);
            return false;
        }
        return true;
    }

    @Override
    public List<Object> editOutfit(long idOutfit, List<Long> idEmployees, long idHeadOfDepartment) {
        List<Outfit> newListOutfit;
        try {
            checkFileExists(Outfit.class);
            List<Outfit> listOutfitFromXml = getRecordsFromXmlFile(Outfit.class);
            List<Employee> listEmployeeFromXml = getRecordsFromXmlFile(Employee.class);
            List getEmployeesOutfit = findExistsEmployeeId(idEmployees);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || findExistsOutfitId(idOutfit) || (boolean) getEmployeesOutfit.get(0) || checkIdEmployeesNotNull(idEmployees) || checkOutfitsNotNull(listOutfitFromXml) || checkCountEmployees(idEmployees) || checkAllEmployeesId(listEmployeeFromXml, idEmployees)) {
                return Arrays.asList(false, null);
            }
            Outfit changeOutfit = listOutfitFromXml.stream()
                    .filter(e -> e.getId() == idOutfit).findFirst().get();
            newListOutfit = listOutfitFromXml.stream()
                    .filter(e -> e.getId() != idOutfit)
                    .collect(Collectors.toList());
            changeOutfit.setEmployees((List<Employee>) getEmployeesOutfit.get(1));
            log.debug(changeOutfit.toString());
            newListOutfit.add(changeOutfit);
            setRecordsToXmlFile(newListOutfit);
            log.info(Constants.EDIT_OUTFIT_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_EDIT_OUTFIT);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(newListOutfit));
    }

    @Override
    public Status analysis(String operation, long idAnalysis, long idHeadOfDepartment) {
        try {
            switch (operation.trim().toUpperCase()) {
                case Constants.ALL_EMPLOYEE_OUTFITS:
                    if ((boolean) allEmployeeOutfits(idAnalysis, idHeadOfDepartment).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
                case Constants.ALL_EXECUTOR_OUTFITS:
                    if ((boolean) allExecutorOutfits(idAnalysis, idHeadOfDepartment).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            log.error(Constants.COMMAND_ERROR);
            return Status.FAIL;
        }
        log.error(Constants.COMMAND_ERROR);
        return Status.FAIL;
    }

    @Override
    public List<Object> allEmployeeOutfits(long idAnalysis, long idHeadOfDepartment) {
        List<Outfit> listEmployeeIdOutfits;
        try {
            List<Outfit> listOutfitFromXml = getRecordsFromXmlFile(Outfit.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean) findExistsEmployeeId(Arrays.asList(idAnalysis)).get(0) || checkOutfitsNotNull(listOutfitFromXml)) {
                return Arrays.asList(false, null);
            }
            listEmployeeIdOutfits = listOutfitFromXml.stream()
                    .filter(e -> !checkIdEmployeeInIdsEmployees(e.getEmployees(), idAnalysis))
                    .collect(Collectors.toList());
            log.debug(listEmployeeIdOutfits.toString());
            log.info(Constants.ALL_EMPLOYEE_OUTFITS_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_ALL_EMPLOYEE_OUTFITS);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(listEmployeeIdOutfits));
    }

    @Override
    public List<Object> allExecutorOutfits(long idAnalysis, long idHeadOfDepartment) {
        List<Outfit> listExecutorInOutfits;
        try {
            List<Outfit> listOutfitFromXml = getRecordsFromXmlFile(Outfit.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || findExistsExecutorId(idAnalysis) || checkOutfitsNotNull(listOutfitFromXml)) {
                return Arrays.asList(false, null);
            }
            listExecutorInOutfits = listOutfitFromXml.stream()
                    .filter(e -> e.getExecutor().getId() == idAnalysis)
                    .collect(Collectors.toList());
            log.debug(listExecutorInOutfits.toString());
            log.info(Constants.ALL_EXECUTOR_OUTFITS_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_ALL_EXECUTOR_OUTFITS);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(listExecutorInOutfits));
    }

    @Override
    public Status employeeManagement(String operation, long idEmployee, String name, long idHeadOfDepartment) {
        try {
            switch (operation.trim().toUpperCase()) {
                case Constants.ADD_EMPLOYEE:
                    if ((boolean) addEmployee(idEmployee, name, idHeadOfDepartment).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
                case Constants.PROMOTE_EMPLOYEE:
                    if ((boolean) promoteEmployee(idEmployee, idHeadOfDepartment).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
                case Constants.DEMOTE_EMPLOYEE:
                    if ((boolean) demoteEmployee(idEmployee, idHeadOfDepartment).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
                case Constants.REMOVE_EMPLOYEE:
                    if (removeEmployee(idEmployee, idHeadOfDepartment)) {
                        return Status.SUCCESS;
                    }
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            log.error(Constants.COMMAND_ERROR);
            return Status.FAIL;
        }
        log.error(Constants.COMMAND_ERROR);
        return Status.FAIL;
    }

    @Override
    public List<Object> addEmployee(long idEmployee, String name, long idHeadOfDepartment) {
        Employee employee;
        try {
            if ((name == null) || (!validName(name))) {
                log.error(Constants.ERROR_BAD_NAME);
                return Arrays.asList(false, null);
            }
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0)) {
                return Arrays.asList(false, null);
            }
            List<Employee> listEmployeeFromXml = getRecordsFromXmlFile(Employee.class);
            if (!(boolean) checkForDuplicatesEmployeeId(Arrays.asList(idEmployee), listEmployeeFromXml).get(0)) {
                log.error(Constants.ERROR_ID_EMPLOYEE_EXISTS);
                return Arrays.asList(false, null);
            }
            TypeEmployee typeEmployee = TypeEmployee.JUNIOR_EMPLOYEE;
            employee = new Employee();
            employee.setId(idEmployee);
            employee.setTypeEmployee(typeEmployee);
            employee.setSalary(12000);
            employee.setName(name);
            log.debug(employee.toString());
            listEmployeeFromXml.add(employee);
            setRecordsToXmlFile(listEmployeeFromXml);
            log.info(Constants.ADD_EMPLOYEE_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_ADD_EMPLOYEE);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(employee));
    }

    @Override
    public List<Object> promoteEmployee(long idEmployee, long idHeadOfDepartment) {
        List<Employee> newListEmployee;
        Employee changeEmployee;
        try {
            List<Employee> listEmployeeFromXml = getRecordsFromXmlFile(Employee.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean) findExistsEmployeeId(Arrays.asList(idEmployee)).get(0) || checkEmployeesNotNull(listEmployeeFromXml)) {
                return Arrays.asList(false, null);
            }
            changeEmployee = listEmployeeFromXml.stream()
                    .filter(e -> e.getId() == idEmployee).findFirst().get();
            if (changeEmployee.getTypeEmployee() == TypeEmployee.SENIOR_EMPLOYEE) {
                log.error(Constants.ERROR_IMPOSSIBLE_PROMOTE_EMPLOYEE);
                return Arrays.asList(false, null);
            }
            newListEmployee = listEmployeeFromXml.stream()
                    .filter(e -> e.getId() != idEmployee)
                    .collect(Collectors.toList());
            changeEmployee.setTypeEmployee(TypeEmployee.SENIOR_EMPLOYEE);
            log.debug(changeEmployee.toString());
            newListEmployee.add(increaseSalaryEmployee(changeEmployee));
            setRecordsToXmlFile(newListEmployee);
            log.info(Constants.PROMOTE_EMPLOYEE_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_PROMOTE_EMPLOYEE);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(changeEmployee));
    }

    @Override
    public Employee increaseSalaryEmployee(Employee employee) {
        try {
            employee.setSalary(employee.getSalary() + 5000.0);
        } catch (Exception e) {
            log.error(e);
        }
        return employee;
    }

    @Override
    public List<Object> demoteEmployee(long idEmployee, long idHeadOfDepartment) {
        List<Employee> newListEmployee;
        Employee changeEmployee;
        try {
            List<Employee> listEmployeeFromXml = getRecordsFromXmlFile(Employee.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean) findExistsEmployeeId(Arrays.asList(idEmployee)).get(0) || checkEmployeesNotNull(listEmployeeFromXml)) {
                return Arrays.asList(false, null);
            }
            changeEmployee = listEmployeeFromXml.stream()
                    .filter(e -> e.getId() == idEmployee).findFirst().get();
            if (changeEmployee.getTypeEmployee() == TypeEmployee.JUNIOR_EMPLOYEE) {
                log.error(Constants.ERROR_IMPOSSIBLE_DEMOTE_EMPLOYEE);
                return Arrays.asList(false, null);
            }
            newListEmployee = listEmployeeFromXml.stream()
                    .filter(e -> e.getId() != idEmployee)
                    .collect(Collectors.toList());
            changeEmployee.setTypeEmployee(TypeEmployee.JUNIOR_EMPLOYEE);
            log.debug(changeEmployee.toString());
            newListEmployee.add(lowerSalaryEmployee(changeEmployee));
            setRecordsToXmlFile(newListEmployee);
            log.info(Constants.DEMOTE_EMPLOYEE_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_DEMOTE_EMPLOYEE);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(changeEmployee));
    }

    @Override
    public Employee lowerSalaryEmployee(Employee employee) {
        try {
            employee.setSalary(employee.getSalary() - 5000);
        } catch (Exception e) {
            log.error(e);
        }
        return employee;
    }

    @Override
    public boolean removeEmployee(long idEmployee, long idHeadOfDepartment) {
        List<Employee> newListEmployee;
        try {
            List<Employee> listEmployeeFromXml = getRecordsFromXmlFile(Employee.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean) findExistsEmployeeId(Arrays.asList(idEmployee)).get(0) || checkEmployeesNotNull(listEmployeeFromXml)) {
                return false;
            }
            newListEmployee = listEmployeeFromXml.stream()
                    .filter(e -> e.getId() != idEmployee)
                    .collect(Collectors.toList());
            if (newListEmployee.isEmpty()) {
                rewriteFile(Employee.class);
                return true;
            }
            log.debug(newListEmployee.toString());
            setRecordsToXmlFile(newListEmployee);
            log.info(Constants.REMOVE_EMPLOYEE_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_REMOVE_EMPLOYEE);
            log.error(e);
            return false;
        }
        return true;
    }

    @Override
    public Status changeStatusOutfit(String operation, long idOutfit, long idExecutor) {
        try {
            switch (operation.trim().toUpperCase()) {
                case Constants.FINISH_WORK:
                    if ((boolean) finishWork(idOutfit, idExecutor).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
                case Constants.CHANGE_STATUS_TO_IN_WORK:
                    if ((boolean) changeStatusToInWork(idOutfit, idExecutor).get(0)) {
                        return Status.SUCCESS;
                    }
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            log.error(Constants.COMMAND_ERROR);
            return Status.FAIL;
        }
        log.error(Constants.COMMAND_ERROR);
        return Status.FAIL;
    }

    @Override
    public List<Object> finishWork(long idOutfit, long idExecutor) {
        List<Outfit> newListOutfit;
        Outfit changeOutfit;
        try {
            List<Outfit> listOutfitFromXml = getRecordsFromXmlFile(Outfit.class);
            if (findExistsOutfitId(idOutfit) || findExistsExecutorId(idExecutor) || checkOutfitsNotNull(listOutfitFromXml)) {
                return Arrays.asList(false, null);
            }
            changeOutfit = listOutfitFromXml.stream()
                    .filter(e -> e.getId() == idOutfit).findFirst().get();
            if (changeOutfit.getExecutor().getId() != idExecutor) {
                log.error(Constants.ERROR_OUTFIT_HAS_DIFFERENT_EXECUTOR);
                return Arrays.asList(false, null);
            }
            changeOutfit.setStatusWork(StatusWork.COMPLETED);
            changeOutfit.setCompletionDate(new Date());
            newListOutfit = listOutfitFromXml.stream()
                    .filter(e -> e.getId() != idOutfit)
                    .collect(Collectors.toList());
            log.debug(changeOutfit.toString());
            newListOutfit.add(changeOutfit);
            setRecordsToXmlFile(newListOutfit);
            log.info(Constants.FINISH_WORK_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_FINISH_WORK);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(changeOutfit));
    }

    @Override
    public List<Object> changeStatusToInWork(long idOutfit, long idExecutor) {
        List<Outfit> newListOutfit;
        Outfit changeOutfit;
        try {
            List<Outfit> listOutfitFromXml = getRecordsFromXmlFile(Outfit.class);
            if (findExistsOutfitId(idOutfit) || findExistsExecutorId(idExecutor) || checkOutfitsNotNull(listOutfitFromXml)) {
                return Arrays.asList(false, null);
            }
            changeOutfit = listOutfitFromXml.stream()
                    .filter(e -> e.getId() == idOutfit).findFirst().get();
            if (changeOutfit.getExecutor().getId() != idExecutor) {
                log.error(Constants.ERROR_OUTFIT_HAS_DIFFERENT_EXECUTOR);
                return Arrays.asList(false, null);
            }
            changeOutfit.setStatusWork(StatusWork.IN_WORK);
            newListOutfit = listOutfitFromXml.stream()
                    .filter(e -> e.getId() != idOutfit)
                    .collect(Collectors.toList());
            log.debug(changeOutfit.toString());
            newListOutfit.add(changeOutfit);
            setRecordsToXmlFile(newListOutfit);
            log.info(Constants.CHANGE_STATUS_TO_IN_WORK_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_CHANGE_STATUS_TO_IN_WORK);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(changeOutfit));
    }

    //Helper methods
    public static TypeOfWork chooseTypeOfWork(String typeWork) {
        try {
            switch (typeWork.split(Constants.UNDERSCORE)[0].toUpperCase()) {
                case Constants.CALIBRATION:
                    return TypeOfWork.CALIBRATION_SI;
                case Constants.VERIFICATION:
                    return TypeOfWork.VERIFICATION_SI;
                case Constants.CERTIFICATION:
                    return TypeOfWork.MEASUREMENT_METHOD_CERTIFICATION;
                case Constants.ACCREDITATION:
                    return TypeOfWork.ACCREDITATION_OF_LEGAL_ENTITIES_AND_INDIVIDUAL_ENTREPRENEURS;
                case Constants.TESTING:
                    return TypeOfWork.TESTING_OF_SI_AND_REFERENCE_MATERIALS;
            }
        } catch (Exception e) {
            log.error(e);
        }
        log.error(Constants.ERROR_CHOOSE_TYPE_OF_WORK);
        return null;
    }

    public static TypeMeasurementInstrument chooseTypeMeasurementInstrument(String measurementInstrument) {
        try {
            switch (measurementInstrument.toUpperCase()) {
                case Constants.MECHANICAL:
                    return TypeMeasurementInstrument.MECHANICAL;
                case Constants.ELECTRICAL:
                    return TypeMeasurementInstrument.ELECTRICAL;
            }
        } catch (Exception e) {
            log.error(e);
        }
        log.error(Constants.ERROR_CHOOSE_TYPE_MEASUREMENT_INSTRUMENT);
        return null;
    }

    private boolean findExistsExecutorId(long idExecutor) throws Exception {
        List<Executor> executors = getRecordsFromXmlFile(Executor.class);
        Executor executor = executors.stream().filter(e -> e.getId() == idExecutor)
                .findFirst()
                .get();
        return executor == null;
    }

    private List<Object> findExistsHeadOfDepartmentId(long idHeadOfDepartment) throws Exception {
        List<HeadOfDepartment> listHeadOfDepartment = getRecordsFromXmlFile(HeadOfDepartment.class);
        List checkForDuplicatesHeadDepartmentId = checkForDuplicatesHeadOfDepartmentId(listHeadOfDepartment, idHeadOfDepartment);
        if ((boolean) checkForDuplicatesHeadDepartmentId.get(0)) {
            log.error(Constants.ERROR_HEAD_DEPARTMENT_ID_NOT_FOUND);
            return Arrays.asList(true, checkForDuplicatesHeadDepartmentId.get(1));
        }
        return Arrays.asList(false, checkForDuplicatesHeadDepartmentId.get(1));
    }

    private boolean findExistsOutfitId(long idOutfit) throws Exception {
        List<Outfit> listOutfit = getRecordsFromXmlFile(Outfit.class);
        if (checkForDuplicatesOutfitId(listOutfit, idOutfit)) {
            log.error(Constants.ERROR_OUTFIT_ID_NOT_FOUND);
            return true;
        }
        return false;
    }

    private List<Object> findPlaceWorkId(long idPlaceWork) throws Exception {
        List<PlaceOfWork> listPlaceWork = getRecordsFromXmlFile(PlaceOfWork.class);
        List placeWorkInOutfit = checkForDuplicatesPlaceOfWorkId(listPlaceWork, idPlaceWork);
        if ((boolean) placeWorkInOutfit.get(0)) {
            log.error(Constants.ERROR_PLACE_OF_WORK_ID_NOT_FOUND);
            return Arrays.asList(true, placeWorkInOutfit.get(1));
        }
        return Arrays.asList(false, placeWorkInOutfit.get(1));
    }

    private List<Object> findExistsCustomerId(long idCustomer) throws Exception {
        List<Customer> listCustomer = getRecordsFromXmlFile(Customer.class);
        List customerInOutfit = checkForDuplicatesCustomerId(listCustomer, idCustomer);
        if ((boolean) customerInOutfit.get(0)) {
            log.error(Constants.ERROR_CUSTOMER_ID_NOT_FOUND);
            return Arrays.asList(true, customerInOutfit.get(1));
        }
        return Arrays.asList(false, customerInOutfit.get(1));
    }

    private List<Object> findExistsEmployeeId(List<Long> idEmployee) throws Exception {
        List<Employee> listEmployee = getRecordsFromXmlFile(Employee.class);
        List getDuplicatesEmployee = checkForDuplicatesEmployeeId(idEmployee, listEmployee);
        if ((boolean) getDuplicatesEmployee.get(0)) {
            log.error(Constants.ERROR_EMPLOYEE_ID_NOT_FOUND);
            return Arrays.asList(true, getDuplicatesEmployee.get(1));
        }
        return Arrays.asList(false, getDuplicatesEmployee.get(1));
    }

    public boolean checkIdEmployeesNotNull(List<Long> idEmployees) {
        try {
            return idEmployees.isEmpty();
        } catch (NullPointerException e) {
            log.error(Constants.ERROR_EMPLOYEE_ID_NOT_FOUND);
            return true;
        }
    }

    private boolean checkEmployeesNotNull(List<Employee> listEmployee) {
        try {
            return listEmployee.isEmpty();
        } catch (NullPointerException e) {
            log.error(Constants.ERROR_NOT_DATA_EMPLOYEE);
            return true;
        }
    }

    private boolean checkIdEmployeeInIdsEmployees(List<Employee> employees, long idEmployee) {
        List<Employee> dublicateId = employees.stream()
                .filter(i -> i.getId() == idEmployee)
                .collect(Collectors.toList());
        return dublicateId.isEmpty();
    }

    private boolean checkTypeOfWorkNotNull(TypeOfWork typeOfWork) {
        try {
            return typeOfWork == null;
        } catch (NullPointerException e) {
            log.error(Constants.ERROR_CHOOSE_TYPE_OF_WORK);
            return true;
        }
    }

    private boolean checkTypeMeasurementInstrumentNotNull(TypeMeasurementInstrument typeMeasurementInstrument) {
        try {
            return typeMeasurementInstrument == null;
        } catch (NullPointerException e) {
            log.error(Constants.ERROR_CHOOSE_TYPE_MEASUREMENT_INSTRUMENT);
            return true;
        }
    }

    private boolean checkOutfitsNotNull(List<Outfit> listOutfit) {
        try {
            return listOutfit.isEmpty();
        } catch (NullPointerException e) {
            log.error(Constants.ERROR_NOT_DATA_OUTFIT);
            return true;
        }
    }

    private boolean checkCountEmployees(List<Long> idEmployees) {
        if ((idEmployees.size() < 2) || (idEmployees.size() > 3)) {
            log.error(Constants.ERROR_COUNT_EMPLOYEES);
            return true;
        }
        return false;
    }

    private List<Object> checkForDuplicatesEmployeeId(List<Long> idEmployees, List<Employee> listEmployee) {
        List<Employee> dublicateId = listEmployee.stream()
                .filter(i -> Collections.frequency(idEmployees, i.getId()) > 0)
                .collect(Collectors.toList());
        boolean flag = dublicateId.isEmpty();
        return Arrays.asList(flag, dublicateId);
    }

    private boolean checkForDuplicatesEmployeeId(List<Long> idEmployees) {
        List<Long> dublicateId = idEmployees.stream()
                .filter(i -> Collections.frequency(idEmployees, i) > 1)
                .collect(Collectors.toList());
        return !dublicateId.isEmpty();
    }

    private List<Object> checkForDuplicatesCustomerId(List<Customer> list, long id) {
        List<Customer> dublicateId = list.stream()
                .filter(e -> e.getId() == id)
                .collect(Collectors.toList());
        boolean flag = dublicateId.isEmpty();
        return Arrays.asList(flag, dublicateId.get(0));
    }

    private List<Object> checkIdTypeMeasurementInstrument(TypeMeasurementInstrument typeInstrument, long id) {
        try {
            List dublicateId;
            MeansOfMeasurement meansOfMeasurement = new MeansOfMeasurement();
            if (typeInstrument == TypeMeasurementInstrument.ELECTRICAL) {
                List<ElectricalMeasurement> listElectricalMeasurementFromXml = getRecordsFromXmlFile(ElectricalMeasurement.class);
                dublicateId = listElectricalMeasurementFromXml.stream()
                        .filter(e -> e.getId() == id)
                        .collect(Collectors.toList());
                boolean flag = dublicateId.isEmpty();
                meansOfMeasurement = (MeansOfMeasurement) dublicateId.get(0);
                return Arrays.asList(flag, meansOfMeasurement);
            } else if (typeInstrument == TypeMeasurementInstrument.MECHANICAL) {
                List<MechanicalMeasurement> listMechanicalMeasurementFromXml = getRecordsFromXmlFile(MechanicalMeasurement.class);
                dublicateId = listMechanicalMeasurementFromXml.stream()
                        .filter(e -> e.getId() == id)
                        .collect(Collectors.toList());
                boolean flag = dublicateId.isEmpty();
                meansOfMeasurement = (MeansOfMeasurement) dublicateId.get(0);
                return Arrays.asList(flag, meansOfMeasurement);
            }
        } catch (Exception e) {
            log.error(Constants.ERROR_TYPE_MEASUREMENT_INSTRUMENT);
            log.error(e);
        }
        log.error(Constants.ERROR_TYPE_MEASUREMENT_INSTRUMENT);
        return Arrays.asList(true, null);
    }

    private List<Object> checkForDuplicatesPlaceOfWorkId(List<PlaceOfWork> list, long id) {
        List<PlaceOfWork> dublicateId = list.stream()
                .filter(e -> e.getId() == id)
                .collect(Collectors.toList());
        boolean flag = dublicateId.isEmpty();
        return Arrays.asList(flag, dublicateId.get(0));
    }

    private boolean checkForDuplicatesOutfitId(List<Outfit> list, long id) {
        List<Outfit> dublicateId = list.stream()
                .filter(e -> e.getId() == id)
                .collect(Collectors.toList());
        return dublicateId.isEmpty();
    }

    private List<Object> checkForDuplicatesHeadOfDepartmentId(List<HeadOfDepartment> list, long id) {
        List<HeadOfDepartment> dublicateId = list.stream()
                .filter(e -> e.getId() == id)
                .collect(Collectors.toList());
        boolean flag = dublicateId.isEmpty();
        return Arrays.asList(flag, dublicateId.get(0));
    }

    private void checkFileExists(Class className) {
        File file = new File(PATH + className.getSimpleName().toLowerCase() + FILE_EXTENTION);
        if (!file.exists()) {
            try {
                new File(PATH + className.getSimpleName().toLowerCase() + FILE_EXTENTION).createNewFile();
            } catch (IOException e) {
                log.error(Constants.ERROR_NOT_DATA_OUTFIT);
                log.error(e);
            }
        }
    }

    private boolean checkAllEmployeesId(List<Employee> listEmployee, List<Long> idEmployees) {
        List<Employee> dublicateId = null;
        try {
            dublicateId = listEmployee.stream()
                    .filter(i -> Collections.frequency(idEmployees, i.getId()) > 0)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e);
        }
        if (dublicateId.size() == idEmployees.size()) {
            return false;
        }
        log.error(Constants.ERROR_EMPLOYEE_ID_NOT_FOUND);
        return true;
    }

    public void rewriteFile(Class className) {
        try {
            FileWriter file = new FileWriter(PATH + className.getSimpleName().toLowerCase() + FILE_EXTENTION, false);
            file.flush();
        } catch (Exception e) {
            log.error(Constants.ERROR_REWRITE_FILE);
            log.error(e);
        }
    }

    private static boolean validName(String name) {
        boolean result = name.matches("^[A-Za-z]{3,20}$");
        return result;
    }

    //Initialization data csv
    @Override
    public Status initDataSource() {
        try {
            List<Customer> listCustomer = new ArrayList<>();
            List<Employee> listEmployee = new ArrayList<>();
            List<PlaceOfWork> listPlaceOfWorks = new ArrayList<>();
            List<Executor> listExecutor = new ArrayList<>();
            List<MechanicalMeasurement> listMechanicalMeasurement = new ArrayList<>();
            List<ElectricalMeasurement> listElectricalMeasurement = new ArrayList<>();
            List<HeadOfDepartment> listHeadOfDepartment = new ArrayList<>();
            List<Outfit> listOutfit = new ArrayList<>();
            List<Long> listEmployeeId = new ArrayList<>();
            listEmployeeId.add((long) 22);
            listEmployeeId.add((long) 23);

            listCustomer.add(createCustomer(10, "Alex"));
            listCustomer.add(createCustomer(11, "Anna"));
            listCustomer.add(createCustomer(12, "John"));
            listCustomer.add(createCustomer(13, "Jack"));
            listCustomer.add(createCustomer(14, "Matthew"));

            listEmployee.add(createEmployee(20, "Den", TypeEmployee.JUNIOR_EMPLOYEE, 12000));
            listEmployee.add(createEmployee(21, "Nate", TypeEmployee.SENIOR_EMPLOYEE, 11000));
            listEmployee.add(createEmployee(22, "Vlad", TypeEmployee.JUNIOR_EMPLOYEE, 12400));
            listEmployee.add(createEmployee(23, "Vova", TypeEmployee.SENIOR_EMPLOYEE, 13600));
            listEmployee.add(createEmployee(24, "Ira", TypeEmployee.JUNIOR_EMPLOYEE, 12200));

            listPlaceOfWorks.add(createPlaceOfWork(30, "VNII Gradient"));
            listPlaceOfWorks.add(createPlaceOfWork(31, "RNIIRS"));
            listPlaceOfWorks.add(createPlaceOfWork(32, "Russian Helicopter Holding"));
            listPlaceOfWorks.add(createPlaceOfWork(33, "Gazprom space systems"));
            listPlaceOfWorks.add(createPlaceOfWork(34, "Sri of space optics"));

            listExecutor.add(createExecutor(60, "79999999999"));
            listExecutor.add(createExecutor(61, "79999999998"));
            listExecutor.add(createExecutor(62, "79999999997"));
            listExecutor.add(createExecutor(63, "79999999996"));
            listExecutor.add(createExecutor(64, "79999999995"));

            listMechanicalMeasurement.add(createMechanicalMeasurement(40, "Calipers", 0.01, "P003"));
            listMechanicalMeasurement.add(createMechanicalMeasurement(41, "Micrometer", 0.01, "U004"));
            listMechanicalMeasurement.add(createMechanicalMeasurement(42, "Probes", 0.011, "OOO"));
            listMechanicalMeasurement.add(createMechanicalMeasurement(43, "Lever brackets", 0.012, "UI0"));

            listElectricalMeasurement.add(createElectricalMeasurement(44, "Inductive appliances", 0.001, "U000", 3.5));
            listElectricalMeasurement.add(createElectricalMeasurement(45, "Round meters", 0.001, "U001", 4));
            listElectricalMeasurement.add(createElectricalMeasurement(46, "Profilers", 0.002, "U002", 4.5));

            listHeadOfDepartment.add(createHeadOfDepartment(50, "Micha", listExecutor.get(0), 30000));
            listHeadOfDepartment.add(createHeadOfDepartment(51, "Nicola", listExecutor.get(1), 35000));
            listHeadOfDepartment.add(createHeadOfDepartment(52, "Dima", listExecutor.get(2), 28000));
            listHeadOfDepartment.add(createHeadOfDepartment(53, "Artem", listExecutor.get(3), 43000));
            listHeadOfDepartment.add(createHeadOfDepartment(54, "Nasta", listExecutor.get(4), 39000));

            listOutfit.add(createNewOutfit(1, 10, 61, 30, listEmployeeId, StatusWork.ESTABLISHED, new Date(), 42, TypeOfWork.MEASUREMENT_METHOD_CERTIFICATION));
            listOutfit.add(createNewOutfit(2, 10, 61, 30, listEmployeeId, StatusWork.ESTABLISHED, new Date(), 44, TypeOfWork.MEASUREMENT_METHOD_CERTIFICATION));
            listOutfit.add(createNewOutfit(3, 10, 61, 30, listEmployeeId, StatusWork.ESTABLISHED, new Date(), 45, TypeOfWork.MEASUREMENT_METHOD_CERTIFICATION));
            listOutfit.add(createNewOutfit(4, 10, 61, 30, listEmployeeId, StatusWork.ESTABLISHED, new Date(), 46, TypeOfWork.MEASUREMENT_METHOD_CERTIFICATION));

            setRecordsToXmlFile(listCustomer);
            setRecordsToXmlFile(listExecutor);
            setRecordsToXmlFile(listEmployee);
            setRecordsToXmlFile(listPlaceOfWorks);
            setRecordsToXmlFile(listMechanicalMeasurement);
            setRecordsToXmlFile(listElectricalMeasurement);
            setRecordsToXmlFile(listHeadOfDepartment);
            setRecordsToXmlFile(listOutfit);

        } catch (Exception e) {
            log.error(Constants.ERROR_INITIALIZATION_DATA_CSV);
            log.error(e);
            System.exit(-1);
        }
        return Status.SUCCESS;
    }

    private Customer createCustomer(long id, String name) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }

    private Employee createEmployee(long id, String name, TypeEmployee typeEmployee, double salary) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setTypeEmployee(typeEmployee);
        employee.setSalary(salary);
        return employee;
    }

    private Executor createExecutor(long id, String phoneNumber) {
        Executor executor = new Executor();
        executor.setId(id);
        executor.setPhoneNumber(phoneNumber);
        return executor;
    }

    private PlaceOfWork createPlaceOfWork(long id, String name) {
        PlaceOfWork placeOfWork = new PlaceOfWork();
        placeOfWork.setId(id);
        placeOfWork.setName(name);
        return placeOfWork;
    }

    private ElectricalMeasurement createElectricalMeasurement(long id, String name, double measurementError,
                                                              String model, double power) {
        ElectricalMeasurement electricalMeasurement = new ElectricalMeasurement();
        electricalMeasurement.setId(id);
        electricalMeasurement.setName(name);
        electricalMeasurement.setMeasurementError(measurementError);
        electricalMeasurement.setModel(model);
        electricalMeasurement.setPower(power);
        return electricalMeasurement;
    }

    private MechanicalMeasurement createMechanicalMeasurement(long id, String name, double measurementError,
                                                              String model) {
        MechanicalMeasurement mechanicalMeasurement = new MechanicalMeasurement();
        mechanicalMeasurement.setId(id);
        mechanicalMeasurement.setName(name);
        mechanicalMeasurement.setMeasurementError(measurementError);
        mechanicalMeasurement.setModel(model);
        return mechanicalMeasurement;
    }

    private HeadOfDepartment createHeadOfDepartment(long id, String name, Executor executor, double salary) {
        HeadOfDepartment headOfDepartment = new HeadOfDepartment();
        headOfDepartment.setId(id);
        headOfDepartment.setName(name);
        headOfDepartment.setExecutor(executor);
        headOfDepartment.setSalary(salary);
        return headOfDepartment;
    }

    private Outfit createNewOutfit(long id, long idCustomer, long idExecutor, long idPlaceWork, List<Long> idEmployees, StatusWork statusWork, Date dateStartWork, long idTypeMeasurementInstrument, TypeOfWork typeOfWork) throws Exception {
        Outfit outfit = new Outfit();
        outfit.setId(id);
        List<Customer> customers = getRecordsFromXmlFile(Customer.class);
        Customer customer = customers.stream().filter(el -> el.getId() == idCustomer).findFirst().get();
        outfit.setCustomer(customer);
        List<Executor> executors = getRecordsFromXmlFile(Executor.class);
        Executor executor = executors.stream().filter(el -> el.getId() == idExecutor).findFirst().get();
        outfit.setExecutor(executor);
        List<Employee> employees = getRecordsFromXmlFile(Employee.class);
        List<Employee> employeesOufit = getEmployeesOutfit(idEmployees, employees);
        outfit.setEmployees(employeesOufit);
        outfit.setStatusWork(statusWork);
        outfit.setDateStartWork(dateStartWork);
        List<MeansOfMeasurement> meansOfMeasurements = getRecordsFromXmlFile(MechanicalMeasurement.class);
        meansOfMeasurements.addAll(getRecordsFromXmlFile(ElectricalMeasurement.class));
        MeansOfMeasurement meansOfMeasurement = meansOfMeasurements.stream().filter(el -> el.getId() == idTypeMeasurementInstrument).findFirst().get();
        outfit.setMeasurementInstrument(meansOfMeasurement);
        outfit.setTypeWork(typeOfWork);
        List<PlaceOfWork> places = getRecordsFromXmlFile(PlaceOfWork.class);
        PlaceOfWork place = places.stream().filter(el -> el.getId() == idPlaceWork).findFirst().get();
        outfit.setPlaceWork(place);
        return outfit;
    }

    public <T> void setRecordsToXmlFile(List<T> list) throws Exception {
        FileWriter writer = null;
        try {
            (new File(PATH + list.get(0).getClass().getSimpleName().toLowerCase() + FILE_EXTENTION)).createNewFile();
            writer = new FileWriter(PATH + list.get(0).getClass().getSimpleName().toLowerCase() + FILE_EXTENTION, false);
            Serializer serializer = new Persister();
            WrapperXML<T> xml = new WrapperXML<T>();
            xml.setList(list);
            serializer.write(xml, writer);
        } catch (Exception e) {
            log.error(Constants.ERROR_FAILED_WRITE_DATA);
            log.error(e);
        } finally {
            writer.close();
        }
    }

    public <T> List<T> getRecordsFromXmlFile(Class className) throws Exception {
        WrapperXML xml = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(PATH + className.getSimpleName().toLowerCase() + FILE_EXTENTION);
            Serializer serializer = new Persister();
            xml = serializer.read(WrapperXML.class, fileReader);
            if (xml.getList() == null)
                xml.setList(new ArrayList<T>());
        } catch (Exception e) {
            log.error(Constants.ERROR_FAILED_GET_DATA);
            log.error(e);
        } finally {
            fileReader.close();
        }
        return xml.getList();
    }

    public List<Employee> getEmployeesOutfit(List<Long> idEmployees, List<Employee> listEmployee) {
        List<Employee> employees = new ArrayList<>();
        try {
            employees = listEmployee.stream()
                    .filter(i -> Collections.frequency(idEmployees, i.getId()) > 0)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            log.error(e);
        }
        return employees;
    }

}