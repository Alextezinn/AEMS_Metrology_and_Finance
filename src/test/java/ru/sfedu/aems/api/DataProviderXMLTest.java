package ru.sfedu.aems.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.aems.BaseTest;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.beans.*;
import ru.sfedu.aems.enums.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataProviderXMLTest extends BaseTest {
    private final static Logger log = LogManager.getLogger(DataProviderXMLTest.class);
    public DataProviderXML provider = new DataProviderXML();

    public DataProviderXMLTest() throws IOException {
    }

    @Before
    public void dataInitTest(){
        try {
            provider.rewriteFile(Outfit.class);

            List<Customer> listCustomer = new ArrayList<>();
            List<Employee> listEmployee = new ArrayList<>();
            List<PlaceOfWork> listPlaceOfWorks = new ArrayList<>();
            List<MechanicalMeasurement> listMechanicalMeasurement = new ArrayList<>();
            List<ElectricalMeasurement> listElectricalMeasurement = new ArrayList<>();
            List<Executor> listExecutor = new ArrayList<>();
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

            listMechanicalMeasurement.add(createMechanicalMeasurement(40, "Calipers", 0.01, "X003"));
            listMechanicalMeasurement.add(createMechanicalMeasurement(41, "Micrometer", 0.01, "X000"));
            listMechanicalMeasurement.add(createMechanicalMeasurement(42, "Probes", 0.011, "X001"));
            listMechanicalMeasurement.add(createMechanicalMeasurement(43, "Lever brackets", 0.012, "X002"));

            listElectricalMeasurement.add(createElectricalMeasurement(44, "Inductive appliances", 0.001, "U000", 3.5));
            listElectricalMeasurement.add(createElectricalMeasurement(45, "Round meters", 0.001, "U001", 4));
            listElectricalMeasurement.add(createElectricalMeasurement(46, "Profilers", 0.002, "U002", 4.5));

            listHeadOfDepartment.add(createHeadOfDepartment(50, "Micha", listExecutor.get(0), 30000));
            listHeadOfDepartment.add(createHeadOfDepartment(51, "Nicola", listExecutor.get(1), 35000));
            listHeadOfDepartment.add(createHeadOfDepartment(52, "Dima", listExecutor.get(2), 28000));
            listHeadOfDepartment.add(createHeadOfDepartment(53, "Artem", listExecutor.get(3), 43000));
            listHeadOfDepartment.add(createHeadOfDepartment(54, "Nasta", listExecutor.get(4), 39000));

            listOutfit.add(createOutfitXml(1, 10, 61, 30, listEmployeeId, StatusWork.ESTABLISHED, new Date(), 42, TypeOfWork.MEASUREMENT_METHOD_CERTIFICATION));
            listOutfit.add(createOutfitXml(2, 12, 63, 32, listEmployeeId, StatusWork.ESTABLISHED, new Date(), 45, TypeOfWork.CALIBRATION_SI));

            provider.setRecordsToXmlFile(listCustomer);
            provider.setRecordsToXmlFile(listEmployee);
            provider.setRecordsToXmlFile(listPlaceOfWorks);
            provider.setRecordsToXmlFile(listMechanicalMeasurement);
            provider.setRecordsToXmlFile(listElectricalMeasurement);
            provider.setRecordsToXmlFile(listHeadOfDepartment);
            provider.setRecordsToXmlFile(listOutfit);
        }catch (Exception e){
            log.error(Constants.ERROR_DATA_INITIALIZING_TEST);
            log.error(e);
        }
    }

    @Test
    public void outfitSuccess() {
        List<Long> listEmployeeId = new ArrayList<>();
        listEmployeeId.add((long)20);
        listEmployeeId.add((long)21);
        assertEquals(Status.SUCCESS, provider.outfit("create_outfit", 5, listEmployeeId, new Date(), 11,50,33,"CALIBRATION", "ELECTRICAL", 44, StatusWork.ESTABLISHED));
        listEmployeeId.add((long)23);
        assertEquals(Status.SUCCESS, provider.outfit("edit_outfit", 5, listEmployeeId, null, 0, 50, 0, null, null, 0, null));
        assertEquals(Status.SUCCESS, provider.outfit("delete_outfit",1, null, null, 0, 50, 0, null, null, 0, null));
    }

    @Test
    public void outfitFail() {
        List<Long> listEmployeeId = new ArrayList<>();
        listEmployeeId.add((long)20);
        listEmployeeId.add((long)21);
        assertEquals(Status.FAIL, provider.outfit("create_outfit", 5, listEmployeeId, new Date(), 21,50,33,"CALIBRATION", "ELECTRICAL", 70, StatusWork.ESTABLISHED));
        listEmployeeId.add((long)23);
        assertEquals(Status.FAIL, provider.outfit("edit_outfit", 5, listEmployeeId, null, 0, 60, 0, null, null, 0, null));
        assertEquals(Status.FAIL, provider.outfit("delete_outfit",10, null, null, 0, 50, 0, null, null, 0, null));
    }

    @Test
    public void createOutfitSuccess() {
        List<Long> listEmployeeId = new ArrayList<>();
        listEmployeeId.add((long)20);
        listEmployeeId.add((long)21);
        assertTrue((boolean) provider.createOutfit(6, new Date(), 13,  53,32,"CALIBRATION","ELECTRICAL",46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
    }

    @Test
    public void createOutfitFail() {
        List<Long> listEmployeeId = new ArrayList<>();
        listEmployeeId.add((long)20);
        assertFalse((boolean) provider.createOutfit(6, new Date(), 13,  53,32,"CALIBRATION","ELECTRICAL", 46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
        listEmployeeId.add((long)20);
        listEmployeeId.clear();
        listEmployeeId.add((long)20);
        listEmployeeId.add((long)27);
        assertFalse((boolean) provider.createOutfit(6, new Date(), 13,  53,32,"CALIBRATION","ELECTRICAL",46,  listEmployeeId, StatusWork.ESTABLISHED).get(0));
        listEmployeeId.clear();
        listEmployeeId.add((long)20);
        listEmployeeId.add((long)22);
        assertFalse((boolean) provider.createOutfit(50, new Date(), 13,  53,32,"CALIBRATION","ELECTRICAL",50, listEmployeeId, StatusWork.ESTABLISHED).get(0));
        assertFalse((boolean) provider.createOutfit(2, new Date(), 13,  53,32,"CALIBRATION","ELECTRICAL",46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
        assertFalse((boolean) provider.createOutfit(8, new Date(), 43,  53,32,"CALIBRATION","ELECTRICAL",46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
        assertFalse((boolean) provider.createOutfit(9, new Date(), 13,  63,32,"CALIBRATION","ELECTRICAL",46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
        assertFalse((boolean) provider.createOutfit(10, new Date(), 13,  53,92,"CALIBRATION","ELECTRICAL",46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
        assertFalse((boolean) provider.createOutfit(11, new Date(), 13,  53,32,"CALIBRATIONS","ELECTRICAL",46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
        assertFalse((boolean) provider.createOutfit(12, new Date(), 13,  53,32,"CALIBRATION","ELECTRICALS",46, listEmployeeId, StatusWork.ESTABLISHED).get(0));
    }

    @Test
    public void deleteOutfitSuccess() {
        assertTrue(provider.deleteOutfit(1, 50));
    }

    @Test
    public void deleteOutfitFail() {
        assertFalse(provider.deleteOutfit(10, 50));
        assertFalse(provider.deleteOutfit(2, 80));
    }

    @Test
    public void editOutfitSuccess() {
        List<Long> listEmployeeId = new ArrayList<>();
        listEmployeeId.add((long)22);
        listEmployeeId.add((long)23);
        assertTrue((boolean) provider.editOutfit(2, listEmployeeId, 53).get(0));
    }

    @Test
    public void editOutfitFail() {
        List<Long> listEmployeeId = new ArrayList<>();
        listEmployeeId.add((long)22);
        listEmployeeId.add((long)27);
        assertFalse((boolean) provider.editOutfit(2, listEmployeeId, 53).get(0));
        listEmployeeId.clear();
        listEmployeeId.add((long)22);
        assertFalse((boolean) provider.editOutfit(2, listEmployeeId, 53).get(0));
        listEmployeeId.add((long)23);
        assertFalse((boolean) provider.editOutfit(31, listEmployeeId, 53).get(0));
        assertFalse((boolean) provider.editOutfit(2, listEmployeeId, 98).get(0));
    }

    @Test
    public void analysisSuccess(){
        assertEquals(Status.SUCCESS, provider.analysis("all_employee_outfits", 22, 52));
        assertEquals(Status.SUCCESS, provider.analysis("all_executor_outfits", 63, 52));
    }

    @Test
    public void analysisFail(){
        assertEquals(Status.FAIL, provider.analysis("all_employee_outfit", 22, 52));
        assertEquals(Status.FAIL, provider.analysis("all_employee_outfits", 30, 52));
        assertEquals(Status.FAIL, provider.analysis("all_employee_outfits", 30, 80));
        assertEquals(Status.FAIL, provider.analysis("all_executor_outfit", 63, 52));
        assertEquals(Status.FAIL, provider.analysis("all_executor_outfits", 43, 52));
        assertEquals(Status.FAIL, provider.analysis("all_executor_outfits", 63, 59));
    }

    @Test
    public void allEmployeeOutfitsSuccess(){
        assertTrue((boolean) provider.allEmployeeOutfits(22, 53).get(0));
    }

    @Test
    public void allEmployeeOutfitsFail(){
        assertFalse((boolean) provider.allEmployeeOutfits(12, 53).get(0));
        assertFalse((boolean) provider.allEmployeeOutfits(22, 80).get(0));
    }

    @Test
    public void allExecutorOutfitsSuccess(){
        assertTrue((boolean) provider.allExecutorOutfits(63, 53).get(0));
    }

    @Test
    public void allExecutorOutfitsFail(){
        assertFalse((boolean) provider.allExecutorOutfits(30, 53).get(0));
        assertFalse((boolean) provider.allExecutorOutfits(63, 63).get(0));
    }

    @Test
    public void employeeManagementSuccess() {
        assertEquals(Status.SUCCESS, provider.employeeManagement("add_employee",27, "Alen", 52));
        assertEquals(Status.SUCCESS, provider.employeeManagement("promote_employee",20,  null, 52));
        assertEquals(Status.SUCCESS, provider.employeeManagement("demote_employee",21, null,52));
        assertEquals(Status.SUCCESS, provider.employeeManagement("remove_employee",27, null,52));
    }

    @Test
    public void employeeManagementFail() {
        assertEquals(Status.FAIL, provider.employeeManagement("add_employe",27, "Alen", 52));
        assertEquals(Status.FAIL, provider.employeeManagement("promote_employe",27, null, 52));
        assertEquals(Status.FAIL, provider.employeeManagement("demote_employee",27, null, 52));
        assertEquals(Status.FAIL, provider.employeeManagement("remove_employee",27, null, 52));
    }

    @Test
    public void addEmployeeSuccess() {
        assertTrue((boolean) provider.addEmployee(28, "Kiko", 52).get(0));
    }

    @Test
    public void addEmployeeFail() {
        assertFalse((boolean) provider.addEmployee(28, "Kiko123", 52).get(0));
        assertFalse((boolean) provider.addEmployee(28, "Kiko", 78).get(0));
    }

    @Test
    public void promoteEmployeeSuccess() {
        assertTrue((boolean) provider.promoteEmployee(20, 53).get(0));
    }

    @Test
    public void promoteEmployeeFail() {
        assertFalse((boolean) provider.promoteEmployee(21, 53).get(0));
        assertFalse((boolean) provider.promoteEmployee(20, 93).get(0));
    }

    @Test
    public void increaseSalaryEmployeeSuccess() {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee1.setSalary(5000);
        assertEquals(employee1.getSalary(), provider.increaseSalaryEmployee(employee2).getSalary());
    }

    @Test
    public void increaseSalaryEmployeeFail() {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee1.setSalary(4000);
        assertEquals(employee1.getSalary(), provider.increaseSalaryEmployee(employee2).getSalary(), 1000);
    }

    @Test
    public void demoteEmployeeSuccess() {
        assertTrue((boolean) provider.demoteEmployee(21, 53).get(0));
    }

    @Test
    public void demoteEmployeeFail() {
        assertFalse((boolean) provider.demoteEmployee(20, 53).get(0));
        assertFalse((boolean) provider.demoteEmployee(20, 90).get(0));
    }

    @Test
    public void lowerSalaryEmployeeSuccess() {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee2.setSalary(10000);
        employee1.setSalary(5000);
        assertEquals(employee1.getSalary(), provider.lowerSalaryEmployee(employee2).getSalary());
    }

    @Test
    public void lowerSalaryEmployeeFail() {
        Employee employee1 = new Employee();
        Employee employee2 = new Employee();
        employee2.setSalary(10000);
        employee1.setSalary(4000);
        assertEquals(employee1.getSalary(), provider.lowerSalaryEmployee(employee2).getSalary(), 1000);
    }

    @Test
    public void removeEmployeeSuccess() {
        assertTrue(provider.removeEmployee(24, 52));
    }

    @Test
    public void removeEmployeeFail() {
        assertFalse(provider.removeEmployee(30, 52));
        assertFalse(provider.removeEmployee(23, 69));
    }

    @Test
    public void changeStatusOutfitSuccess(){
        assertEquals(Status.SUCCESS, provider.changeStatusOutfit("change_status_to_in_work", 2, 63));
        assertEquals(Status.SUCCESS, provider.changeStatusOutfit("finish_work", 2, 63));
    }

    @Test
    public void changeStatusOutfitFail(){
        assertEquals(Status.FAIL, provider.changeStatusOutfit("finish_works", 2, 63));
        assertEquals(Status.FAIL, provider.changeStatusOutfit("finish_work", 3, 63));
        assertEquals(Status.FAIL, provider.changeStatusOutfit("finish_work", 2, 73));

        assertEquals(Status.FAIL, provider.changeStatusOutfit("change_status_to_in_works", 2, 63));
        assertEquals(Status.FAIL, provider.changeStatusOutfit("change_status_to_in_work", 3, 63));
        assertEquals(Status.FAIL, provider.changeStatusOutfit("change_status_to_in_work", 2, 73));
    }

    @Test
    public void changeStatusToInWorkSuccess(){
        assertTrue((boolean) provider.changeStatusToInWork( 2, 63).get(0));
    }

    @Test
    public void changeStatusToInWorkFail(){
        assertFalse((boolean) provider.changeStatusToInWork( 3, 63).get(0));
        assertFalse((boolean) provider.changeStatusToInWork( 2, 62).get(0));
    }

    @Test
    public void finishWorkSuccess(){
        assertTrue((boolean) provider.finishWork(2, 63).get(0));
    }

    @Test
    public void finishWorkFail(){
        assertFalse((boolean) provider.finishWork( 3, 63).get(0));
        assertFalse((boolean) provider.finishWork( 2, 62).get(0));
    }
}