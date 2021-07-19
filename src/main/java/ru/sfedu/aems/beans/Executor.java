package ru.sfedu.aems.beans;

import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import java.io.Serializable;
import java.util.Objects;

@Root
public class Executor implements Serializable {

    public Executor() {
    }

    @CsvBindByName
    private long id;

    @CsvBindByName
    private String phoneNumber;

    @Attribute(name = "id")
    public long getId() {
        return id;
    }

    @Attribute(name = "id")
    public void setId(long id) {
        this.id = id;
    }

    @Element(name = "PhoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Element(name = "PhoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Executor executor = (Executor) o;
        return id == executor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phoneNumber);
    }

    @Override
    public String toString() {
        return "Executor{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
