package ru.sfedu.aems;


public class Constants {

    // Errors messages
    public static final String ERROR_CHOOSE_DATA_PROVIDER = "Error choosing data provider";
    public static final String COMMAND_ERROR = "Incorrect command entry";
    public static final String EMPTY_DATA_ERROR = "No data or not enough values for the program to work";
    public static final String ERROR_CONVERT_LIST_TO_STRING = "Error convert list to string";
    public static final String ERROR_CONVERT_STRING_TO_LIST = "Error convert string to list";
    public static final String ERROR_COUNT_EMPLOYEES = "Error count employees should be 2 or 3 employees";
    public static final String ERROR_EMPLOYEE_ID_NOT_FOUND = "Error employee id not found";
    public static final String ERROR_DUPLICATES_EMPLOYEE_ID = "Error duplicates employee id";
    public static final String ERROR_CUSTOMER_ID_NOT_FOUND = "Error customer id not found";
    public static final String ERROR_EXECUTOR_ID_NOT_FOUND = "Error executor id not found";
    public static final String ERROR_PLACE_OF_WORK_ID_NOT_FOUND = "Error place of work id not found";
    public static final String ERROR_TYPE_MEASUREMENT_INSTRUMENT = "Error type measurement instrument";
    public static final String ERROR_ID_OUTFIT_EXISTS = "Error this id outfit already exists";
    public static final String ERROR_NOT_DATA_OUTFIT = "No data about outfit";
    public static final String ERROR_ID_EMPLOYEE_EXISTS = "Error this id employee already exists";
    public static final String ERROR_BAD_NAME = "Error bad name";
    public static final String ERROR_NOT_DATA_EMPLOYEE = "No data about employee";
    public static final String ERROR_IMPOSSIBLE_PROMOTE_EMPLOYEE = "Error impossible to raise an employee because he senior";
    public static final String ERROR_IMPOSSIBLE_DEMOTE_EMPLOYEE = "Error impossible to lower an employee because he junior";
    public static final String ERROR_OUTFIT_HAS_DIFFERENT_EXECUTOR = "Error this outfit has a different executor";
    public static final String ERROR_HEAD_DEPARTMENT_ID_NOT_FOUND = "Error head department id not found";
    public static final String ERROR_OUTFIT_ID_NOT_FOUND = "Error outfit id not found";
    public static final String ERROR_REWRITE_FILE = "Error impossible rewrite file";
    public static final String ERROR_DATA_INITIALIZING_TEST = "Error data initializing for tests";
    public static final String ERROR_CREATE_OUTFIT = "Outfit create error";
    public static final String ERROR_DELETE_OUTFIT = "Outfit delete error";
    public static final String ERROR_EDIT_OUTFIT = "Outfit edit error";
    public static final String ERROR_ALL_EMPLOYEE_OUTFITS = "Get all employee outfits error";
    public static final String ERROR_ALL_EXECUTOR_OUTFITS = "Get all executor outfits error";
    public static final String ERROR_ADD_EMPLOYEE = "Add employee error";
    public static final String ERROR_PROMOTE_EMPLOYEE = "Promote employee error";
    public static final String ERROR_DEMOTE_EMPLOYEE = "Demote employee error";
    public static final String ERROR_REMOVE_EMPLOYEE = "Remove employee error";
    public static final String ERROR_FINISH_WORK = "Finish work error";
    public static final String ERROR_CHANGE_STATUS_TO_IN_WORK = "Change status to in work error";
    public static final String ERROR_INITIALIZATION_DATA_CSV = "Error initialization data csv";
    public static final String ERROR_INITIALIZATION_DATA_XML = "Error initialization data xml";
    public static final String ERROR_GET_VALUES_BEAN = "Error get values bean";
    public static final String ERROR_CHOOSE_TYPE_EMPLOYEE = "Error choose type employee";
    public static final String ERROR_CHOOSE_TYPE_OF_WORK = "Error choose type of work";
    public static final String ERROR_CHOOSE_STATUS_WORK = "Error choose status work";
    public static final String ERROR_CHOOSE_TYPE_MEASUREMENT_INSTRUMENT = "Error choose type measurement instrument";
    public static final String ERROR_FAILED_WRITE_DATA = "Failed to write data";
    public static final String ERROR_FAILED_GET_DATA = "Failed to get data";
    public static final String ERROR_GET_LIST_CUSTOMER = "Error get list customer";
    public static final String ERROR_GET_LIST_EMPLOYEE = "Error get list employee";
    public static final String ERROR_GET_LIST_PLACE_OF_WORK = "Error get list place of work";
    public static final String ERROR_GET_LIST_HEAD_OF_DEPARTMENT = "Error get list head of department";
    public static final String ERROR_GET_LIST_ELECTRICAL_MEASUREMENT = "Error get list electrical measurement";
    public static final String ERROR_GET_LIST_MECHANICAL_MEASUREMENT = "Error get list mechanical measurement";
    public static final String ERROR_GET_LIST_OUTFIT = "Error get list outfit";

