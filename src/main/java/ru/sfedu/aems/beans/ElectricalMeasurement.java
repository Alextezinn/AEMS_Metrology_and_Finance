package ru.sfedu.aems.beans;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import java.io.Serializable;
import java.util.Objects;

@Root
public class ElectricalMeasurement extends MeansOfMeasurement implements Serializable {

    public ElectricalMeasurement(){
    }

    @CsvBindByName
    private String model;

    @CsvBindByName
    private double power;

    @Element(name = "Model")
    public String getModel(){
        return model;
    }

    @Element(name = "Model")
    public void setModel(String model){
        this.model = model;
    }

    @Element(name = "Power")
    public double getPower(){
        return power;
    }

    @Element(name = "Power")
    public void setPower(double power){
        this.power = power;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model, power);
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
        ElectricalMeasurement other = (ElectricalMeasurement) obj;
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
        return "ElectricalMeasurement{id=" + getId() + ", name=" + getName() +
                ", measurementError=" + getMeasurementError() + " , model=" + model +
                ", power=" + power + "}";
    }

}
