package pucp.dp1.redex.services.impl.storage;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.storage.IPackage;
import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.model.storage.PackageStatus;
import pucp.dp1.redex.services.dao.storage.IPackageService;

@Service
public class PackageService implements IPackageService{

	@Autowired
	private IPackage dao;
	
//	@Autowired
//	private IRoutePlan daoRoutePlan;
	
	@Override
	public List<Package> findAll() {
		return dao.findByActiveTrue();
	}

	@Override
	public void deleteSimulated(PackageStatus status) {
		dao.deleteSimulated(status);
	}

//	@Override
//	public List<Package> findOutgoingPackages(Integer id) {
//		return this.dao.findOutgoingPackages(id);
//	}

//	@Override
//	public List<Package> findArrivingPackages(Integer id) {
//		List<Package> listPackage = new ArrayList<>();
//		List<RoutePlan> listRoutePlan = this.daoRoutePlan.findAll();
//		for(RoutePlan route: listRoutePlan) {
//			Integer size = route.getFlightPlans().size();
//			Integer currentStage = route.getCurrentStage();
//			if(currentStage < size) {
//				if(id == route.getFlightPlans().get(currentStage).getFlight().getArrivalAirport().getId()) {
//					Optional<Package> packFind = this.dao.findArrivingPackages(route.getId());
//					if(packFind.isPresent()) {
//						listPackage.add(packFind.get());
//					}
//				}	
//			}
//		}
//		return listPackage;
//	}
	
}
