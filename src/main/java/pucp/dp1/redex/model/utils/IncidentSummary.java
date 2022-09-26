package pucp.dp1.redex.model.utils;

import java.util.List;

public class IncidentSummary {
	
	private List<AirportElement> timeline; 
	
	private List<AirportElement> byairports;
	
	private List<FlightPlanElement> byflightplans;
	
	private SummaryCase summaryCase;

	public List<AirportElement> getTimeline() {
		return timeline;
	}

	public void setTimeline(List<AirportElement> timeline) {
		this.timeline = timeline;
	}

	public List<AirportElement> getByairports() {
		return byairports;
	}

	public void setByairports(List<AirportElement> byairports) {
		this.byairports = byairports;
	}

	public List<FlightPlanElement> getByflightplans() {
		return byflightplans;
	}

	public void setByflightplans(List<FlightPlanElement> byflightplans) {
		this.byflightplans = byflightplans;
	}

	public SummaryCase getSummaryCase() {
		return summaryCase;
	}

	public void setSummaryCase(SummaryCase summaryCase) {
		this.summaryCase = summaryCase;
	}
}
