package ru.sfedu.aems.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.api.DataProviderCsv;

public class TypeEmployeeConvertor extends AbstractBeanField {
    private final static Logger log = LogManager.getLogger(TypeEmployeeConvertor.class);

    @Override
    protected Object convert(String value) {
        try {
            if (value.equals(Constants.DOUBLE_QUOTES)) {
                return null;
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return DataProviderCsv.chooseTypeEmployee(value);
    }
}
