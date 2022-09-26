package pucp.dp1.redex.model.storage;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "storageRegister")
public class StorageRegister implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "checkInDate")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	private Date checkInDate;
	
	@Column(name = "checkOutDate")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
	private Date checkOutDate;
	
	@Column(name = "inWarehouse")
	private Boolean inWarehouse;
	
	@ManyToOne
//	@JsonIgnoreProperties("registers")
	@JoinColumn(name =  "package_id")
	@JsonIgnoreProperties("routePlan")
	private Package pack;
	
	@ManyToOne
//	@JsonIgnoreProperties("registers")
	private Warehouse warehouse;

	@Column(name="active")
	private Boolean active;
	
	@Column(name="is_simulated")
	private Boolean isSimulated;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(Date checkInDate) {
		this.checkInDate = checkInDate;
	}

	public Date getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(Date checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public Boolean isInWarehouse() {
		return inWarehouse;
	}

	public void setInWarehouse(Boolean inWarehouse) {
		this.inWarehouse = inWarehouse;
	}

	public Package getPack() {
		return pack;
	}

	public void setPack(Package pack) {
		this.pack = pack;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getInWarehouse() {
		return inWarehouse;
	}

	public Boolean isSimulated() {
		return isSimulated;
	}

	public void setSimulated(Boolean isSimulated) {
		this.isSimulated = isSimulated;
	}
	
	
	
}
