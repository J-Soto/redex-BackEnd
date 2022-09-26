package pucp.dp1.redex.model.sales;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pucp.dp1.redex.model.storage.Package;

@Entity
@Table(name = "dispatch")
public class Dispatch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "trackingCode")
	private String trackingCode;
	
	@Column(name = "registerDate")
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDateTime registerDate;
	
	@Column(name = "endDate")
	@JsonFormat(pattern="dd/MM/yyyy")
	private LocalDateTime endDate;
	
	@Column(name = "status")
	private DispatchStatus status;
	
	@Column(name = "late")
	private Boolean late;
	
	@ManyToOne
	@JsonIgnoreProperties("orders")
	private Client send_client;
	
	@Column(name = "receive_client_lastname")
	private String receiveClientLastname;
	
	@Column(name = "receive_client_name")
	private String receiveClientName;
	
	@Column(name = "receive_client_document")
	private String receiveClientDocument;
	
	@ManyToOne
	@JoinColumn(name = "origin_airport_id")
	@JsonIgnoreProperties("warehouse")
	private Airport originAirport;
	
	@ManyToOne
	@JoinColumn(name = "destination_airport_id")
	@JsonIgnoreProperties("warehouse")
	private Airport destinationAirport;
	
	@OneToOne
	@JoinColumn(name = "package_id")
	@JsonIgnoreProperties("dispatch")
	private Package pack;

	@Column(name="active")
	private Boolean active;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTrackingCode() {
		return trackingCode;
	}

	public void setTrackingCode(String trackingCode) {
		this.trackingCode = trackingCode;
	}

	public LocalDateTime getRegisterDate() {
		return registerDate.plusHours(5);
	}

	public void setRegisterDate(LocalDateTime registerDate) {
		this.registerDate = registerDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public DispatchStatus getStatus() {
		return status;
	}

	public void setStatus(DispatchStatus status) {
		this.status = status;
	}

	public Boolean isLate() {
		return late;
	}

	public void setLate(Boolean late) {
		this.late = late;
	}

	public Client getSend_client() {
		return send_client;
	}

	public void setSend_client(Client send_client) {
		this.send_client = send_client;
	}

	public Boolean getLate() {
		return late;
	}

	public Airport getOriginAirport() {
		return originAirport;
	}

	public void setOriginAirport(Airport originAirport) {
		this.originAirport = originAirport;
	}

	public Airport getDestinationAirport() {
		return destinationAirport;
	}

	public void setDestinationAirport(Airport destinationAirport) {
		this.destinationAirport = destinationAirport;
	}

	public Package getPack() {
		return pack;
	}

	public void setPack(Package pack) {
		this.pack = pack;
	}

	public String getReceiveClientLastname() {
		return receiveClientLastname;
	}

	public void setReceiveClientLastname(String receiveClientLastname) {
		this.receiveClientLastname = receiveClientLastname;
	}

	public String getReceiveClientName() {
		return receiveClientName;
	}

	public void setReceiveClientName(String receiveClientName) {
		this.receiveClientName = receiveClientName;
	}

	public String getReceiveClientDocument() {
		return receiveClientDocument;
	}

	public void setReceiveClientDocument(String receiveClientDocument) {
		this.receiveClientDocument = receiveClientDocument;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}
