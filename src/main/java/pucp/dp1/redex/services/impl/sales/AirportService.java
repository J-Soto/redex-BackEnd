package pucp.dp1.redex.services.impl.sales;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.sales.IAirport;
import pucp.dp1.redex.model.sales.Airport;
import pucp.dp1.redex.services.dao.sales.IAirportService;

@Service
public class AirportService implements IAirportService {

	@Autowired
	private IAirport dao;
	
	@Override
	public List<Airport> findAll() {
		System.out.println("Find All");
		return dao.findAll();
	}

	@Override
	public Optional<Airport> findByCity_id(Integer id) {
		return dao.findByCity_id(id);
	}
	
	@Override
	public Optional<Airport> findByCode(String code){
		return dao.findByCode(code);
	}
}
