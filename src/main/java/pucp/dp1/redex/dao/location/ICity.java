package pucp.dp1.redex.dao.location;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pucp.dp1.redex.model.location.City;

public interface ICity extends JpaRepository<City, Integer>{

	List<City> findAll();
	
	Optional<City> findByCountry_id(Integer id);
}
