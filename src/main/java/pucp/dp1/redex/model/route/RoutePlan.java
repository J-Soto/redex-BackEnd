package pucp.dp1.redex.model.route;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "route_plan")
public class RoutePlan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "route_plan_id")
	private int id;
	
	@Column(name = "currentStage")
	private Integer currentStage;
	
	@Column(name = "status")
	private RoutePlanStatus status;
	
	@Column(name = "estimated_time")
	private Double estimatedTime;
	
	@ManyToMany
	@JoinTable(name = "routeplan_flightplan", joinColumns = @JoinColumn(name = "route_plan_id"),
			inverseJoinColumns = @JoinColumn(name = "flight_plan_id"))
	@JsonIgnoreProperties("routePlan")
	private List<FlightPlan> flightPlans;

	@Column(name = "packages")
	private Integer packages;
	public Integer getPackages() {
		return this.packages;
	}

	public void setPackages(final Integer packages) {
		this.packages = packages;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<FlightPlan> getFlightPlans() {
		return flightPlans;
	}

	public void setFlightPlans(List<FlightPlan> flightPlans) {
		this.flightPlans = flightPlans;
	}

	public Integer getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(Integer currentStage) {
		this.currentStage = currentStage;
	}

	public RoutePlanStatus getStatus() {
		return status;
	}

	public void setStatus(RoutePlanStatus status) {
		this.status = status;
	}

	public Double getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(Double estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	
}
