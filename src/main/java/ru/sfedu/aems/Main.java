package ru.sfedu.aems;

import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.api.DataProvider;
import ru.sfedu.aems.api.DataProviderCsv;
//import ru.sfedu.aems.api.DataProviderJDBC;
//import ru.sfedu.aems.api.DataProviderXML;
import ru.sfedu.aems.api.DataProviderJDBC;
import ru.sfedu.aems.api.DataProviderXML;
import ru.sfedu.aems.beans.*;
import ru.sfedu.aems.dataConvertors.EmployeesConvertor;
import ru.sfedu.aems.dataConvertors.ListIdConvertor;
import ru.sfedu.aems.enums.*;


public class Main {
    private final static Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws Exception {
        List<String> arguments = Arrays.asList(args);
        DataProvider dataProvider = null;
        try {
            switch (arguments.get(0).toUpperCase()) {
                case Constants.CSV:
                    dataProvider = new DataProviderCsv();
                    break;
                case Constants.XML:
                    dataProvider = new DataProviderXML();
                    break;
                case Constants.JDBC:
                    dataProvider = new DataProviderJDBC();
                    break;
                default:
                    log.error(Constants.ERROR_CHOOSE_DATA_PROVIDER);
                    System.exit(1);
            }
            if(arguments.get(1).equals(Constants.DATA_INITIALIZATION)){
                log.info(Constants.DATA_INITIALIZATION_SUCCESSFUL);
                log.info(dataProvider.initDataSource());
                System.exit(1);
            }
            chooseBaseMethod(dataProvider, arguments);
        } catch(ArrayIndexOutOfBoundsException e){
            log.error(Constants.EMPTY_DATA_ERROR);
        } catch (Exception e) {
            log.error(Constants.COMMAND_ERROR);
            log.error(e);
        }
    }

    private static void chooseBaseMethod(DataProvider dataProvider, List<String> params) {
        try {
            String extendMethod = params.get(2).trim().toUpperCase();
            switch (params.get(1).trim().toUpperCase()) {
                case Constants.OUTFIT:
                    if(extendMethod.equals(Constants.DELETE_OUTFIT)){
                        log.info(dataProvider.outfit(extendMethod, Long.parseLong(params.get(3)), null, null, 0, Long.parseLong(params.get(4)), 0, null, null, 0, null));
                    }else if(extendMethod.equals(Constants.CREATE_OUTFIT)){
                        log.info(dataProvider.outfit(extendMethod, generateId(), ListIdConvertor.stringToList(params.get(4)), new Date(), Long.parseLong(params.get(3)), Long.parseLong(params.get(7)), Long.parseLong(params.get(5)), params.get(8), params.get(9) ,Long.parseLong(params.get(6)), StatusWork.ESTABLISHED));
                    }else{
                        log.info(dataProvider.outfit(extendMethod, Long.parseLong(params.get(3)), ListIdConvertor.stringToList(params.get(5)), null, 0, Long.parseLong(params.get(4)), 0, null, null, 0, null));
                    }
                    break;
                case Constants.ANALYSIS:
                    log.info(dataProvider.analysis(extendMethod, Long.parseLong(params.get(3)), Long.parseLong(params.get(4))));
                    break;
                case Constants.EMPLOYEE_MANAGEMENT:
                    if(extendMethod.equals(Constants.ADD_EMPLOYEE)){
                        log.info(dataProvider.employeeManagement(extendMethod, generateId(), params.get(3), Long.parseLong(params.get(4))));
                    }else {
                        log.info(dataProvider.employeeManagement(extendMethod, Long.parseLong(params.get(3)), null, Long.parseLong(params.get(4))));
                    }
                    break;
                case Constants.CHANGE_OUTFIT:
                    log.info(dataProvider.changeStatusOutfit(extendMethod, Long.parseLong(params.get(3)), Long.parseLong(params.get(4))));
                    break;
            }
        } catch (IllegalArgumentException e) {
            log.error(Constants.COMMAND_ERROR);
        }
    }

    private static long generateId(){
        return Math.round(Math.random() * 1000000);
    }
}


