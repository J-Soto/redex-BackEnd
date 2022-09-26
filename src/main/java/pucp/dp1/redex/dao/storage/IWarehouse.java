package pucp.dp1.redex.dao.storage;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.model.utils.AirportElement;

public interface IWarehouse extends JpaRepository<Warehouse, Integer> {
	List<Warehouse> findAll();
	
	Optional<Warehouse> findByAirport_id(Integer id);
	
	@Query("select new pucp.dp1.redex.model.utils.AirportElement(i.airport.code, i.airport.description, i.registerDate, i.airport.city.name, i.airport.city.country.name, count(*)) from Incident i where i.airport.id=:id and i.active = true and i.isSimulated = :simulated and i.registerDate>=:idate and i.registerDate <=:fdate group by i.airport.id, i.registerDate order by count(*) desc")
	List<AirportElement> findByRange(@Param("simulated") Boolean simulated, @Param("id") Integer id, @Param("idate") Date idate, @Param("fdate") Date fdate);
	
	@Query("select new pucp.dp1.redex.model.utils.AirportElement(s.warehouse.airport.code, s.warehouse.airport.city.name , s.warehouse.airport.city.country.name, count(*)) from StorageRegister s where s.checkInDate <= :date and (s.checkOutDate >= :date or s.checkOutDate is null) and s.active=true and s.isSimulated = :simulated group by s.warehouse.id")
	List<AirportElement> getTimeLineWarehouse(@Param("date") Date date, @Param("simulated") Boolean simulated);
	
	Optional<Warehouse> findByAirport_code(String code);
	
}
