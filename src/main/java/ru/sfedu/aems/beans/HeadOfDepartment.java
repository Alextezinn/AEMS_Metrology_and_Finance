package ru.sfedu.aems.beans;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import ru.sfedu.aems.dataConvertors.ExecutorConvertor;

import java.io.Serializable;
import java.util.Objects;

@Root
public class HeadOfDepartment implements Serializable {

    public HeadOfDepartment() {
    }

    @CsvBindByName
    private long id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private double salary;

    @CsvCustomBindByName(converter = ExecutorConvertor.class)
    private Executor executor;

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

    @Element(name = "Salary")
    public double getSalary(){
        return salary;
    }

    @Element(name = "Salary")
    public void setSalary(double salary){
        this.salary = salary;
    }

    @Element(name = "Executor")
    public Executor getExecutor(){
        return executor;
    }

    @Element(name = "Executor")
    public void setExecutor(Executor executor){
        this.executor = executor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, salary, executor);
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
        final HeadOfDepartment other = (HeadOfDepartment) obj;
        if(this.id != other.id){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HeadOfDepartment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", executor=" + executor +
                '}';
    }
}
