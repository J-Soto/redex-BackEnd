package pucp.dp1.redex.dao.storage;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pucp.dp1.redex.model.storage.StorageRegister;

public interface IStorageRegister extends JpaRepository<StorageRegister, Integer> {

	List<StorageRegister> findByWarehouse_id(Integer id);
	
	@Query("select s from StorageRegister s where s.checkInDate <= :date and (s.checkOutDate >= :date or s.checkOutDate is null) and s.warehouse.id = :id and s.active = true")
	List<StorageRegister> findAllPresentOnDate(@Param("date") Date date, @Param("id") Integer id);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update StorageRegister s set s.active = false where s.isSimulated = true")
	void deleteSimulated();
	
	Optional<StorageRegister> findByPack_idAndWarehouse_idAndInWarehouse(Integer idPack, Integer idWarehouse, Boolean b);
}
