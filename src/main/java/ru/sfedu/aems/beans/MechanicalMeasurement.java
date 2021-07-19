package ru.sfedu.aems.beans;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import java.io.Serializable;
import java.util.Objects;

@Root
public class MechanicalMeasurement extends MeansOfMeasurement implements Serializable {

    public MechanicalMeasurement(){
    }

    @CsvBindByName
    private String model;

    @Element(name = "Model")
    public String getModel(){
        return model;
    }

    @Element(name = "Model")
    public void setModel(String model){
        this.model = model;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        MechanicalMeasurement other = (MechanicalMeasurement) obj;
        if(this.getId() != other.getId()){
            return false;
        }
        if(!Objects.equals(this.getName(), other.getName())){
            return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return "MechanicalMeasurement{id=" + getId() + ", name=" + getName() +
                " ,measurementError=" + getMeasurementError() + ", model=" + getModel() + "}";
    }
}
