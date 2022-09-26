package pucp.dp1.redex.model.utils;

import java.util.List;

import pucp.dp1.redex.model.sales.Dispatch;

public class TrackingList {
	
	private Dispatch dispatch;
	
	private List<TrackingHistory> history;

	public Dispatch getDispatch() {
		return dispatch;
	}

	public void setDispatch(Dispatch dispatch) {
		this.dispatch = dispatch;
	}

	public List<TrackingHistory> getHistory() {
		return history;
	}

	public void setHistory(List<TrackingHistory> history) {
		this.history = history;
	}

}
