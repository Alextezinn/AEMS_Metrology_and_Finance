package ru.sfedu.aems.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.api.DataProviderCsv;
import ru.sfedu.aems.beans.Employee;
import java.util.ArrayList;
import java.util.List;

public class EmployeesConvertor extends AbstractBeanField {
    private final static Logger log = LogManager.getLogger(EmployeesConvertor.class);

    @Override
    public Object convert(String value) {
        List<Employee> employeesOutfit;
        try {
            List<Long> idEmployees = new ArrayList<>();
            if (value.equals(Constants.DOUBLE_QUOTES)) {
                return null;
            }
            String s = value.substring(1, value.length() - 1);
            String[] args = s.split(Constants.LIST_OBJECT_SEPARATOR);
            for(String arg : args){
                idEmployees.add(Long.parseLong(arg.substring(12,14)));
            }
            DataProviderCsv dataProviderCsv = new DataProviderCsv();
            List<Employee> employees = dataProviderCsv.getRecordsFromCsvFile(Employee.class);
            employeesOutfit = dataProviderCsv.getEmployeesOutfit(idEmployees, employees);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return employeesOutfit;
    }

    @Override
    protected String convertToWrite(Object value) {
        List<Employee> employees = (List<Employee>) value;
        String s = Constants.LEFT_BRACKET;
        for(int i = 0; i < employees.size(); i++){
            if(i != employees.size() - 1){
                s += employees.get(i).toString()+Constants.LIST_OBJECT_SEPARATOR;
            }
            else{
                s += employees.get(i).toString();
            }
        }
        s += Constants.RIGHT_BRACKET;
        return s;
    }
}
