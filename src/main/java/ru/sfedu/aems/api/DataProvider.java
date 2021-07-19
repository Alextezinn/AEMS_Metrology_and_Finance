package ru.sfedu.aems.api;

import ru.sfedu.aems.beans.Employee;
import ru.sfedu.aems.enums.Status;
import ru.sfedu.aems.enums.StatusWork;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface DataProvider {
    /**
     * Метод позволяет управлять нарядами, возможность создавать, редактировать и удалять наряды.
     * @param operation - действие пользователя
     * @param idOutfit - идентификатор наряда
     * @param idEmployees - список идентификаторов рабочих в наряде
     * @param dateStartWork - дата начала работы наряда
     * @param idCustomer - идентификатор покупателя
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @param idPlaceWork - идентификатор места работы наряда
     * @param typeWork - тип работы
     * @param typeInstrument - тип инструмента
     * @param idTypeMeasurementInstrument - идентификатор инструмента
     * @param statusWork - статус в котором находится наряд (создан,в работе,завершен)
     * @return Status
     */

    Status outfit(String operation, long idOutfit, List<Long> idEmployees, Date dateStartWork,
                  long idCustomer, long idHeadOfDepartment, long idPlaceWork,
                  String typeWork, String typeInstrument, long idTypeMeasurementInstrument, StatusWork statusWork);

    /**
     * Метод создает новый наряд на поверку/калибровку.
     * @param idOutfit - идентификатор наряда
     * @param dateStartWork - дата начала работы наряда
     * @param idCustomer - идентификатор покупателя
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @param idPlaceWork - идентификатор места работы наряда
     * @param typeWork - тип работы
     * @param typeInstrument - тип инструмента
     * @param idTypeMeasurementInstrument - идентификатор инструмента
     * @param idEmployees - список идентификаторов рабочих в наряде
     * @param statusWork - статус в котором находится наряд (создан)
     * @return List<Object>
     */

    List<Object> createOutfit(long idOutfit, Date dateStartWork, long idCustomer, long idHeadOfDepartment, long idPlaceWork, String typeWork,
                         String typeInstrument, long idTypeMeasurementInstrument, List<Long> idEmployees, StatusWork statusWork);

    /**
     * Метод позволяет удалить наряд
     * @param idOutfit - идентификатор наряда
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return boolean value
     */

    boolean deleteOutfit(long idOutfit, long idHeadOfDepartment) throws SQLException, IOException, ClassNotFoundException;

    /**
     * Метод позволяет редактировать список рабочих в наряде
     * @param idOutfit - идентификатор наряда
     * @param idEmployees - список идентификаторов рабочих в наряде
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return List<Object>
     */

    List<Object> editOutfit(long idOutfit, List<Long> idEmployees, long idHeadOfDepartment);

    /**
     * Метод позволяет просмотреть идентификаторы нарядов где есть такой сотрудник/исполнитель
     * @param operation - действие пользователя
     * @param idAnalysis - идентификатор анализируемого объекта
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return Status
     */

    Status analysis(String operation, long idAnalysis, long idHeadOfDepartment);

    /**
     * Метод позволяет просмотреть идентификаторы нарядов где есть такой сотрудник
     * @param idAnalysis - идентификатор сотрудника
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return List<Object>
     */

    List<Object> allEmployeeOutfits(long idAnalysis, long idHeadOfDepartment);

    /**
     * Метод позволяет просмотреть идентификаторы нарядов где есть такой исполнитель
     * @param idAnalysis - идентификатор исполнителя
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return List<Object>
     */

    List<Object> allExecutorOutfits(long idAnalysis, long idHeadOfDepartment);

    /**
     * Метод позволяет управлять сотрудниками (возможность добавления сотрудника,
     * удаления сотрудника(увольнения), повышения сотрудника, понижения сотрудника в должности)
     * @param operation - действие пользователя
     * @param idEmployee - идентификатор сотрудника
     * @param name - имя сотрудника
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return Status
     */

    Status employeeManagement(String operation, long idEmployee, String name, long idHeadOfDepartment);

    /**
     * Метод добавляет сотрудника
     * @param idEmployee - идентификатор сотрудника
     * @param name - имя сотрудника
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return List<Object>
     */

    List<Object> addEmployee(long idEmployee, String name, long idHeadOfDepartment);

    /**
     * Метод повышает сотрудника в должности
     * @param idEmployee - идентификатор сотрудника
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return List<Object>
     */

    List<Object> promoteEmployee(long idEmployee, long idHeadOfDepartment);

    /**
     * Метод повышает заработную плату сотруднику
     * @param employee - объект класса Employee
     * @return Employee
     */

    Employee increaseSalaryEmployee(Employee employee);

    /**
     * Метод понижает сотрудника в должности
     * @param idEmployee - идентификатор сотрудника
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return List<Object>
     */

    List<Object> demoteEmployee(long idEmployee, long idHeadOfDepartment);

    /**
     * Метод понижает заработную плату сотруднику
     * @param employee - объект класса Employee
     * @return Employee
     */

    Employee lowerSalaryEmployee(Employee employee);

    /**
     * Метод удаляет(увольняет) сотрудника
     * @param idEmployee - идентификатор сотрудника
     * @param idHeadOfDepartment - идентификатор главы отдела
     * @return boolean value
     */

    boolean removeEmployee(long idEmployee, long idHeadOfDepartment);

    /**
     * Метод изменяет статус наряда
     * @param operation - действие пользователя
     * @param idOutfit - идентификатор наряда
     * @param idExecutor - идентификатор исполнителя
     * @return Status
     */

    Status changeStatusOutfit(String operation, long idOutfit, long idExecutor);

    /**
     * Метод изменяет статус наряда на "завершен"
     * @param idOutfit - идентификатор наряда
     * @param idExecutor - идентификатор исполнителя
     * @return List<Object>
     */

    List<Object> finishWork(long idOutfit, long idExecutor) throws SQLException, ClassNotFoundException, IOException;

    /**
     * Метод изменяет статус наряда на "в работе"
     * @param idOutfit
     * @param idExecutor
     * @return List<Object>
     */

    List<Object> changeStatusToInWork(long idOutfit, long idExecutor) throws SQLException, ClassNotFoundException, IOException;

    /**
     * Инициализация данных для работы программы
     * @return Status
     */

    Status initDataSource() throws SQLException, IOException, ClassNotFoundException;
}
