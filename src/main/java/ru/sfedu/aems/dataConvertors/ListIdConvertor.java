package ru.sfedu.aems.dataConvertors;

import com.opencsv.bean.AbstractBeanField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.aems.Constants;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ListIdConvertor extends AbstractBeanField {
    private final static Logger log = LogManager.getLogger(ListIdConvertor.class);

    @Override
    protected Object convert(String value){
        try{
            if (value.equals(Constants.DOUBLE_QUOTES)){
                return null;
            }
        }catch(Exception e){
            log.error(e);
            return null;
        }
        return Arrays.asList(value.split(Constants.DELIMITER)).stream().map(Long::parseLong).collect(Collectors.toList());
    }

    @Override
    protected String convertToWrite(Object value){
        if (value == null){
            return Constants.DOUBLE_QUOTES;
        }
        return listToString((List<Long>)value);
    }

    public static String listToString(List<Long> listId) {
        try{
            return listId.stream()
                    .map(Objects::toString)
                    .collect(Collectors.joining(Constants.DELIMITER));
        }catch (Exception e){
            log.error(e);
            log.error(Constants.ERROR_CONVERT_LIST_TO_STRING);
            return Constants.DOUBLE_QUOTES;
        }
    }

    public static List<Long> stringToList(String listId){
        try{
            return Stream.of(listId.split(Constants.DELIMITER))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }catch (Exception e){
            log.error(e);
            log.error(Constants.ERROR_CONVERT_STRING_TO_LIST);
            return null;
        }
    }
}
