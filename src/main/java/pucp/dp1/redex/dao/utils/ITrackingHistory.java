package pucp.dp1.redex.dao.utils;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.utils.TrackingHistory;

public interface ITrackingHistory extends JpaRepository<TrackingHistory, Integer>{

	List<TrackingHistory> findByDispatch_id(Integer id);
	
}
