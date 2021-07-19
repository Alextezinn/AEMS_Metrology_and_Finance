package ru.sfedu.aems.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import ru.sfedu.aems.api.DataProviderCsv;
import ru.sfedu.aems.beans.ElectricalMeasurement;
import ru.sfedu.aems.beans.MeansOfMeasurement;
import ru.sfedu.aems.beans.MechanicalMeasurement;
import java.util.List;

public class MeansOfMeasurementConvertor extends AbstractBeanField {
    private final static Logger log = LogManager.getLogger(MeansOfMeasurementConvertor.class);

    @Override
    protected Object convert(String value) {
        MeansOfMeasurement measurement;
        try {
            if (value.equals(Constants.DOUBLE_QUOTES)) {
                return null;
            }
            DataProviderCsv dataProviderCsv = new DataProviderCsv();
            List<String> params = dataProviderCsv.parseCsvObject(value);
            if(value.substring(0, 21).equals(ElectricalMeasurement.class.getSimpleName())){
                List<ElectricalMeasurement> records = dataProviderCsv.getRecordsFromCsvFile(ElectricalMeasurement.class);
                measurement = (MeansOfMeasurement) records.stream().filter(el -> el.getId() == Long.parseLong(params.get(0))).findFirst().get();
            }else{
                List<MechanicalMeasurement> records = dataProviderCsv.getRecordsFromCsvFile(MechanicalMeasurement.class);
                measurement = (MeansOfMeasurement) records.stream().filter(el -> el.getId() == Long.parseLong(params.get(0))).findFirst().get();
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return measurement;
    }
}
