package pucp.dp1.redex.services.dao.sales;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.sales.Airport;

public interface IAirportService {

	public List<Airport> findAll();
	
	public Optional<Airport> findByCity_id(Integer id);
	
	public Optional<Airport> findByCode(String code);
	
}
