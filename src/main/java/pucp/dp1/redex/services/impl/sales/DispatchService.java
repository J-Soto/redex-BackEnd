package pucp.dp1.redex.services.impl.sales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pucp.dp1.redex.dao.location.ICountry;
import pucp.dp1.redex.dao.route.IFlightPlan;
import pucp.dp1.redex.dao.route.IRoutePlan;
import pucp.dp1.redex.dao.sales.IAirport;
import pucp.dp1.redex.dao.sales.IClient;
import pucp.dp1.redex.dao.sales.IDispatch;
import pucp.dp1.redex.dao.sales.IHistorico;
import pucp.dp1.redex.dao.storage.IPackage;
import pucp.dp1.redex.dao.storage.IStorageRegister;
import pucp.dp1.redex.dao.storage.IWarehouse;
import pucp.dp1.redex.dao.utils.ISummaryCase;
import pucp.dp1.redex.dao.utils.ITrackingHistory;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.model.sales.Dispatch;
import pucp.dp1.redex.model.sales.DispatchStatus;
import pucp.dp1.redex.model.sales.Historico;
import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.model.storage.StorageRegister;
import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.model.utils.TrackingHistory;
import pucp.dp1.redex.router.algorithms.AStar;
import pucp.dp1.redex.services.dao.sales.IDispatchService;

@Service
public class DispatchService implements IDispatchService {

	@Autowired
	private IDispatch dao;
	@Autowired
	private IClient daoClient;
	@Autowired
	private IFlightPlan daoFlightPlan;
	@Autowired
	private IPackage daoPackage;
	@Autowired
	private IRoutePlan daoRoutePlan;
	@Autowired
	private ISummaryCase daoSummary;
	@Autowired
	private IWarehouse daoWarehouse;
	@Autowired
	private IStorageRegister daoStorageRegister;
	@Autowired
	private ITrackingHistory daoTracking;
	@Autowired
	private AStar serviceAStart;
	@Autowired
	private IAirport daoAirport;
	@Autowired
	private ICountry daoCountry;
	@Autowired
	private HistoricoService historicoService;
	@Autowired
	private IHistorico daoHistorico;
	@Override
	public List<Dispatch> findByActiveTrue() {
		return this.dao.findByActiveTrue();
	}
	@Override
	public Optional<Dispatch> findByTrackingCode(String trackingcode) {
		return this.dao.findByTrackingCode(trackingcode);
	}
	@Override
	public List<Dispatch> findByOriginAirport_idAndStatusNotOrderByRegisterDateDesc(Integer id) {
		return this.dao.findByOriginAirport_idAndStatusNotOrderByRegisterDateDesc(id,DispatchStatus.SIMULADO);
	}
	@Override
	public List<Dispatch> findByDestinationAirport_idAndStatusNotOrderByRegisterDateDesc(Integer id) {
		return this.dao.findByDestinationAirport_idAndStatusNotOrderByRegisterDateDesc(id, DispatchStatus.SIMULADO);
	}
	@Override
	public Dispatch save(Dispatch dispatch, Boolean simulated, Date dateCheckIn) {
		dispatch.setActive(true);
		dispatch.getPack().setActive(true);
		// guardar clientes
		// actualizar flight plans
		List<FlightPlan> plans = dispatch.getPack().getRoutePlan().getFlightPlans();
		// sirve para guardar el aeropuerto destino como atributo de envio
		Integer listSize = plans.size();
		Integer i = 0;
		//System.out.println(listSize);
		for (FlightPlan plan : plans) {
			Optional<FlightPlan> actual_plan = this.daoFlightPlan.findById(plan.getId());
			if (actual_plan.isPresent()) {
				/*
				if(simulated) {
					actual_plan.get().setPackagesNumberSimulated(actual_plan.get().getPackagesNumberSimulated() + 1);
					if(actual_plan.get().getPackagesNumberSimulated()>=actual_plan.get().getFlight().getCapacity()) {
						actual_plan.get().setFullSimulated(true);
					}
				}
				else {
					actual_plan.get().setPackagesNumber(actual_plan.get().getPackagesNumber() + 1);
					if(actual_plan.get().getPackagesNumber()>=actual_plan.get().getFlight().getCapacity()) {
						actual_plan.get().setFull(true);
					}
				}

				*/

				this.daoFlightPlan.save(actual_plan.get());
				if (listSize - 1 == i) {
					dispatch.setDestinationAirport(actual_plan.get().getFlight().getArrivalAirport());
				} else {
					i++;
				}
			} else {
				/*
				if(simulated) {
					plan.setPackagesNumberSimulated(plan.getPackagesNumberSimulated() + 1);
					plan.setFullSimulated(false);
				}
				else {
					plan.setPackagesNumber(plan.getPackagesNumber() + 1);
					plan.setFull(false);
				}
				*/

				this.daoFlightPlan.save(plan);
				if (listSize - 1 == i) {
					dispatch.setDestinationAirport(plan.getFlight().getArrivalAirport());
				} else {
					i++;
				}
			}
		}
		//fecha estimada de fin
		//dispatch.setEndDate((this.serviceAStart.convertDateAndTimeToDate(plans.get(plans.size()-1).getArrivalDate(), plans.get(plans.size()-1).getFlight().getArrivalTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
		// guardar ruta
		this.daoRoutePlan.save(dispatch.getPack().getRoutePlan());
		this.daoPackage.save(dispatch.getPack());
		RoutePlan rp = dispatch.getPack().getRoutePlan();
		Optional<Warehouse> oWare = this.daoWarehouse.findByAirport_id(rp.getFlightPlans().get(0).getFlight().getTakeOffAirport().getId());
		Dispatch dispatchSave = this.dao.save(dispatch);
		return dispatchSave;
	}
	@Override
	public Optional<Dispatch> updateState(String trackingCode, DispatchStatus status) {
		Optional<Dispatch> findDispatch = this.dao.findByTrackingCode(trackingCode);
		if (findDispatch.isPresent()) {
			if (status.equals(DispatchStatus.ATRASADO))
				findDispatch.get().setStatus(DispatchStatus.ATRASADO);
			else if (status.equals(DispatchStatus.CANCELADO))
				findDispatch.get().setStatus(DispatchStatus.CANCELADO);
			else if (status.equals(DispatchStatus.EN_PROCESO))
				findDispatch.get().setStatus(DispatchStatus.EN_PROCESO);
			else if (status.equals(DispatchStatus.FINALIZADO))
				findDispatch.get().setStatus(DispatchStatus.FINALIZADO);
			else if (status.equals(DispatchStatus.RECIBIDO))
				findDispatch.get().setStatus(DispatchStatus.RECIBIDO);
			else
				return Optional.empty();
			findDispatch.get().setEndDate(LocalDateTime.now());
			//this.dao.save(findDispatch.get());
			return findDispatch;
		} else {
			return Optional.empty();
		}
	}
	class PackageTemp {
		String originAirport;
		String destinationAirport;
		LocalDate date;
		LocalTime time;
		Integer cantPackages;
		public LocalTime toUtc(Integer utc) {
			LocalTime aux;
			aux=this.time;
			if(utc>0){
				time=time.minusHours(utc);
				if(time.isAfter(aux)) {
					date=date.minusDays(1);
				}
			}
			else{
				utc*=-1;
				time=time.plusHours(utc);
				if(time.isBefore(aux)) {
					date=date.plusDays(1);
				}
			}
			return time;
		}
		public PackageTemp(final String originAirport, final String destinationAirport, final LocalDate date, final LocalTime time, final Integer cantPackages,Integer utc) {
			this.originAirport = originAirport;
			this.destinationAirport = destinationAirport;
			this.date = date;
			this.time = time;
			this.time=toUtc(utc);
			this.cantPackages = cantPackages;
		}
		public String getOriginAirport() {
			return this.originAirport;
		}

