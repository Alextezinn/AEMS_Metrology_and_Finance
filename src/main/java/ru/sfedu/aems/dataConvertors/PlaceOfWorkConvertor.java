package ru.sfedu.aems.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.api.DataProviderCsv;
import ru.sfedu.aems.beans.PlaceOfWork;

import java.util.List;

public class PlaceOfWorkConvertor extends AbstractBeanField {
    private final static Logger log = LogManager.getLogger(PlaceOfWorkConvertor.class);

    @Override
    protected Object convert(String value) {
        PlaceOfWork placeWork = new PlaceOfWork();
        try {
            if (value.equals(Constants.DOUBLE_QUOTES)) {
                return null;
            }
            DataProviderCsv dataProviderCsv = new DataProviderCsv();
            List<String> params = dataProviderCsv.parseCsvObject(value);
            placeWork.setId(Long.parseLong(params.get(0)));
            placeWork.setName(params.get(1));
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return placeWork;
    }
}
