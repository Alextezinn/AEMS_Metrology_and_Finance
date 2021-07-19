package ru.sfedu.aems.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.api.DataProviderCsv;
import ru.sfedu.aems.beans.Executor;

import java.util.List;

public class ExecutorConvertor extends AbstractBeanField {
    private final static Logger log = LogManager.getLogger(ExecutorConvertor.class);

    @Override
    protected Object convert(String value) {
        Executor executor = new Executor();
        try {
            if (value.equals(Constants.DOUBLE_QUOTES)) {
                return null;
            }
            DataProviderCsv dataProviderCsv = new DataProviderCsv();
            List<String> params = dataProviderCsv.parseCsvObject(value);
            executor.setId(Long.parseLong(params.get(0)));
            executor.setPhoneNumber(params.get(1));
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return executor;
    }

}
