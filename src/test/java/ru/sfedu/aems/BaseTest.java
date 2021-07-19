package ru.sfedu.aems;

import ru.sfedu.aems.api.DataProviderCsv;
import ru.sfedu.aems.api.DataProviderJDBC;
import ru.sfedu.aems.api.DataProviderXML;
import ru.sfedu.aems.beans.*;
import ru.sfedu.aems.enums.StatusWork;
import ru.sfedu.aems.enums.TypeEmployee;
import ru.sfedu.aems.enums.TypeOfWork;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class BaseTest {
    public static Customer createCustomer(long id, String name){
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        return customer;
    }

    public static Employee createEmployee(long id, String name, TypeEmployee typeEmployee, double salary){
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setTypeEmployee(typeEmployee);
        employee.setSalary(salary);
        return employee;
    }

    public static PlaceOfWork createPlaceOfWork(long id, String name){
        PlaceOfWork placeOfWork = new PlaceOfWork();
        placeOfWork.setId(id);
        placeOfWork.setName(name);
        return placeOfWork;
    }

    public static ElectricalMeasurement createElectricalMeasurement(long id, String name, double measurementError,
                                                             String model, double power){
        ElectricalMeasurement electricalMeasurement = new ElectricalMeasurement();
        electricalMeasurement.setId(id);
        electricalMeasurement.setName(name);
        electricalMeasurement.setMeasurementError(measurementError);
        electricalMeasurement.setModel(model);
        electricalMeasurement.setPower(power);
        return electricalMeasurement;
    }

    public static MechanicalMeasurement createMechanicalMeasurement(long id, String name, double measurementError,
                                                             String model){
        MechanicalMeasurement mechanicalMeasurement = new MechanicalMeasurement();
        mechanicalMeasurement.setId(id);
        mechanicalMeasurement.setName(name);
        mechanicalMeasurement.setMeasurementError(measurementError);
        mechanicalMeasurement.setModel(model);
        return mechanicalMeasurement;
    }

    public static HeadOfDepartment createHeadOfDepartment(long id, String name, Executor executor, double salary){
        HeadOfDepartment headOfDepartment = new HeadOfDepartment();
        headOfDepartment.setId(id);
        headOfDepartment.setName(name);
        headOfDepartment.setExecutor(executor);
        headOfDepartment.setSalary(salary);
        return headOfDepartment;
    }

    public Executor createExecutor(long id, String phoneNumber){
        Executor executor = new Executor();
        executor.setId(id);
        executor.setPhoneNumber(phoneNumber);
        return executor;
    }

    public static Outfit createOutfit(long id, long idCustomer, long idExecutor, long idPlaceWork, List<Long> idEmployees, StatusWork statusWork, Date dateStartWork, long idTypeMeasurementInstrument, TypeOfWork typeOfWork) throws IOException {
        DataProviderCsv dataProviderCsv =  new DataProviderCsv();
        Outfit outfit = new Outfit();
        outfit.setId(id);
        List<Customer> customers = dataProviderCsv.getRecordsFromCsvFile(Customer.class);
        Customer customer =  customers.stream().filter(el -> el.getId() == idCustomer).findFirst().get();
        outfit.setCustomer(customer);
        List<Executor> executors = dataProviderCsv.getRecordsFromCsvFile(Executor.class);
        Executor executor = executors.stream().filter(el -> el.getId() == idExecutor).findFirst().get();
        outfit.setExecutor(executor);
        List<Employee> employees = dataProviderCsv.getRecordsFromCsvFile(Employee.class);
        List<Employee> employeesOufit = dataProviderCsv.getEmployeesOutfit(idEmployees, employees);
        outfit.setEmployees(employeesOufit);
        outfit.setStatusWork(statusWork);
        outfit.setDateStartWork(dateStartWork);
        List<MeansOfMeasurement> meansOfMeasurements = dataProviderCsv.getRecordsFromCsvFile(MechanicalMeasurement.class);
        meansOfMeasurements.addAll(dataProviderCsv.getRecordsFromCsvFile(ElectricalMeasurement.class));
        MeansOfMeasurement meansOfMeasurement = meansOfMeasurements.stream().filter(el -> el.getId() == idTypeMeasurementInstrument).findFirst().get();
        outfit.setMeasurementInstrument(meansOfMeasurement);
        outfit.setTypeWork(typeOfWork);
        List<PlaceOfWork> places = dataProviderCsv.getRecordsFromCsvFile(PlaceOfWork.class);
        PlaceOfWork place = places.stream().filter(el -> el.getId() == idPlaceWork).findFirst().get();
        outfit.setPlaceWork(place);
        return outfit;
    }

    public static Outfit createOutfitXml(long id, long idCustomer, long idExecutor, long idPlaceWork, List<Long> idEmployees, StatusWork statusWork, Date dateStartWork, long idTypeMeasurementInstrument, TypeOfWork typeOfWork) throws Exception {
        DataProviderXML provider =  new DataProviderXML();
        Outfit outfit = new Outfit();
        outfit.setId(id);
        List<Customer> customers = provider.getRecordsFromXmlFile(Customer.class);
        Customer customer =  customers.stream().filter(el -> el.getId() == idCustomer).findFirst().get();
        outfit.setCustomer(customer);
        List<Executor> executors = provider.getRecordsFromXmlFile(Executor.class);
        Executor executor = executors.stream().filter(el -> el.getId() == idExecutor).findFirst().get();
        outfit.setExecutor(executor);
        List<Employee> employees = provider.getRecordsFromXmlFile(Employee.class);
        List<Employee> employeesOufit = provider.getEmployeesOutfit(idEmployees, employees);
        outfit.setEmployees(employeesOufit);
        outfit.setStatusWork(statusWork);
        outfit.setDateStartWork(dateStartWork);
        List<MeansOfMeasurement> meansOfMeasurements = provider.getRecordsFromXmlFile(MechanicalMeasurement.class);
        meansOfMeasurements.addAll(provider.getRecordsFromXmlFile(ElectricalMeasurement.class));
        MeansOfMeasurement meansOfMeasurement = meansOfMeasurements.stream().filter(el -> el.getId() == idTypeMeasurementInstrument).findFirst().get();
        outfit.setMeasurementInstrument(meansOfMeasurement);
        outfit.setTypeWork(typeOfWork);
        List<PlaceOfWork> places = provider.getRecordsFromXmlFile(PlaceOfWork.class);
        PlaceOfWork place = places.stream().filter(el -> el.getId() == idPlaceWork).findFirst().get();
        outfit.setPlaceWork(place);
        return outfit;
    }

    public static Outfit createOutfit(long id, long idCustomer, long idExecutor, long idPlaceWork, List<Long> idEmployees, StatusWork statusWork, Date dateStartWork, long idTypeMeasurementInstrument, TypeOfWork typeOfWork, Date complecationWork) throws SQLException, IOException, ClassNotFoundException {
        DataProviderJDBC provider = new DataProviderJDBC();
        Outfit outfit = new Outfit();
        outfit.setId(id);
        outfit.setCompletionDate(new Date(0));
        List<Customer> customers = provider.getListCustomer(Customer.class);
        Customer customer =  customers.stream().filter(el -> el.getId() == idCustomer).findFirst().get();
        outfit.setCustomer(customer);
        List<Executor> executors = provider.getListExecutor(Executor.class);
        Executor executor = executors.stream().filter(el -> el.getId() == idExecutor).findFirst().get();
        outfit.setExecutor(executor);
        List<Employee> employees = provider.getListEmployee(Employee.class);
        List<Employee> employeesOufit = provider.getEmployeesOutfit(idEmployees, employees);
        outfit.setEmployees(employeesOufit);
        outfit.setStatusWork(statusWork);
        outfit.setDateStartWork(dateStartWork);
        List<MeansOfMeasurement> meansOfMeasurements = provider.getListMechanicalMeasurement(MechanicalMeasurement.class);
        meansOfMeasurements.addAll(provider.getListElectricalMeasurement(ElectricalMeasurement.class));
        MeansOfMeasurement meansOfMeasurement = meansOfMeasurements.stream().filter(el -> el.getId() == idTypeMeasurementInstrument).findFirst().get();
        outfit.setMeasurementInstrument(meansOfMeasurement);
        outfit.setTypeWork(typeOfWork);
        List<PlaceOfWork> places = provider.getListPlaceOfWork(PlaceOfWork.class);
        PlaceOfWork place = places.stream().filter(el -> el.getId() == idPlaceWork).findFirst().get();
        outfit.setPlaceWork(place);
        return outfit;
    }

}
