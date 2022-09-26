package pucp.dp1.redex.dao.storage;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.model.storage.PackageStatus;

public interface IPackage extends JpaRepository<Package, Integer> {
	
	List<Package> findByActiveTrue();

	@Transactional
	@Modifying
	@Query(value = "update Package p set p.active = false where p.status = :status")
	void deleteSimulated(@Param("status") PackageStatus status);
	
	@Query(value = "select s.pack from StorageRegister s where s.warehouse.id=:id and s.inWarehouse = true and s.isSimulated = false")
	List<Package> findOutgoingPackages(@Param("id") Integer id);
	
	@Query(value = "select s.pack from StorageRegister s where s.pack.routePlan.id=:id and s.isSimulated = false")
	Optional<Package> findArrivingPackages(Integer id);
}
