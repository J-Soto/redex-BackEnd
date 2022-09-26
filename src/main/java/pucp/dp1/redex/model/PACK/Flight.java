package pucp.dp1.redex.model.PACK;

import java.io.Serializable;
import java.sql.Time;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import pucp.dp1.redex.model.sales.Airport;

@Table(name = "flight")
@Entity
public class Flight implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int idFlight;
	
	@Column(name = "take_off_time")
	private Time takeOffTime;
	
	@Column(name = "arrival_time")
	private Time arrivalTime;
	
	@ManyToOne
	@JsonIgnoreProperties("warehouse")
	private Airport takeOffAirport;
	
	@ManyToOne
	@JsonIgnoreProperties("warehouse")
	private Airport arrivalAirport;
	
	@Column(name = "capacity")
	private Integer capacity;

	public int getIdFlight() {
		return idFlight;
	}

	public void setIdFlight(int idFlight) {
		this.idFlight = idFlight;
	}

	public Time getTakeOffTime() {
		return takeOffTime;
	}

	public void setTakeOffTime(Time takeOffTime) {
		this.takeOffTime = takeOffTime;
	}

	public Time getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Time arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Airport getTakeOffAirport() {
		return takeOffAirport;
	}

	public void setTakeOffAirport(Airport takeOffAirport) {
		this.takeOffAirport = takeOffAirport;
	}

	public Airport getArrivalAirport() {
		return arrivalAirport;
	}

	public void setArrivalAirport(Airport arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

}
