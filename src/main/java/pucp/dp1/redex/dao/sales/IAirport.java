package pucp.dp1.redex.dao.sales;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pucp.dp1.redex.model.sales.Airport;

public interface IAirport extends JpaRepository<Airport, Integer>{

	@Query("SELECT a FROM Airport as a")
	List<Airport> findAll();
	
	Optional<Airport> findByCity_id(Integer id);
	
	Optional<Airport> findByCode(String code);

}

