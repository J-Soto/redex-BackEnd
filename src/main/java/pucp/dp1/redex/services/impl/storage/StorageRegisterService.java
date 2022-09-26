package pucp.dp1.redex.services.impl.storage;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pucp.dp1.redex.dao.route.IRoutePlan;
import pucp.dp1.redex.dao.sales.IDispatch;
import pucp.dp1.redex.dao.storage.IPackage;
import pucp.dp1.redex.dao.storage.IStorageRegister;
import pucp.dp1.redex.dao.utils.ITrackingHistory;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.model.sales.Dispatch;
import pucp.dp1.redex.model.sales.DispatchStatus;
import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.model.storage.StorageRegister;
import pucp.dp1.redex.model.utils.TrackingHistory;
import pucp.dp1.redex.router.algorithms.AStar;
import pucp.dp1.redex.services.dao.storage.IStorageRegisterService;

@Service
public class StorageRegisterService implements IStorageRegisterService{
	
	@Autowired
	private IStorageRegister dao;
	
	@Autowired
	private IRoutePlan daoRoutePlan;
	
	@Autowired
	private IDispatch daoDispatch;
	
	@Autowired
	private IPackage daoPackage;
	
	@Autowired
	private ITrackingHistory daoTracking;
	
	@Autowired
	private AStar utils;
	
	@Override
	public StorageRegister save(StorageRegister register) {
		return this.dao.save(register);
	}

	@Override
	public List<StorageRegister> findByWarehouse_id(Integer id) {
		return this.dao.findByWarehouse_id(id);
	}

	@Override
	public List<StorageRegister> findAllPresentOnDate(Date date, Integer id) {
		return this.dao.findAllPresentOnDate(date, id);
	}

	@Override
	public void deleteSimulated() {
		this.dao.deleteSimulated();
	}

	@Override
	public String updateCheckIn(Integer idPack, Integer idWarehouse) {
		Optional<StorageRegister> sr = this.dao.findByPack_idAndWarehouse_idAndInWarehouse(idPack, idWarehouse, false);
		if(sr.isPresent()) {
			System.out.println(sr.get().getId());
			sr.get().setInWarehouse(true);
			//ACTUALIZAR HORA DE CHECKIN
			LocalDateTime dt = LocalDateTime.now().minusHours(5);
			Date d = Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
			sr.get().setCheckInDate(d); 
			this.dao.save(sr.get());
			//Actualizar ruta
			Optional<Package> pck = this.daoPackage.findById(idPack);
			if(pck.isPresent()) {
				RoutePlan rp = pck.get().getRoutePlan();
				int cont = 0;
				int size = rp.getFlightPlans().size();
				while(cont<size) {
					Integer current = rp.getFlightPlans().get(cont).getFlight().getTakeOffAirport().getId();
					System.out.println(current);
					if(current==idWarehouse) {
						Optional<RoutePlan> find_rp = this.daoRoutePlan.findById(pck.get().getRoutePlan().getId());
						if(find_rp.isPresent()) {
							find_rp.get().setCurrentStage(cont);
							this.daoRoutePlan.save(find_rp.get());
							//ACTUALIZAR TRACKING
							TrackingHistory register = new TrackingHistory();
							register.setRegisterDate(this.utils.convertToLocalDateViaInstant(sr.get().getCheckInDate()).minusDays(1));
							register.setDescription("EL PAQUETE ESTA EN EL ALMACEN DE " + sr.get().getWarehouse().getAirport().getCity().getName().toUpperCase());
							Optional<Dispatch> find = this.daoDispatch.findByPack_id(pck.get().getId());
							register.setDispatch(find.get());
							this.daoTracking.save(register);
							return "OK";
						}
					}
					//nodo final
					else if(cont+1==size)
					{
						Optional<RoutePlan> find_rp = this.daoRoutePlan.findById(pck.get().getRoutePlan().getId());
						if(find_rp.isPresent()) {
							find_rp.get().setCurrentStage(cont+1);
							this.daoRoutePlan.save(find_rp.get());
						}
						//ACTUALIZAR TRACKING
						TrackingHistory register = new TrackingHistory();
						register.setRegisterDate(this.utils.convertToLocalDateViaInstant(sr.get().getCheckInDate()).minusDays(1));
						register.setDescription("EL PAQUETE ESTA EN EL ALMACEN DE " + sr.get().getWarehouse().getAirport().getCity().getName().toUpperCase());
						Optional<Dispatch> find = this.daoDispatch.findByPack_id(pck.get().getId());
						register.setDispatch(find.get());
						this.daoTracking.save(register);

						return "OK";
					}
					cont++;
				}
			}
		}
		return "ERROR";
	}

