package pucp.dp1.redex.services.dao.location;

import java.util.List;
import java.util.Optional;

import pucp.dp1.redex.model.location.City;

public interface ICityService {

	List<City> findAll();

	Optional<City> findByCountry_id(Integer id);
}
