package ru.sfedu.aems.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.beans.*;
import ru.sfedu.aems.dataConvertors.ListIdConvertor;
import ru.sfedu.aems.enums.*;
import ru.sfedu.aems.utils.ConfigurationUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


public class DataProviderJDBC implements DataProvider{
    private final static Logger log = LogManager.getLogger(DataProviderJDBC.class);
    private final String DB_URL = ConfigurationUtil.getConfigurationEntry("db_url");
    private final String DB_Driver = ConfigurationUtil.getConfigurationEntry("db_driver");
    private final String DB_USER = ConfigurationUtil.getConfigurationEntry("db_user");
    private final String DB_PASSWORD = ConfigurationUtil.getConfigurationEntry("db_password");
    private final String DB_INIT_PATH = ConfigurationUtil.getConfigurationEntry("db_init_path");
    private final String DB_INIT_FILE_NAME = ConfigurationUtil.getConfigurationEntry("db_ini_file_name");


    public DataProviderJDBC() throws IOException {
    }

    //API
    @Override
    public Status outfit(String operation, long idOutfit, List<Long> idEmployees, Date dateStartWork, long idCustomer, long idHeadOfDepartment, long idPlaceWork, String typeWork, String typeInstrument, long idTypeMeasurementInstrument, StatusWork statusWork) {
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
            TypeOfWork typeOfWork = chooseTypeOfWork(typeWork);
            TypeMeasurementInstrument typeMeasurementInstrument = chooseTypeMeasurementInstrument(typeInstrument);
            List<Employee> listEmployeeFromCsv = getListEmployee(Employee.class);
            List getHeadDepartment = findExistsHeadOfDepartmentId(idHeadOfDepartment);
            List getEmployeesOutfit = findExistsEmployeeId(idEmployees);
            List getCustomerOutfit = findExistsCustomerId(idCustomer);
            List getPlaceWorkInOutfit = findPlaceWorkId(idPlaceWork);
            List getMeasuremantInstrument = checkIdTypeMeasurementInstrument(typeMeasurementInstrument, idTypeMeasurementInstrument);
            if ((boolean) getHeadDepartment.get(0) || (boolean) getEmployeesOutfit.get(0) || (boolean) getCustomerOutfit.get(0) || (boolean) getPlaceWorkInOutfit.get(0) || checkIdEmployeesNotNull(idEmployees) || checkTypeOfWorkNotNull(typeOfWork) || checkTypeMeasurementInstrumentNotNull(typeMeasurementInstrument) || checkCountEmployees(idEmployees) || checkAllEmployeesId(listEmployeeFromCsv, idEmployees) || (boolean) getMeasuremantInstrument.get(0)) {
                return Arrays.asList(false, null);
            }
            List<Outfit> listOutfitFromCsv = getListOutfit(Outfit.class);
            if (!checkForDuplicatesOutfitId(listOutfitFromCsv, idOutfit)) {
                log.error(Constants.ERROR_ID_OUTFIT_EXISTS);
                return Arrays.asList(false, null);
            }
            if (checkForDuplicatesEmployeeId(idEmployees)) {
                log.error(Constants.ERROR_DUPLICATES_EMPLOYEE_ID);
                return Arrays.asList(false, null);
            }
            List<HeadOfDepartment> listHeadOfDepartment = getListHeadOfDepartment(HeadOfDepartment.class);
            HeadOfDepartment headOfDepartment = listHeadOfDepartment.stream()
                    .filter(e -> e.getId() == idHeadOfDepartment).findFirst().get();
            Executor executor = headOfDepartment.getExecutor();
            outfit = new Outfit();
            outfit.setId(idOutfit);
            outfit.setDateStartWork(dateStartWork);
            outfit.setCompletionDate(new Date(0));
            outfit.setCustomer((Customer) getCustomerOutfit.get(1));
            outfit.setExecutor(executor);
            outfit.setPlaceWork((PlaceOfWork) getPlaceWorkInOutfit.get(1));
            outfit.setTypeWork(typeOfWork);
            outfit.setMeasurementInstrument((MeansOfMeasurement) getMeasuremantInstrument.get(1));
            outfit.setEmployees((List<Employee>) getEmployeesOutfit.get(1));
            outfit.setStatusWork(statusWork);
            log.debug(outfit.toString());
            listOutfitFromCsv.add(outfit);
            insertRecordsToDb(Arrays.asList(outfit));
            log.info(Constants.CREATE_OUTFIT_SUCCESSFUL);
        } catch (Exception e) {
            log.error(Constants.ERROR_CREATE_OUTFIT);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(outfit));
    }

    @Override
    public boolean deleteOutfit(long idOutfit, long idHeadOfDepartment){
        try {
            List<Outfit> listOutfitFromDb = getListOutfit(Outfit.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || findExistsOutfitId(idOutfit) || checkOutfitsNotNull(listOutfitFromDb)) {
                return false;
            }
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String query = String.format(Constants.QUERY_DELETE_BY_CONDITION, listOutfitFromDb.get(0).getClass().getSimpleName(), idOutfit);
            statement.executeUpdate(query);
            statement.close();
            connection.close();
            log.info(Constants.DELETE_OUTFIT_SUCCESSFUL);
        }catch (Exception e){
            log.error(Constants.ERROR_DELETE_OUTFIT);
            log.error(e);
            return false;
        }
        return true;
    }

    @Override
    public List<Object> editOutfit(long idOutfit, List<Long> idEmployees, long idHeadOfDepartment) {
        Outfit outfit;
        try {
            List<Outfit> listOutfitFromDb = getListOutfit(Outfit.class);
            List<Employee> listEmployeeFromDb = getListEmployee(Employee.class);
            List getEmployeesOutfit = findExistsEmployeeId(idEmployees);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || findExistsOutfitId(idOutfit) || (boolean) getEmployeesOutfit.get(0) || checkIdEmployeesNotNull(idEmployees) || checkOutfitsNotNull(listOutfitFromDb) || checkCountEmployees(idEmployees) || checkAllEmployeesId(listEmployeeFromDb, idEmployees)) {
                return Arrays.asList(false, null);
            }
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String query = String.format(Constants.QUERY_UPDATE_RECORD_OUTFIT, listOutfitFromDb.get(0).getClass().getSimpleName(), ListIdConvertor.listToString(idEmployees), idOutfit);
            statement.executeUpdate(query);
            statement.close();
            connection.close();
            log.info(Constants.EDIT_OUTFIT_SUCCESSFUL);
            outfit = listOutfitFromDb.stream()
                    .filter(i -> i.getId() == idOutfit)
                    .findFirst()
                    .get();
        } catch (Exception e) {
            log.error(Constants.ERROR_EDIT_OUTFIT);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, outfit);
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
            List<Outfit> listOutfitFromDb = getListOutfit(Outfit.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean)findExistsEmployeeId(Arrays.asList(idAnalysis)).get(0) || checkOutfitsNotNull(listOutfitFromDb)) {
                return Arrays.asList(false, null);
            }
            listEmployeeIdOutfits = listOutfitFromDb.stream()
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
            List<Outfit> listOutfitFromDb = getListOutfit(Outfit.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || findExistsExecutorId(idAnalysis) || checkOutfitsNotNull(listOutfitFromDb)) {
                return Arrays.asList(false, null);
            }
            listExecutorInOutfits = listOutfitFromDb.stream()
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
            List<Employee> listEmployeeFromDb = getListEmployee(Employee.class);
            if (!(boolean) checkForDuplicatesEmployeeId(Arrays.asList(idEmployee), listEmployeeFromDb).get(0)) {
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
            insertRecordsToDb(Arrays.asList(employee));
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
        Employee changeEmployee;
        try {
            List<Employee> listEmployeeFromDb = getListEmployee(Employee.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean) findExistsEmployeeId(Arrays.asList(idEmployee)).get(0) || checkEmployeesNotNull(listEmployeeFromDb)) {
                return Arrays.asList(false, null);
            }
            changeEmployee = listEmployeeFromDb.stream()
                    .filter(e -> e.getId() == idEmployee).findFirst().get();
            if (changeEmployee.getTypeEmployee() == TypeEmployee.SENIOR_EMPLOYEE) {
                log.error(Constants.ERROR_IMPOSSIBLE_PROMOTE_EMPLOYEE);
                return Arrays.asList(false, null);
            }
            changeEmployee.setTypeEmployee(TypeEmployee.SENIOR_EMPLOYEE);
            changeEmployee = increaseSalaryEmployee(changeEmployee);
            log.debug(changeEmployee.toString());
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String query = String.format(Constants.QUERY_UPDATE_RECORD_EMPLOYEE, listEmployeeFromDb.get(0).getClass().getSimpleName(), changeEmployee.getSalary(), changeEmployee.getTypeEmployee(), idEmployee);
            statement.executeUpdate(query);
            statement.close();
            connection.close();
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
        Employee changeEmployee;
        try {
            List<Employee> listEmployeeFromDb = getListEmployee(Employee.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean)findExistsEmployeeId(Arrays.asList(idEmployee)).get(0) || checkEmployeesNotNull(listEmployeeFromDb)) {
                return Arrays.asList(false, null);
            }
            changeEmployee = listEmployeeFromDb.stream()
                    .filter(e -> e.getId() == idEmployee).findFirst().get();
            if (changeEmployee.getTypeEmployee() == TypeEmployee.JUNIOR_EMPLOYEE) {
                log.error(Constants.ERROR_IMPOSSIBLE_DEMOTE_EMPLOYEE);
                return Arrays.asList(false, null);
            }
            changeEmployee.setTypeEmployee(TypeEmployee.JUNIOR_EMPLOYEE);
            changeEmployee = lowerSalaryEmployee(changeEmployee);
            log.debug(changeEmployee.toString());
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String query = String.format(Constants.QUERY_UPDATE_RECORD_EMPLOYEE, listEmployeeFromDb.get(0).getClass().getSimpleName(), changeEmployee.getSalary(), changeEmployee.getTypeEmployee(), idEmployee);
            statement.executeUpdate(query);
            statement.close();
            connection.close();
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
        try {
            List<Employee> listEmployeeFromDb = getListEmployee(Employee.class);
            if ((boolean) findExistsHeadOfDepartmentId(idHeadOfDepartment).get(0) || (boolean) findExistsEmployeeId(Arrays.asList(idEmployee)).get(0) || checkEmployeesNotNull(listEmployeeFromDb)) {
                return false;
            }
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String query = String.format(Constants.QUERY_DELETE_BY_CONDITION, listEmployeeFromDb.get(0).getClass().getSimpleName(), idEmployee);
            statement.executeUpdate(query);
            statement.close();
            connection.close();
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
                    if ((boolean)finishWork(idOutfit, idExecutor).get(0)) {
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
        Outfit changeOutfit;
        try {
            List<Outfit> listOutfitFromDb = getListOutfit(Outfit.class);
            if (findExistsOutfitId(idOutfit) || findExistsExecutorId(idExecutor) || checkOutfitsNotNull(listOutfitFromDb)) {
                return Arrays.asList(false, null);
            }
            changeOutfit = listOutfitFromDb.stream()
                    .filter(e -> e.getId() == idOutfit).findFirst().get();
            if (changeOutfit.getExecutor().getId() != idExecutor) {
                log.error(Constants.ERROR_OUTFIT_HAS_DIFFERENT_EXECUTOR);
                return Arrays.asList(false, null);
            }
            changeOutfit.setStatusWork(StatusWork.COMPLETED);
            changeOutfit.setCompletionDate(new Date());
            log.debug(changeOutfit.toString());
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String query = String.format(Constants.QUERY_FINISH_OUTFIT, listOutfitFromDb.get(0).getClass().getSimpleName(), changeOutfit.getStatusWork(), changeOutfit.getCompletionDate(), idOutfit);
            statement.executeUpdate(query);
            statement.close();
            connection.close();
            log.info(Constants.FINISH_WORK_SUCCESSFUL);
        }catch (Exception e) {
            log.error(Constants.ERROR_FINISH_WORK);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(changeOutfit));
    }

    @Override
    public List<Object> changeStatusToInWork(long idOutfit, long idExecutor) {
        Outfit changeOutfit;
        try {
            List<Outfit> listOutfitFromDb = getListOutfit(Outfit.class);
            if (findExistsOutfitId(idOutfit) || findExistsExecutorId(idExecutor) || checkOutfitsNotNull(listOutfitFromDb)) {
                return Arrays.asList(false, null);
            }
            changeOutfit = listOutfitFromDb.stream()
                    .filter(e -> e.getId() == idOutfit).findFirst().get();
            if (changeOutfit.getExecutor().getId() != idExecutor) {
                log.error(Constants.ERROR_OUTFIT_HAS_DIFFERENT_EXECUTOR);
                return Arrays.asList(false, null);
            }
            changeOutfit.setStatusWork(StatusWork.IN_WORK);
            log.debug(changeOutfit.toString());
            Connection connection = getConnection();
            Statement statement = connection.createStatement();
            String query = String.format(Constants.QUERY_CHANGE_STATUS_TO_IN_WORK, listOutfitFromDb.get(0).getClass().getSimpleName(), changeOutfit.getStatusWork(), idOutfit);
            statement.executeUpdate(query);
            statement.close();
            connection.close();
            log.info(Constants.CHANGE_STATUS_TO_IN_WORK_SUCCESSFUL);
        }catch (Exception e) {
            log.error(Constants.ERROR_CHANGE_STATUS_TO_IN_WORK);
            log.error(e);
            return Arrays.asList(false, null);
        }
        return Arrays.asList(true, Optional.of(changeOutfit));
    }

    //Helper methods
    public static TypeEmployee chooseTypeEmployee(String typeEmployee) {
        try {
            switch (typeEmployee.split(Constants.UNDERSCORE)[0].toUpperCase()) {
                case Constants.JUNIOR_EMPLOYEE:
                    return TypeEmployee.JUNIOR_EMPLOYEE;
                case Constants.SENIOR_EMPLOYEE:
                    return TypeEmployee.SENIOR_EMPLOYEE;
            }
        } catch (Exception e) {
            log.error(e);
        }
        log.error(Constants.ERROR_CHOOSE_TYPE_EMPLOYEE);
        return null;
    }

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

    public static StatusWork chooseStatusWork(String statusWork) {
        try {
            switch (statusWork.toUpperCase()) {
                case Constants.COMPLETED:
                    return StatusWork.COMPLETED;
                case Constants.ESTABLISHED:
                    return StatusWork.ESTABLISHED;
                case Constants.IN_WORK:
                    return StatusWork.IN_WORK;
            }
        } catch (Exception e) {
            log.error(e);
        }
        log.error(Constants.ERROR_CHOOSE_STATUS_WORK);
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

    private List<Object> findExistsHeadOfDepartmentId(long idHeadOfDepartment) throws SQLException {
        List<HeadOfDepartment> listHeadOfDepartment = getListHeadOfDepartment(HeadOfDepartment.class);
        List checkForDuplicatesHeadDepartmentId = checkForDuplicatesHeadOfDepartmentId(listHeadOfDepartment, idHeadOfDepartment);
        if ((boolean) checkForDuplicatesHeadDepartmentId.get(0)) {
            log.error(Constants.ERROR_HEAD_DEPARTMENT_ID_NOT_FOUND);
            return Arrays.asList(true, checkForDuplicatesHeadDepartmentId.get(1));
        }
        return Arrays.asList(false, checkForDuplicatesHeadDepartmentId.get(1));
    }

    private boolean findExistsExecutorId(long idExecutor) throws SQLException, ClassNotFoundException {
        List<Executor> executors = getListExecutor(Executor.class);
        Executor executor = executors.stream().filter(e -> e.getId() == idExecutor)
                .findFirst()
                .get();
        return executor == null;
    }

    private boolean findExistsOutfitId(long idOutfit) throws SQLException {
        List<Outfit> listOutfit = getListOutfit(Outfit.class);
        if (checkForDuplicatesOutfitId(listOutfit, idOutfit)) {
            log.error(Constants.ERROR_OUTFIT_ID_NOT_FOUND);
            return true;
        }
        return false;
    }

    private List<Object> findPlaceWorkId(long idPlaceWork) throws SQLException {
        List<PlaceOfWork> listPlaceWork = getListPlaceOfWork(PlaceOfWork.class);
        List placeWorkInOutfit = checkForDuplicatesPlaceOfWorkId(listPlaceWork, idPlaceWork);
        if ((boolean) placeWorkInOutfit.get(0)) {
            log.error(Constants.ERROR_PLACE_OF_WORK_ID_NOT_FOUND);
            return Arrays.asList(true, placeWorkInOutfit.get(1));
        }
        return Arrays.asList(false, placeWorkInOutfit.get(1));
    }

    private List<Object> findExistsCustomerId(long idCustomer) throws IOException, SQLException {
        List<Customer> listCustomer = getListCustomer(Customer.class);
        List customerInOutfit = checkForDuplicatesCustomerId(listCustomer, idCustomer);
        if ((boolean) customerInOutfit.get(0)) {
            log.error(Constants.ERROR_CUSTOMER_ID_NOT_FOUND);
            return Arrays.asList(true, customerInOutfit.get(1));
        }
        return Arrays.asList(false, customerInOutfit.get(1));
    }

    private List<Object> findExistsEmployeeId(List<Long> idEmployee) throws SQLException {
        List<Employee> listEmployee = getListEmployee(Employee.class);
        List getDuplicatesEmployee = checkForDuplicatesEmployeeId(idEmployee, listEmployee);
        if ((boolean) getDuplicatesEmployee.get(0)) {
            log.error(Constants.ERROR_EMPLOYEE_ID_NOT_FOUND);
            return Arrays.asList(true, getDuplicatesEmployee.get(1));
        }
        return Arrays.asList(false, getDuplicatesEmployee.get(1));
    }

    private boolean checkIdEmployeeInIdsEmployees(List<Employee> employees, long idEmployee) {
        List<Employee> dublicateId = employees.stream()
                .filter(i -> i.getId() == idEmployee)
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

    private List<Object> checkForDuplicatesPlaceOfWorkId(List<PlaceOfWork> list, long id) {
        List<PlaceOfWork> dublicateId = list.stream()
                .filter(e -> e.getId() == id)
                .collect(Collectors.toList());
        boolean flag = dublicateId.isEmpty();
        return Arrays.asList(flag, dublicateId.get(0));
    }

    private List<Object> checkForDuplicatesEmployeeId(List<Long> idEmployees, List<Employee> listEmployee) {
        List<Employee> dublicateId = listEmployee.stream()
                .filter(i -> Collections.frequency(idEmployees, i.getId()) > 0)
                .collect(Collectors.toList());
        boolean flag = dublicateId.isEmpty();
        return Arrays.asList(flag, dublicateId);
    }

    private List<Object> checkForDuplicatesCustomerId(List<Customer> list, long id) {
        List<Customer> dublicateId = list.stream()
                .filter(e -> e.getId() == id)
                .collect(Collectors.toList());
        boolean flag = dublicateId.isEmpty();
        return Arrays.asList(flag, dublicateId.get(0));
    }

    private boolean checkForDuplicatesEmployeeId(List<Long> idEmployees) {
        List<Long> dublicateId = idEmployees.stream()
                .filter(i -> Collections.frequency(idEmployees, i) > 1)
                .collect(Collectors.toList());
        return !dublicateId.isEmpty();
    }

    private List<Object> checkIdTypeMeasurementInstrument(TypeMeasurementInstrument typeInstrument, long id) {
        try {
            List dublicateId;
            MeansOfMeasurement meansOfMeasurement;
            if (typeInstrument == TypeMeasurementInstrument.ELECTRICAL) {
                List<ElectricalMeasurement> listElectricalMeasurementFromDb = getListElectricalMeasurement(ElectricalMeasurement.class);
                dublicateId = listElectricalMeasurementFromDb.stream()
                        .filter(e -> e.getId() == id)
                        .collect(Collectors.toList());
                boolean flag = dublicateId.isEmpty();
                meansOfMeasurement =(MeansOfMeasurement) dublicateId.get(0);
                return Arrays.asList(flag, meansOfMeasurement);
            } else if (typeInstrument == TypeMeasurementInstrument.MECHANICAL) {
                List<MechanicalMeasurement> listMechanicalMeasurementFromDb = getListMechanicalMeasurement(MechanicalMeasurement.class);
                dublicateId = listMechanicalMeasurementFromDb.stream()
                        .filter(e -> e.getId() == id)
                        .collect(Collectors.toList());
                boolean flag = dublicateId.isEmpty();
                meansOfMeasurement =(MeansOfMeasurement) dublicateId.get(0);
                return Arrays.asList(flag, meansOfMeasurement);
            }
        }catch (Exception e){
            log.error(Constants.ERROR_TYPE_MEASUREMENT_INSTRUMENT);
            log.error(e);
        }
        log.error(Constants.ERROR_TYPE_MEASUREMENT_INSTRUMENT);
        return Arrays.asList(true, null);
    }

    private boolean checkTypeOfWorkNotNull(TypeOfWork typeOfWork) {
        try {
            return typeOfWork == null;
        }catch (NullPointerException e){
            log.error(Constants.ERROR_CHOOSE_TYPE_OF_WORK);
            return true;
        }
    }

    private boolean checkTypeMeasurementInstrumentNotNull(TypeMeasurementInstrument typeMeasurementInstrument) {
        try {
            return typeMeasurementInstrument == null;
        }catch (NullPointerException e){
            log.error(Constants.ERROR_CHOOSE_TYPE_MEASUREMENT_INSTRUMENT);
            return true;
        }
    }

    private boolean checkOutfitsNotNull(List<Outfit> listOutfit) {
        try {
            return listOutfit.isEmpty();
        }catch (NullPointerException e){
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

    private boolean checkForDuplicatesOutfitId(List<Outfit> list, long id) {
        List<Outfit> dublicateId = list.stream()
                .filter(e -> e.getId() == id)
                .collect(Collectors.toList());
        return dublicateId.isEmpty();
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

    public boolean checkIdEmployeesNotNull(List<Long> idEmployees) {
        try{
            return idEmployees.isEmpty();
        }catch (NullPointerException e){
            log.error(Constants.ERROR_EMPLOYEE_ID_NOT_FOUND);
            return true;
        }
    }


    private boolean checkEmployeesNotNull(List<Employee> listEmployee) {
        try {
            return listEmployee.isEmpty();
        }catch (NullPointerException e){
            log.error(Constants.ERROR_NOT_DATA_EMPLOYEE);
            return true;
        }
    }

    public void deleteRecordsInTable(Class className)throws SQLException{
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            String query = String.format(Constants.QUERY_DELETE_ALL_TABLE, className.getSimpleName());
            statement.executeUpdate(query);
        }catch (Exception e){
            log.error(e);
            System.exit(1);
        }finally {
            statement.close();
            connection.close();
        }
    }

    public <T> void insertRecordsToDb(List<T> list) throws IOException, SQLException {
        Connection connection = null;
        Statement statement = null;
        try{
            connection = getConnection();
            statement = connection.createStatement();
            for (Object bean : list) {
                String values = getValuesBean(bean, list.get(0).getClass());
                String query = String.format(Constants.QUERY_INSERT_TABLE, list.get(0).getClass().getSimpleName(), values);
                statement.executeUpdate(query);
            }
        }catch (Exception e){
            log.error(e);
            System.exit(1);
        }finally {
            statement.close();
            connection.close();
        }
    }

    private String getValuesBean(Object bean, Class className){
        try {
            switch (className.getSimpleName()) {
                case Constants.CUSTOMER:
                    return getValuesCustomer((Customer) bean);
                case Constants.EMPLOYEE:
                    return getValuesEmployee((Employee) bean);
                case Constants.HEAD_OF_DEPARTMENT:
                    return getValuesHeadOfDepartment((HeadOfDepartment) bean);
                case Constants.PLACE_OF_WORK:
                    return getValuesPlaceOfWork((PlaceOfWork) bean);
                case Constants.ELECTRICAL_MEASUREMENT:
                    return getValuesElectricalMeasurement((ElectricalMeasurement) bean);
                case Constants.EXECUTOR:
                    return getValuesExecutor((Executor) bean);
                case Constants.MECHANICAL_MEASUREMENT:
                    return getValuesMechanicalMeasurement((MechanicalMeasurement) bean);
                case Constants.OUTFIT_TABLE:
                    return getValuesOutfit((Outfit) bean);

            }
        }catch (Exception e){
            log.error(e);
        }
        log.info(className.getSimpleName());
        log.error(Constants.ERROR_GET_VALUES_BEAN);
        return "";
    }

    private String getValuesExecutor(Executor bean){
        return String.format("%d, '%s'", bean.getId(), bean.getPhoneNumber());
    }

    private String getValuesCustomer(Customer bean){
        return String.format("%d, '%s'", bean.getId(), bean.getName());
    }

    private String getValuesPlaceOfWork(PlaceOfWork bean){
        return String.format("%d, '%s'", bean.getId(), bean.getName());
    }

    private String getValuesEmployee(Employee bean){
        return String.format("%d, '%s', '%s', '%s'", bean.getId(), bean.getName(), bean.getSalary(), bean.getTypeEmployee());
    }

    private String getValuesHeadOfDepartment(HeadOfDepartment bean){
        return String.format("%d, '%s', '%s', %d", bean.getId(), bean.getName(), bean.getSalary(), bean.getExecutor().getId());
    }

    private String getValuesElectricalMeasurement(ElectricalMeasurement bean){
        return String.format("%d, '%s', '%s', '%s', '%s'", bean.getId(), bean.getName(), bean.getMeasurementError(), bean.getModel(), bean.getPower());
    }

    private String getValuesMechanicalMeasurement(MechanicalMeasurement bean){
        return String.format("%d, '%s', '%s', '%s'", bean.getId(), bean.getName(), bean.getMeasurementError(), bean.getModel());
    }

    private String getValuesOutfit(Outfit bean)  {
        List<Employee> employees = bean.getEmployees();
        List<Long> idEmployees = employees.stream().map(Employee::getId).collect(Collectors.toList());
        return String.format("%d, '%tF', '%tF', %d, %d, %d, %d, '%s', '%s', '%s'", bean.getId(), bean.getDateStartWork(), bean.getCompletionDate(), bean.getExecutor().getId(), bean.getPlaceWork().getId(), bean.getCustomer().getId(), bean.getMeasurementInstrument().getId(), bean.getTypeWork(), bean.getStatusWork(), ListIdConvertor.listToString(idEmployees));
    }

    public List<Customer> getListCustomer(Class chooseClass) throws NoSuchElementException, SQLException {
        Connection connection = null;
        Statement statement = null;
        List<Customer> listCustomer = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                Customer customer = new Customer();
                customer.setId(resultSet.getLong("id"));
                customer.setName(resultSet.getString("name"));
                listCustomer.add(customer);
            }
        }catch (Exception e){
            log.error(Constants.ERROR_GET_LIST_CUSTOMER);
            log.error(e);
        }finally {
            statement.close();
            connection.close();
        }
        return listCustomer;
    }

    public List<Employee> getListEmployee(Class chooseClass) throws NoSuchElementException, SQLException {
        Connection connection = null;
        Statement statement = null;
        List<Employee> listEmployee = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                Employee employee = new Employee();
                employee.setId(resultSet.getLong("id"));
                employee.setName(resultSet.getString("name"));
                employee.setSalary(resultSet.getDouble("salary"));
                employee.setTypeEmployee(chooseTypeEmployee(resultSet.getString("typeEmployee")));
                listEmployee.add(employee);
            }
        }catch (Exception e){
            log.error(Constants.ERROR_GET_LIST_EMPLOYEE);
            log.error(e);
        }finally {
            statement.close();
            connection.close();
        }
        return listEmployee;
    }

    public List<PlaceOfWork> getListPlaceOfWork(Class chooseClass) throws NoSuchElementException, SQLException {
        Connection connection = null;
        Statement statement = null;
        List<PlaceOfWork> listPlaceOfWork = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                PlaceOfWork placeOfWork = new PlaceOfWork();
                placeOfWork.setId(resultSet.getLong("id"));
                placeOfWork.setName(resultSet.getString("name"));
                listPlaceOfWork.add(placeOfWork);
            }
        }catch (Exception e){
            log.error(Constants.ERROR_GET_LIST_PLACE_OF_WORK);
            log.error(e);
        }finally {
            statement.close();
            connection.close();
        }
        return listPlaceOfWork;
    }

    public List<Executor> getListExecutor(Class chooseClass) throws NoSuchElementException {
        Connection connection;
        Statement statement;
        List<Executor> listExecutor = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                Executor executor = new Executor();
                executor.setId(resultSet.getLong("id"));
                executor.setPhoneNumber(resultSet.getString("phoneNumber"));
                listExecutor.add(executor);
            }
        }catch (Exception e){
            log.error(e);
        }
        return listExecutor;
    }

    public List<HeadOfDepartment> getListHeadOfDepartment(Class chooseClass) throws NoSuchElementException, SQLException {
        Connection connection = null;
        Statement statement = null;
        List<HeadOfDepartment> listHeadOfDepartment = new ArrayList<>();
        List<Long> idExecutors = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                HeadOfDepartment headOfDepartment = new HeadOfDepartment();
                headOfDepartment.setId(resultSet.getLong("id"));
                headOfDepartment.setName(resultSet.getString("name"));
                headOfDepartment.setSalary(resultSet.getDouble("salary"));
                idExecutors.add(resultSet.getLong("idExecutor"));
                listHeadOfDepartment.add(headOfDepartment);
            }
            statement.close();
            connection.close();
            for(int i = 0; i < listHeadOfDepartment.size(); i++){
                listHeadOfDepartment.get(i).setExecutor(findExecutorForHeadOfDepartment(idExecutors.get(i)));
            }
        }catch (Exception e){
            log.error(Constants.ERROR_GET_LIST_HEAD_OF_DEPARTMENT);
            log.error(e);
        }finally {
            statement.close();
            connection.close();
        }
        return listHeadOfDepartment;
    }

    public Executor findExecutorForHeadOfDepartment(long idExecutor) {
        Connection connection;
        Statement statement;
        Executor executor = new Executor();
        try{
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_BY_CONDITION, Executor.class.getSimpleName(), idExecutor));
            while (resultSet.next()) {
                executor.setId(resultSet.getLong("id"));
                executor.setPhoneNumber(resultSet.getString("phoneNumber"));
            }
        }catch (Exception e){
            log.error(e);
        }
        return executor;
    }

    public List<ElectricalMeasurement> getListElectricalMeasurement(Class chooseClass) throws NoSuchElementException, SQLException {
        Connection connection = null;
        Statement statement = null;
        List<ElectricalMeasurement> listElectricalMeasurement = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                ElectricalMeasurement electricalMeasurement = new ElectricalMeasurement();
                electricalMeasurement.setId(resultSet.getLong("id"));
                electricalMeasurement.setName(resultSet.getString("name"));
                electricalMeasurement.setMeasurementError(resultSet.getDouble("measurementError"));
                electricalMeasurement.setModel(resultSet.getString("model"));
                electricalMeasurement.setPower(resultSet.getDouble("power"));
                listElectricalMeasurement.add(electricalMeasurement);
            }
        }catch (Exception e){
            log.error(Constants.ERROR_GET_LIST_ELECTRICAL_MEASUREMENT);
            log.error(e);
        }finally {
            statement.close();
            connection.close();
        }
        return listElectricalMeasurement;
    }

    public List getListMechanicalMeasurement(Class chooseClass) throws NoSuchElementException, SQLException {
        Connection connection = null;
        Statement statement = null;
        List<MechanicalMeasurement> listMechanicalMeasurement = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                MechanicalMeasurement mechanicalMeasurement = new MechanicalMeasurement();
                mechanicalMeasurement.setId(resultSet.getLong("id"));
                mechanicalMeasurement.setName(resultSet.getString("name"));
                mechanicalMeasurement.setMeasurementError(resultSet.getDouble("measurementError"));
                mechanicalMeasurement.setModel(resultSet.getString("model"));
                listMechanicalMeasurement.add(mechanicalMeasurement);
            }
        }catch (Exception e){
            log.error(Constants.ERROR_GET_LIST_MECHANICAL_MEASUREMENT);
            log.error(e);
        }finally {
            statement.close();
            connection.close();
        }
        return listMechanicalMeasurement;
    }

    public List<Outfit> getListOutfit(Class chooseClass) throws NoSuchElementException{
        Connection connection;
        Statement statement;
        List<Outfit> listOutfit = new ArrayList<>();
        List<Long> idExecutors = new ArrayList<>();
        List<Long> idCustomers = new ArrayList<>();
        List<Long> idPlaceOfWork = new ArrayList<>();
        List<Long> idTypeMeasurementInstrument = new ArrayList<>();
        List<String> idEmployeesInOutfit = new ArrayList<>();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_ALL, chooseClass.getSimpleName()));
            while (resultSet.next()) {
                Outfit outfit = new Outfit();
                outfit.setId(resultSet.getLong("id"));
                outfit.setDateStartWork(resultSet.getDate("dateStartWork"));
                outfit.setCompletionDate(resultSet.getDate("completionDate"));
                idExecutors.add(resultSet.getLong("idExecutor"));
                idCustomers.add(resultSet.getLong("idCustomer"));
                idPlaceOfWork.add(resultSet.getLong("idPlaceWork"));
                idTypeMeasurementInstrument.add(resultSet.getLong("idMeansOfMeasurement"));
                idEmployeesInOutfit.add(resultSet.getString("idEmployees"));
                outfit.setTypeWork(chooseTypeOfWork(resultSet.getString("typeOfWork")));
                outfit.setStatusWork(chooseStatusWork(resultSet.getString("statusWork")));
                listOutfit.add(outfit);
            }
            statement.close();
            connection.close();
            for(int i = 0; i < listOutfit.size(); i++){
                listOutfit.get(i).setExecutor(findExecutorForHeadOfDepartment(idExecutors.get(i)));
                listOutfit.get(i).setCustomer(findCustomerForOutfit(idCustomers.get(i)));
                listOutfit.get(i).setPlaceWork(findPlaceOfWorkForOutfit(idPlaceOfWork.get(i)));
                listOutfit.get(i).setMeasurementInstrument(findMeasurementInstrumentForOutfit(idTypeMeasurementInstrument.get(i)));
                listOutfit.get(i).setEmployees(getEmployeeInOutfit(ListIdConvertor.stringToList(idEmployeesInOutfit.get(i))));
            }
        }catch (Exception e){
            log.error(Constants.ERROR_GET_LIST_OUTFIT);
            log.error(e);
        }
        return listOutfit;
    }

    private Customer findCustomerForOutfit(long idCustomer) {
        Connection connection;
        Statement statement;
        Customer customer = new Customer();
        try{
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_BY_CONDITION, Customer.class.getSimpleName(), idCustomer));
            while (resultSet.next()) {
                customer.setId(resultSet.getLong("id"));
                customer.setName(resultSet.getString("name"));
            }
        }catch (Exception e){
            log.error(e);
        }
        return customer;
    }

    private List<Employee> getEmployeeInOutfit(List<Long> idEmployees) throws SQLException {
        List<Employee> employees = new ArrayList<>();
        List<Employee> listEmployee = getListEmployee(Employee.class);
        try {
            employees = listEmployee.stream()
                    .filter(i -> Collections.frequency(idEmployees, i.getId()) > 0)
                    .collect(Collectors.toList());
        }catch (NullPointerException e){
            log.error(e);
        }
        return employees;
    }

    private PlaceOfWork findPlaceOfWorkForOutfit(long idPlaceOfWork){
        Connection connection;
        Statement statement;
        PlaceOfWork placeOfWork = new PlaceOfWork();
        try{
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format(Constants.QUERY_SELECT_BY_CONDITION, PlaceOfWork.class.getSimpleName(), idPlaceOfWork));
            while (resultSet.next()) {
                placeOfWork.setId(resultSet.getLong("id"));
                placeOfWork.setName(resultSet.getString("name"));
            }
        }catch (Exception e){
            log.error(e);
        }
        return placeOfWork;
    }

    private MeansOfMeasurement findMeasurementInstrumentForOutfit(long idTypeMeasurementInstrument) throws SQLException {
        List meansOfMeasurement = getListMechanicalMeasurement(MechanicalMeasurement.class);
        meansOfMeasurement.addAll(getListElectricalMeasurement(ElectricalMeasurement.class));
        List<MeansOfMeasurement> instruments = (List<MeansOfMeasurement>) meansOfMeasurement;
        return instruments.stream()
                .filter(i -> i.getId() == idTypeMeasurementInstrument)
                .findFirst()
                .get();
    }

    private static boolean validName(String name) {
        boolean result = name.matches("^[A-Za-z]{3,20}$");
        return result;
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DB_Driver);
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    //Initialization database
    @Override
    public Status initDataSource() throws SQLException, IOException, ClassNotFoundException {
        try {
            initDatabase();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        List<Customer> listCustomer = new ArrayList<>();
        List<Employee> listEmployee = new ArrayList<>();
        List<PlaceOfWork> listPlaceOfWorks = new ArrayList<>();
        List<Executor> listExecutor = new ArrayList<>();
        List<MechanicalMeasurement> listMechanicalMeasurement = new ArrayList<>();
        List<ElectricalMeasurement> listElectricalMeasurement = new ArrayList<>();
        List<HeadOfDepartment> listHeadOfDepartment = new ArrayList<>();
        List<Outfit> listOutfit = new ArrayList<>();
        List<Long> listEmployeeId = new ArrayList<>();
        listEmployeeId.add((long)22);
        listEmployeeId.add((long)23);

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


        try {
            deleteRecordsInTable(Customer.class);
            deleteRecordsInTable(Employee.class);
            deleteRecordsInTable(PlaceOfWork.class);
            deleteRecordsInTable(MechanicalMeasurement.class);
            deleteRecordsInTable(ElectricalMeasurement.class);
            deleteRecordsInTable(HeadOfDepartment.class);
            deleteRecordsInTable(Executor.class);
            deleteRecordsInTable(Outfit.class);

            insertRecordsToDb(listCustomer);
            insertRecordsToDb(listEmployee);
            insertRecordsToDb(listPlaceOfWorks);
            insertRecordsToDb(listMechanicalMeasurement);
            insertRecordsToDb(listElectricalMeasurement);
            insertRecordsToDb(listHeadOfDepartment);
            insertRecordsToDb(listExecutor);
            insertRecordsToDb(listOutfit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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

    private Executor createExecutor(long id, String phoneNumber){
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

    private Outfit createNewOutfit(long id, long idCustomer, long idExecutor, long idPlaceWork, List<Long> idEmployees, StatusWork statusWork, Date dateStartWork, long idTypeMeasurementInstrument, TypeOfWork typeOfWork) throws IOException, SQLException, ClassNotFoundException {
        Outfit outfit = new Outfit();
        outfit.setId(id);
        outfit.setCompletionDate(new Date(0));
        List<Customer> customers = getListCustomer(Customer.class);
        Customer customer =  customers.stream().filter(el -> el.getId() == idCustomer).findFirst().get();
        outfit.setCustomer(customer);
        List<Executor> executors = getListExecutor(Executor.class);
        Executor executor = executors.stream().filter(el -> el.getId() == idExecutor).findFirst().get();
        outfit.setExecutor(executor);
        List<Employee> employees = getListEmployee(Employee.class);
        List<Employee> employeesOufit = getEmployeesOutfit(idEmployees, employees);
        outfit.setEmployees(employeesOufit);
        outfit.setStatusWork(statusWork);
        outfit.setDateStartWork(dateStartWork);
        List<MeansOfMeasurement> meansOfMeasurements = getListMechanicalMeasurement(MechanicalMeasurement.class);
        meansOfMeasurements.addAll(getListElectricalMeasurement(ElectricalMeasurement.class));
        MeansOfMeasurement meansOfMeasurement = meansOfMeasurements.stream().filter(el -> el.getId() == idTypeMeasurementInstrument).findFirst().get();
        outfit.setMeasurementInstrument(meansOfMeasurement);
        outfit.setTypeWork(typeOfWork);
        List<PlaceOfWork> places = getListPlaceOfWork(PlaceOfWork.class);
        PlaceOfWork place = places.stream().filter(el -> el.getId() == idPlaceWork).findFirst().get();
        outfit.setPlaceWork(place);
        return outfit;
    }

    public List<Employee> getEmployeesOutfit(List<Long> idEmployees, List<Employee> listEmployee) {
        List<Employee> employees = new ArrayList<>();
        try {
            employees = listEmployee.stream()
                    .filter(i -> Collections.frequency(idEmployees, i.getId()) > 0)
                    .collect(Collectors.toList());
        }catch (NullPointerException e){
            log.error(e);
        }
        return employees;
    }

    public void initDatabase() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try{
            connection = getConnection();
            String dbCreate = new String(Files.readAllBytes(Paths.get(DB_INIT_PATH + DB_INIT_FILE_NAME)));
            statement = connection.createStatement();
            statement.executeUpdate(dbCreate);
        }catch (Exception e){
            log.error(e);
            System.exit(1);
        }finally {
            statement.close();
            connection.close();
        }
    }
}
