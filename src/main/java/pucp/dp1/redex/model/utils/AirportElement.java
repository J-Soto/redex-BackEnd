package pucp.dp1.redex.model.utils;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AirportElement {
	
	private String code; 
	
	private String name;
	
	private String city;
	
	private String country;
	
	private Long count;
	
	private Long x;
	
	private Double percentage;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date date;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date ini;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date fin;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDate ldate;
	
	public AirportElement(String code, String name, Long count) {
		super();
		this.code = code;
		this.name = name;
		this.count = count;
	}

	public AirportElement(String code, String city, String country, Long x) {
		super();
		this.code = code;
		this.x = x;
		this.city = city;
		this.country = country;
	}

	public AirportElement(String code, String name,  Date date, String city, String country, Long count) {
		super();
		this.code = code;
		this.name = name;
		this.count = count;
		this.date = date;
		this.city = city;
		this.country = country;
	}
	
	
	
	public AirportElement(String code, String name, Date ini, Date fin, String city, String country, Long count) {
		super();
		this.ini = ini;
		this.fin = fin;
		this.code = code;
		this.name = name;
		this.city = city;
		this.country = country;
		this.count = count;
	}

	public LocalDate getLdate() {
		return ldate;
	}

	public void setLdate(LocalDate ldate) {
		this.ldate = ldate;
	}

	@JsonIgnore
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@JsonIgnore
	public Long getX() {
		return x;
	}

	public void setX(Long x) {
		this.x = x;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
}
