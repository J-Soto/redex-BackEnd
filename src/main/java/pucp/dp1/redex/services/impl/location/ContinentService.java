package pucp.dp1.redex.services.impl.location;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.location.IContinent;
import pucp.dp1.redex.model.location.Continent;
import pucp.dp1.redex.services.dao.location.IContinentService;

@Service
public class ContinentService implements IContinentService {
	
	@Autowired
	private IContinent dao;

	@Override
	public List<Continent> findAll() {
		return dao.findAll();
	}
	
}
