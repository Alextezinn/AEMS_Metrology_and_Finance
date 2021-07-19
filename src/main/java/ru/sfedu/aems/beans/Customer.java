package ru.sfedu.aems.beans;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import java.io.Serializable;
import java.util.Objects;

@Root
public class Customer implements Serializable {

    public Customer() {
    }

    @CsvBindByName
    private long id;

    @CsvBindByName
    private String name;

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

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
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
        final Customer other = (Customer) obj;
        if(this.id != other.id){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