    //Successful messages
    public static final String CREATE_OUTFIT_SUCCESSFUL = "Outfit created successful";
    public static final String DELETE_OUTFIT_SUCCESSFUL = "Outfit deleted successful";
    public static final String EDIT_OUTFIT_SUCCESSFUL = "Outfit edited successful";
    public static final String ALL_EMPLOYEE_OUTFITS_SUCCESSFUL = "Get all employee outfits successful";
    public static final String ALL_EXECUTOR_OUTFITS_SUCCESSFUL = "Get all executor outfits successful";
    public static final String ADD_EMPLOYEE_SUCCESSFUL = "Add employee successful";
    public static final String PROMOTE_EMPLOYEE_SUCCESSFUL = "Promote employee successful";
    public static final String DEMOTE_EMPLOYEE_SUCCESSFUL = "Demote employee successful";
    public static final String REMOVE_EMPLOYEE_SUCCESSFUL = "Remove employee successful";
    public static final String FINISH_WORK_SUCCESSFUL = "Finish work successful";
    public static final String CHANGE_STATUS_TO_IN_WORK_SUCCESSFUL = "Change status to in work successful";
    public static final String DATA_INITIALIZATION_SUCCESSFUL = "Data successfully initialized";

    //Names data providers
    public static final String CSV = "CSV";
    public static final String XML = "XML";
    public static final String JDBC = "JDBC";

    //Names base methods API
    public static final String OUTFIT = "OUTFIT";
    public static final String EMPLOYEE_MANAGEMENT = "EMPLOYEE_MANAGEMENT";
    public static final String ANALYSIS = "ANALYSIS";
    public static final String CHANGE_OUTFIT = "CHANGE_OUTFIT";

    //Names extend methods API
    public static final String CREATE_OUTFIT = "CREATE_OUTFIT";
    public static final String DELETE_OUTFIT = "DELETE_OUTFIT";
    public static final String EDIT_OUTFIT = "EDIT_OUTFIT";
    public static final String ADD_EMPLOYEE = "ADD_EMPLOYEE";
    public static final String PROMOTE_EMPLOYEE = "PROMOTE_EMPLOYEE";
    public static final String DEMOTE_EMPLOYEE = "DEMOTE_EMPLOYEE";
    public static final String REMOVE_EMPLOYEE = "REMOVE_EMPLOYEE";
    public static final String FINISH_WORK = "FINISH_WORK";
    public static final String CHANGE_STATUS_TO_IN_WORK = "CHANGE_STATUS_TO_IN_WORK";
    public static final String ALL_EMPLOYEE_OUTFITS = "ALL_EMPLOYEE_OUTFITS";
    public static final String ALL_EXECUTOR_OUTFITS = "ALL_EXECUTOR_OUTFITS";

    //Names types of work
    public static final String VERIFICATION = "VERIFICATION";
    public static final String CALIBRATION = "CALIBRATION";
    public static final String TESTING = "TESTING";
    public static final String CERTIFICATION = "MEASUREMENT";
    public static final String ACCREDITATION = "ACCREDITATION";

    //Names types measurement instrument
    public static final String ELECTRICAL = "ELECTRICAL";
    public static final String MECHANICAL = "MECHANICAL";

    //Names type employee
    public static final String JUNIOR_EMPLOYEE = "JUNIOR";
    public static final String SENIOR_EMPLOYEE = "SENIOR";

    //Names status work
    public static final String ESTABLISHED = "ESTABLISHED";
    public static final String IN_WORK = "IN_WORK";
    public static final String COMPLETED = "COMPLETED";

    //Names tables database
    public static final String CUSTOMER = "Customer";
    public static final String EMPLOYEE = "Employee";
    public static final String HEAD_OF_DEPARTMENT = "HeadOfDepartment";
    public static final String PLACE_OF_WORK = "PlaceOfWork";
    public static final String ELECTRICAL_MEASUREMENT = "ElectricalMeasurement";
    public static final String MECHANICAL_MEASUREMENT = "MechanicalMeasurement";
    public static final String OUTFIT_TABLE = "Outfit";
    public static final String EXECUTOR = "Executor";

    //Sql queries
    public static final String QUERY_INSERT_TABLE = "INSERT INTO %s VALUES (%s)";
    public static final String QUERY_SELECT_ALL = "SELECT * FROM %s";
    public static final String QUERY_DELETE_ALL_TABLE = "DELETE FROM %s";
    public static final String QUERY_SELECT_BY_CONDITION = "SELECT * FROM %s WHERE id = %s";
    public static final String QUERY_DELETE_BY_CONDITION = "DELETE FROM %s WHERE id = %s";
    public static final String QUERY_UPDATE_RECORD_OUTFIT = "UPDATE %s SET idEmployees = '%s' WHERE id = %s";
    public static final String QUERY_UPDATE_RECORD_EMPLOYEE = "UPDATE %s SET salary = '%s', typeEmployee = '%s' WHERE id = %s";
    public static final String QUERY_FINISH_OUTFIT = "UPDATE %s SET statusWork = '%s', completionDate = '%tF' WHERE id = %s";
    public static final String QUERY_CHANGE_STATUS_TO_IN_WORK = "UPDATE %s SET statusWork = '%s' WHERE id = %s";

    //Other
    public static final String DELIMITER = ",";
    public static final String DOUBLE_QUOTES = "";
    public static final String UNDERSCORE = "_";
    public static final String DATA_INITIALIZATION = "data_init";
    public static final String OBJECT_LEFT_SEPARATOR = "{";
    public static final String OBJECT_RIGHT_SEPARATOR = "}";
    public static final String OBJECT_FIELD_SEPARATOR = ", ";
    public static final String OBJECT_VALUE_SEPARATOR = "=";
    public static final String STRING_SEPARATOR = "'";
    public static final String EMPTY = "";
    public static final String LIST_OBJECT_SEPARATOR = ";";
    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";
}
