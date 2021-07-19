package ru.sfedu.aems.beans;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import java.io.Serializable;
import java.util.Objects;

@Root
public class MeansOfMeasurement implements Serializable {

    public MeansOfMeasurement() {
    }

    @CsvBindByName
    private long id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private double measurementError;

    @Attribute(name = "id")
    public long getId(){
        return id;
    }

    @Attribute(name = "id")
    public void setId(long id){
        this.id = id;
    }

    @Element(name = "Name")
    public String getName(){
        return name;
    }

    @Element(name = "Name")
    public void setName(String name){
        this.name = name;
    }

    @Element(name = "MeasurementError")
    public double getMeasurementError(){
        return measurementError;
    }

    @Element(name = "MeasurementError")
    public void setMeasurementError(double measurementError){
        this.measurementError = measurementError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeansOfMeasurement that = (MeansOfMeasurement) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, measurementError);
    }

    @Override
    public String toString() {
        return "MeansOfMeasurement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", measurementError=" + measurementError +
                '}';
    }
}
