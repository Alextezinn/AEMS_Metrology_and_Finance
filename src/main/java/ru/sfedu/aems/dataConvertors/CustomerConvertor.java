package ru.sfedu.aems.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.api.DataProviderCsv;
import ru.sfedu.aems.beans.Customer;


import java.util.List;

public class CustomerConvertor extends AbstractBeanField {
    private final static Logger log = LogManager.getLogger(CustomerConvertor.class);

    @Override
    protected Object convert(String value) {
        Customer customer = new Customer();
        try {
            if (value.equals(Constants.DOUBLE_QUOTES)) {
                return null;
            }
            DataProviderCsv dataProviderCsv = new DataProviderCsv();
            List<String> params = dataProviderCsv.parseCsvObject(value);
            customer.setId(Long.parseLong(params.get(0)));
            customer.setName(params.get(1));
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return customer;
    }
}