		public void setOriginAirport(final String originAirport) {
			this.originAirport = originAirport;
		}

		public String getDestinationAirport() {
			return this.destinationAirport;
		}

		public void setDestinationAirport(final String destinationAirport) {
			this.destinationAirport = destinationAirport;
		}

		public LocalDate getDate() {
			return this.date;
		}

		public void setDate(final LocalDate date) {
			this.date = date;
		}

		public LocalTime getTime() {
			return this.time;
		}

		public void setTime(final LocalTime time) {
			this.time = time;
		}

		public Integer getCantPackages() {
			return this.cantPackages;
		}

		public void setCantPackages(final Integer cantPackages) {
			this.cantPackages = cantPackages;
		}
	}
	public LocalDate convertToLocalDateViaInstant(LocalDate dateToConvert) {
		return LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateToConvert));
	}
	public LocalTime convertStringToLocalTime(String time) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return LocalTime.parse(time, formatter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	class PackageComparator implements Comparator<Historico>{
		public int compare(Historico a, Historico b) {
			if ( a.getHora().isAfter(b.getHora()) ) return 1;
			else if (a.getHora().isBefore(b.getHora())) return -1;
			return 0;
		}
	}

	/*
	public LocalDate convertStringToLocalDate(String date) {
		try {
			SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
			Date dateDate;
			dateDate = formatterDate.parse(date);
			return convertToLocalDateViaInstant(dateDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	*/
	@Override
	public String masiveLoad(MultipartHttpServletRequest request) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		try {
			List<Historico> historicos = new ArrayList<Historico>();
			String datereq = request.getParameter("date").substring(1, 11).replace("-", "");
			String horaI = request.getParameter("horai");
			String horaF = request.getParameter("horaf");
			Integer resultPlan =1, dias =0;
			Boolean fallo = false, cinco =true;
			//Creamos el date con el cual se buscara la lista de envios historicos a partir del date enviado por front (fecha inicio)
			SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
			Date dateDate;
			dateDate = formatterDate.parse(datereq);

			//Creamos un Local date con la fecha inicio enviada por front para poder ir avanzando en el tiempo
			LocalDate date1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateDate));
			LocalTime time1 = convertStringToLocalTime(horaI);
			LocalTime time2 = convertStringToLocalTime(horaF);
			// PROCESAR ALGORITMO
			//while(!fallo && (dias != 3 || !cinco) ) {
			//le enviamos la fecha Date yyyyMMdd para que retorne la lista de envios historicos de esa fecha
			historicos = daoHistorico.findByFechaHora(date1,time1,time2);

			//Inicio :07:56

			//enviamos los envios historicos al algoritmo a ver si falla
			fallo =procesarAlgoritmo(historicos);
			if (fallo) return "COLAPSO";

			//Fin :08:04

			//Si no fallo, entonces aumentamos el LocalDate en 1 dia y los dias procesados en 1
			//date1 = date1.plusDays(1);
			//dias +=1;

			//Asignamos la fecha aumentada al date para que busque los encios historicos del dia siguiente
			//dateDate = Date.from(date1.atStartOfDay(ZoneId.systemDefault()).toInstant());
			//}
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return "COLAPSO";
		}
	}
	private Boolean procesarAlgoritmo(List<Historico> pqHistoricos) {

		for (Historico pack : pqHistoricos) {
			Integer	resultPlan = serviceAStart.insertHistoricPackage(pack.getCodigoPaisSalida(), pack.getCodigoPaisLlegada(), pack.getFecha(),pack.getHora(), pack.getNroPaquetes());
			if (resultPlan != 1) {
				System.out.println(pack.getCodigoPaisSalida() + "  " + pack.getFecha()+ " " + pack.getHora() + " " + pack.getCodigoPaisLlegada() + " " + pack.getNroPaquetes());
				System.out.println("El sistema fallo");
				return true;
			}

		}
		return false;
	}
	private void insetar(List<PackageTemp> pq, PackageTemp newPackage) {
		for(PackageTemp p :pq){
			
		}
	}
	@Override
	public void deleteSimulated(DispatchStatus status){
		this.dao.deleteSimulated(status);
	}
	@Override
	public List<Dispatch> findOutgoingDispatchs(Integer id) {
		List<Package> outgoingPackages = this.daoPackage.findOutgoingPackages(id);
		List<Dispatch> outgoingDispatchs = new ArrayList<>();
		for(Package pack : outgoingPackages) {
			System.out.println(pack.getId());
			Optional<Dispatch> fdispatch = this.dao.findByPack_id(pack.getId());
			if(fdispatch.isPresent())
				outgoingDispatchs.add(fdispatch.get());
		}
		Collections.sort(outgoingDispatchs, Comparator.comparing(Dispatch::getRegisterDate));
		Collections.reverse(outgoingDispatchs);
		return outgoingDispatchs;
	}
	@Override
	public List<Dispatch> findArrivingDispatchs(Integer id) {
		List<Dispatch> arrivingDispatchs = new ArrayList<>();
		List<Package> listPackage = new ArrayList<>();
		List<RoutePlan> listRoutePlan = this.daoRoutePlan.findAll();
		for(RoutePlan route: listRoutePlan) {
			Integer size = route.getFlightPlans().size();
			Integer currentStage = route.getCurrentStage();
			if(currentStage < size) {
				if(id == route.getFlightPlans().get(currentStage).getFlight().getArrivalAirport().getId()) {
					Optional<Package> packFind = this.daoPackage.findArrivingPackages(route.getId());
					if(packFind.isPresent()) {
						//VERIFICAR QUE YA SALIÓ DEL NODO ANTERIOR
						Optional<StorageRegister> optSr = this.daoStorageRegister.findByPack_idAndWarehouse_idAndInWarehouse(packFind.get().getId(),
								route.getFlightPlans().get(currentStage).getFlight().getTakeOffAirport().getId(), false);
						if(optSr.isPresent()) {
							listPackage.add(packFind.get());							
						}
					}
				}	
			}
		}
		for(Package pack: listPackage) {
			Optional<Dispatch> fdispatch = this.dao.findByPack_id(pack.getId());
			if(fdispatch.isPresent())
				arrivingDispatchs.add(fdispatch.get());
		}
		//Ordenar
		Collections.sort(arrivingDispatchs, Comparator.comparing(Dispatch::getRegisterDate));
		Collections.reverse(arrivingDispatchs);
		return arrivingDispatchs;
	}
	@Override
	public List<TrackingHistory> findTrackingHistory(String code) {
		Optional<Dispatch> fdispatch = this.dao.findByTrackingCode(code);
		if(fdispatch.isPresent())
			return this.daoTracking.findByDispatch_id(fdispatch.get().getId());
		else
			return null;
	}
}
