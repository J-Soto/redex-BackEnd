package pucp.dp1.redex.services.dao.storage;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.model.utils.AirportElement;

public interface IWarehouseService {

	public List<Warehouse> findAll();
	
	public Optional<Warehouse> findByAirport_id(Integer id);
	
	public Optional<Warehouse> updateCapacity(Integer id, Integer capacity);
	
	public List<AirportElement> getTimeLineWarehouse(Date date, Boolean simulated);
	
	public Optional<Warehouse> findByAirport_code(String code);
	
	public List<AirportElement> findByRange(Boolean simulated, Integer id, Date idate, Date fdate);
	
}
