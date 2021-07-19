package ru.sfedu.aems.beans;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import ru.sfedu.aems.dataConvertors.*;
import ru.sfedu.aems.enums.StatusWork;
import ru.sfedu.aems.enums.TypeOfWork;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Root
public class Outfit implements Serializable {

    public Outfit() {
    }

    @CsvBindByName
    private long id;

    @CsvBindByName(column = "dateStartWork")
    @CsvDate("dd.MM.yyyy")
    private Date dateStartWork;

    @CsvBindByName(column = "completionDate")
    @CsvDate("dd.MM.yyyy")
    private Date completionDate;

    @CsvCustomBindByName(converter = ExecutorConvertor.class)
    private Executor executor;

    @CsvCustomBindByName(converter = PlaceOfWorkConvertor.class)
    private PlaceOfWork placeWork;

    @CsvCustomBindByName(converter = CustomerConvertor.class)
    private Customer customer;

    @CsvCustomBindByName(converter = MeansOfMeasurementConvertor.class)
    private MeansOfMeasurement measurementInstrument;

    @CsvCustomBindByName(converter = TypeOfWorkConvertor.class)
    private TypeOfWork typeWork;

    @CsvCustomBindByName(converter = StatusWorkConvertor.class)
    private StatusWork statusWork;

    @CsvCustomBindByName(column = "employees", converter = EmployeesConvertor.class)
    private List<Employee> employees;

    @Attribute(name = "id")
    public long getId() {
        return id;
    }

    @Attribute(name = "id")
    public void setId(long id) {
        this.id = id;
    }

    @Element(required = false, name = "CompletionDate")
    public Date getCompletionDate() {
        return completionDate;
    }

    @Element(required = false, name = "CompletionDate")
    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    @Element(name = "DateStartWork")
    public Date getDateStartWork() {
        return dateStartWork;
    }

    @Element(name = "DateStartWork")
    public void setDateStartWork(Date dateStartWork) {
        this.dateStartWork = dateStartWork;
    }

    @Element(name = "Customer")
    public Customer getCustomer() {
        return customer;
    }

    @Element(name = "Customer")
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Element(name = "MeasurementInstrument")
    public MeansOfMeasurement getMeasurementInstrument() {
        return measurementInstrument;
    }

    @Element(name = "MeasurementInstrument")
    public void setMeasurementInstrument(MeansOfMeasurement measurementInstrument) {
        this.measurementInstrument = measurementInstrument;
    }

    @Element(name = "StatusWork")
    public StatusWork getStatusWork() {
        return statusWork;
    }

    @Element(name = "StatusWork")
    public void setStatusWork(StatusWork statusWork) {
        this.statusWork = statusWork;
    }

    @Element(name = "PlaceWork")
    public PlaceOfWork getPlaceWork() {
        return placeWork;
    }

    @Element(name = "PlaceWork")
    public void setPlaceWork(PlaceOfWork placeWork) {
        this.placeWork = placeWork;
    }

    @Element(name = "Executor")
    public Executor getExecutor() {
        return executor;
    }

    @Element(name = "Executor")
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Element(name = "TypeWork")
    public TypeOfWork getTypeWork() {
        return typeWork;
    }

    @Element(name = "TypeWork")
    public void setTypeWork(TypeOfWork typeWork) {
        this.typeWork = typeWork;
    }

    @ElementList(required=false, name = "ListEmployee")
    public List<Employee> getEmployees() {
        return employees;
    }

    @ElementList(required=false, name = "ListEmployee")
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Outfit outfit = (Outfit) o;
        return id == outfit.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateStartWork, completionDate, executor, placeWork, customer, measurementInstrument, typeWork, statusWork, employees);
    }

    @Override
    public String toString() {
        return "Outfit{" +
                "id=" + id +
                ", dateStartWork=" + dateStartWork +
                ", completionDate=" + completionDate +
                ", executor=" + executor +
                ", placeWork=" + placeWork +
                ", customer=" + customer +
                ", measurementInstrument=" + measurementInstrument +
                ", typeWork=" + typeWork +
                ", statusWork=" + statusWork +
                ", employees=" + employees +
                '}';
    }
}
