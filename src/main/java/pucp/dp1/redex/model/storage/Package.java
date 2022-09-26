package pucp.dp1.redex.model.storage;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import pucp.dp1.redex.model.route.RoutePlan;

@Entity
@Table(name = "package")
public class Package implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "package_id")
	private int id;
	
	@Column(name = "weight")
	private Double weight;
	
	@Column(name = "large")
	private Double large;
	
	@Column(name = "width")
	private Double width;
	
	@Column(name = "high")
	private Double high;
	
	@Column(name = "fragile")
	private Boolean fragile;
	
	@Column(name = "status")
	private PackageStatus status;
	
	@Column(name = "description")
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "route_plan_id")
	private RoutePlan routePlan;
	
	@Column(name="active")
	private Boolean active;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Boolean getFragile() {
		return fragile;
	}

	public void setFragile(Boolean fragile) {
		this.fragile = fragile;
	}

	public PackageStatus getStatus() {
		return status;
	}

	public void setStatus(PackageStatus status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RoutePlan getRoutePlan() {
		return routePlan;
	}

	public void setRoutePlan(RoutePlan routePlan) {
		this.routePlan = routePlan;
	}

	public Double getLarge() {
		return large;
	}

	public void setLarge(Double large) {
		this.large = large;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