	@Override
	public String updateCheckOut(Integer idPack, Integer idWarehouse) {
		Optional<StorageRegister> sr = this.dao.findByPack_idAndWarehouse_idAndInWarehouse(idPack, idWarehouse, true);
		if(sr.isPresent()) {
			sr.get().setInWarehouse(false);
			//ACTUALIZAR HORA DE CHECKOUT
			LocalDateTime dt = LocalDateTime.now().minusHours(5);
			Date d = Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
			sr.get().setCheckOutDate(d); 
			//verificar si es ultimo nodo
			Optional<Package> pck = this.daoPackage.findById(idPack);
			if(pck.isPresent()) {
				RoutePlan rp = pck.get().getRoutePlan();
				Integer last_node = rp.getFlightPlans().get(rp.getFlightPlans().size()-1).getFlight().getArrivalAirport().getId();
				Integer first_node = rp.getFlightPlans().get(0).getFlight().getTakeOffAirport().getId();
				System.out.println("LAST_NODE" + last_node);
				//si es el primero
				if(first_node == idWarehouse) {
					Optional<Dispatch> df = this.daoDispatch.findByPack_id(idPack);
					df.get().setStatus(DispatchStatus.EN_PROCESO);
					this.daoDispatch.save(df.get());
					//ACTUALIZAR TRACKING
					TrackingHistory register = new TrackingHistory();
					register.setRegisterDate(this.utils.convertToLocalDateViaInstant(sr.get().getCheckOutDate()).minusDays(1));
					register.setDescription("EL PAQUETE SALIO DEL ALMACEN DE " + df.get().getOriginAirport().getCity().getName().toUpperCase());
					Optional<Dispatch> find = this.daoDispatch.findByPack_id(pck.get().getId());
					register.setDispatch(find.get());
					this.daoTracking.save(register);
				}
				//si es el ultimo
				else if(last_node == idWarehouse) {
					//envio pasa a EN_DESTINO
					Optional<Dispatch> df = this.daoDispatch.findByPack_id(idPack);
					df.get().setStatus(DispatchStatus.EN_DESTINO);
					this.daoDispatch.save(df.get());
					//ACTUALIZAR TRACKING
					TrackingHistory register = new TrackingHistory();
					register.setRegisterDate(this.utils.convertToLocalDateViaInstant(sr.get().getCheckOutDate()).minusDays(1));
					register.setDescription("EL PAQUETE ESTA LISTO PARA RECOJO");
					Optional<Dispatch> find = this.daoDispatch.findByPack_id(pck.get().getId());
					register.setDispatch(find.get());
					this.daoTracking.save(register);
				} else {
					//CUALQUIER PUNTO INTERMEDIO
					//ACTUALIZAR TRACKING
					TrackingHistory register = new TrackingHistory();
					register.setRegisterDate(this.utils.convertToLocalDateViaInstant(sr.get().getCheckOutDate()).minusDays(1));
					register.setDescription("EL PAQUETE SALIO DEL ALMACEN DE " + sr.get().getWarehouse().getAirport().getCity().getName().toUpperCase());
					Optional<Dispatch> find = this.daoDispatch.findByPack_id(pck.get().getId());
					register.setDispatch(find.get());
					this.daoTracking.save(register);
					
				}
			}
			this.dao.save(sr.get());
			return "OK";
		}
		return "ERROR";
	}

}
