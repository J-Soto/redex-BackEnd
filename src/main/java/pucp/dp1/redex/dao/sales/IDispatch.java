package pucp.dp1.redex.dao.sales;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pucp.dp1.redex.model.sales.Dispatch;
import pucp.dp1.redex.model.sales.DispatchStatus;

public interface IDispatch extends JpaRepository<Dispatch, Integer>{
	
	List<Dispatch> findByActiveTrue();
	
	Optional<Dispatch> findByTrackingCode(String trackingcode);
	
	List<Dispatch> findByOriginAirport_idAndStatusNotOrderByRegisterDateDesc(Integer id, DispatchStatus status);
	
	List<Dispatch> findByDestinationAirport_idAndStatusNotOrderByRegisterDateDesc(Integer id, DispatchStatus status);
	
	@Transactional
	@Modifying
	@Query(value = "update Dispatch d set d.active = false where d.status = :status")
	void deleteSimulated(@Param("status") DispatchStatus status);
	
	Optional<Dispatch> findByPack_id(Integer id);
}
