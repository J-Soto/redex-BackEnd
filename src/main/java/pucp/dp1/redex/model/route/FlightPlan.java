package pucp.dp1.redex.model.route;

import java.io.Serializable;
import java.sql.Time;
import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import pucp.dp1.redex.model.PACK.Flight;

@Entity
@Table(name = "flight_plan")
public class FlightPlan implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "flight_plan_id")
	private int id;

	@Column(name = "packagesNumber")
	private Integer packagesNumber;

	@Column(name = "occupied_Capacity")
	private Integer occupiedCapacity;

	@Column(name = "full")
	private Boolean full;
	
	@Column(name = "packages_number_simulated")
	private Integer packagesNumberSimulated;
	
	@Column(name = "full_simulated")
	private Boolean fullSimulated;
	
	@Column(name = "status")
	private FlightPlanStatus status;
	
	@Column(name = "take_off_date")
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date takeOffDate;
	
	@Column(name = "arrival_date")
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date arrivalDate;

	@Column(name = "take_off_time_utc")
	private Time takeOffTimeUtc;
	
	@Column(name = "arrival_time_utc")
	private Time arrivalTimeUtc;
	
	@OneToOne
	@JsonIgnoreProperties("flightPlan")
	private Flight flight;

	public FlightPlan() {
	}

	public FlightPlan( Flight flight) {
		this.flight = flight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getPackagesNumber() {
		return packagesNumber;
	}

	public void setPackagesNumber(Integer packagesNumber) {
		this.packagesNumber = packagesNumber;
	}

	public Boolean getFull() {
		return full;
	}

	public void setFull(Boolean full) {
		this.full = full;
	}

	public Integer getOccupiedCapacity() {
		return this.occupiedCapacity;
	}

	public void setOccupiedCapacity(final Integer occupiedCapacity) {
		this.occupiedCapacity = occupiedCapacity;
	}
	public FlightPlanStatus getStatus() {
		return status;
	}

	public void setStatus(FlightPlanStatus status) {
		this.status = status;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

	public Date getTakeOffDate() {
		return takeOffDate;
	}

	public void setTakeOffDate(Date takeOffDate) {
		this.takeOffDate = takeOffDate;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public Integer getPackagesNumberSimulated() {
		return packagesNumberSimulated;
	}

	public void setPackagesNumberSimulated(Integer packagesNumberSimulated) {
		this.packagesNumberSimulated = packagesNumberSimulated;
	}
	public Boolean getFullSimulated() {
		return fullSimulated;
	}

	public void setFullSimulated(Boolean fullSimulated) {
		this.fullSimulated = fullSimulated;
	}

	public Time getTakeOffTimeUtc() {
		return takeOffTimeUtc;
	}

	public void setTakeOffTimeUtc(Time takeOffTimeUtc) {
		this.takeOffTimeUtc = takeOffTimeUtc;
	}

	public Time getArrivalTimeUtc() {
		return arrivalTimeUtc;
	}

	public void setArrivalTimeUtc(Time arrivalTimeUtc) {
		this.arrivalTimeUtc = arrivalTimeUtc;
	}

	

}
