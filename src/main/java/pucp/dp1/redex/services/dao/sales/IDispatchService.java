package pucp.dp1.redex.services.dao.sales;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pucp.dp1.redex.model.sales.Dispatch;
import pucp.dp1.redex.model.sales.DispatchStatus;
import pucp.dp1.redex.model.sales.Historico;
import pucp.dp1.redex.model.utils.TrackingHistory;

public interface IDispatchService {
	public Historico envioMuerte();
	public List<Dispatch> findByActiveTrue();
	
	public Optional<Dispatch> findByTrackingCode(String trackingcode);
	
	public List<Dispatch> findByOriginAirport_idAndStatusNotOrderByRegisterDateDesc(Integer id);
	
	public List<Dispatch> findByDestinationAirport_idAndStatusNotOrderByRegisterDateDesc(Integer id);
	
	public Dispatch save(Dispatch dispatch, Boolean simulated, Date dateCheckin);
	
	public Optional<Dispatch> updateState(String trackingCode, DispatchStatus status);
	
	public String masiveLoad(MultipartHttpServletRequest file); 
	
	public void deleteSimulated(DispatchStatus status);
	
	public List<Dispatch> findOutgoingDispatchs(Integer id);
	
	public List<Dispatch> findArrivingDispatchs(Integer id);
	
	public List<TrackingHistory> findTrackingHistory(String code);

}
