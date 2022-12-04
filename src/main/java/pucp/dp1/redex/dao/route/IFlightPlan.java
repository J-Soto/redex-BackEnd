package pucp.dp1.redex.dao.route;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import pucp.dp1.redex.model.route.FlightPlan;

public interface IFlightPlan extends JpaRepository<FlightPlan, Integer>{

	List<FlightPlan> findAll();
	
	Optional<FlightPlan> findById(Integer id);
	
	Optional<FlightPlan> findByFlight_IdFlightAndTakeOffDate(Integer id, Date date);
	
	@Transactional
	@Modifying
	@Query(value = "update FlightPlan f set f.fullSimulated = false, f.packagesNumberSimulated=0 where f.id>0")
	void cleanSimulated();

	@Query("SELECT fp FROM FlightPlan fp, Flight f where fp.id = f.id and fp.takeOffDate = :fecha AND f.takeOffTime between :horaI AND :horaF")
	List<FlightPlan> findByFechaHora(Date fecha, Time horaI, Time horaF);
	
}
