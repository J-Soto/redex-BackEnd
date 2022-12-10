package pucp.dp1.redex.model.storage;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import pucp.dp1.redex.model.sales.Airport;

@Entity
@Table(name = "warehouse")
public class Warehouse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "capacity")
	private Integer capacity;
	
	@Column(name = "packagesNumber")
	private Integer packagesNumber;
	
	@Column(name = "full")
	private Boolean full;

	@Column(name = "occupied_capacity")
	private Integer occupiedCapacity;

	public Integer getOccupiedCapacity() {
		return this.occupiedCapacity;
	}

	public void setOccupiedCapacity(final Integer occupiedCapacity) {
		this.occupiedCapacity = occupiedCapacity;
	}

	@OneToOne
	@JsonIgnoreProperties({"warehouse", "takeOffFlights", "arrivalFlights"})
	private Airport airport;

	public Airport getAirport() {
		return airport;
	}

	public void setAirport(Airport airport) {
		this.airport = airport;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getPackagesNumber() {
		return packagesNumber;
	}

	public void setPackagesNumber(Integer packagesNumber) {
		this.packagesNumber = packagesNumber;
	}

	public Boolean isFull() {
		return full;
	}

	public void setFull(Boolean full) {
		this.full = full;
	}

	public Boolean getFull() {
		return full;
	}

	
}
