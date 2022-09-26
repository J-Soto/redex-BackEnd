package pucp.dp1.redex.services.dao.storage;

import java.util.Date;
import java.util.List;
import pucp.dp1.redex.model.storage.StorageRegister;

public interface IStorageRegisterService {

	public List<StorageRegister> findByWarehouse_id(Integer id);
	
	public StorageRegister save(StorageRegister register);
	
	public List<StorageRegister> findAllPresentOnDate(Date date, Integer id);
	
	public void deleteSimulated();
	
	public String updateCheckIn(Integer idPack, Integer idWarehouse);
	
	public String updateCheckOut(Integer idPack, Integer idWarehouse);
}
