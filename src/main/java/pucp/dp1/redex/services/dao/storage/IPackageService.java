package pucp.dp1.redex.services.dao.storage;

import java.util.List;

import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.model.storage.PackageStatus;

public interface IPackageService {
	
	public List<Package> findAll();
	
	public void deleteSimulated(PackageStatus status);
	
//	public List<Package> findOutgoingPackages(Integer id);
	
//	public List<Package> findArrivingPackages(Integer id);

}
