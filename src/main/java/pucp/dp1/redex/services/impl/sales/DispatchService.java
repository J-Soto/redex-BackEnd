package pucp.dp1.redex.services.impl.sales;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pucp.dp1.redex.dao.route.IFlightPlan;
import pucp.dp1.redex.dao.route.IRoutePlan;
import pucp.dp1.redex.dao.sales.IClient;
import pucp.dp1.redex.dao.sales.IDispatch;
import pucp.dp1.redex.dao.storage.IPackage;
import pucp.dp1.redex.dao.storage.IStorageRegister;
import pucp.dp1.redex.dao.storage.IWarehouse;
import pucp.dp1.redex.dao.utils.ISummaryCase;
import pucp.dp1.redex.dao.utils.ITrackingHistory;
import pucp.dp1.redex.model.route.FlightPlan;
import pucp.dp1.redex.model.route.RoutePlan;
import pucp.dp1.redex.model.sales.Dispatch;
import pucp.dp1.redex.model.sales.DispatchStatus;
import pucp.dp1.redex.model.storage.Package;
import pucp.dp1.redex.model.storage.StorageRegister;
import pucp.dp1.redex.model.storage.Warehouse;
import pucp.dp1.redex.model.utils.AirportElement;
import pucp.dp1.redex.model.utils.SummaryCase;
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
	//CAMBIO URGENTE
	@Override
	public Dispatch save(Dispatch dispatch, Boolean simulated, Date dateCheckIn) {
		dispatch.setActive(true);
		dispatch.getPack().setActive(true);
		Random rnd = new Random();
		String trackingCode = String.format("%06d", rnd.nextInt(999999));
		dispatch.setRegisterDate(LocalDateTime.now().minusHours(5));
		dispatch.setTrackingCode(trackingCode);
		// guardar clientes
		//dispatch.getSend_client().setRegisterDate(LocalDateTime.now().minusHours(5));
		//System.out.println("Crear cliente");
		//this.daoClient.save(dispatch.getSend_client());
		// actualizar flight plans
		List<FlightPlan> plans = dispatch.getPack().getRoutePlan().getFlightPlans();
		// sirve para guardar el aeropuerto destino como atributo de envio
		Integer listSize = plans.size();
		Integer i = 0;
		//System.out.println(listSize);
		for (FlightPlan plan : plans) {
			Optional<FlightPlan> actual_plan = this.daoFlightPlan.findById(plan.getId());
			if (actual_plan.isPresent()) {
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
				this.daoFlightPlan.save(actual_plan.get());
				if (listSize - 1 == i) {
					dispatch.setDestinationAirport(actual_plan.get().getFlight().getArrivalAirport());
				} else {
					i++;
				}
			} else {
				if(simulated) {
					plan.setPackagesNumberSimulated(plan.getPackagesNumberSimulated() + 1);
					plan.setFullSimulated(false);
				}
				else {
					plan.setPackagesNumber(plan.getPackagesNumber() + 1);
					plan.setFull(false);
				}
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
		//Registros de almacen
		RoutePlan rp = dispatch.getPack().getRoutePlan();
		/* Primer Storage Register */
		StorageRegister sr = new StorageRegister();
		sr.setPack(dispatch.getPack());
		LocalDateTime dt = LocalDateTime.now().minusHours(5);
		Date checkIn = Date.from(dt.atZone(ZoneId.systemDefault()).toInstant()); 
		Date checkOut = this.serviceAStart.convertDateAndTimeToDate(rp.getFlightPlans().get(0).getTakeOffDate(),
				rp.getFlightPlans().get(0).getFlight().getTakeOffTime());
		if(simulated) {
			checkIn=dateCheckIn;
		}
		sr.setCheckInDate(checkIn);
		sr.setCheckOutDate(checkOut);
		Optional<Warehouse> oWare = this.daoWarehouse.findByAirport_id(rp.getFlightPlans().get(0).getFlight().getTakeOffAirport().getId());
		if (oWare.isPresent()) {
			Warehouse warehouse = oWare.get();
			sr.setWarehouse(warehouse);
		} else {
			System.out.println("No hay almacen? SR");
		}
		sr.setInWarehouse(true);
		sr.setActive(true);
		sr.setSimulated(simulated);
		this.daoStorageRegister.save(sr);
		Integer j = 0;
		// while (j < listSize) {
		// 	/* Crear StorageRegister */
		// 	if (j < listSize - 1) {
		// 		sr = new StorageRegister();
		// 		sr.setPack(dispatch.getPack());
		// 		checkIn = this.serviceAStart.convertDateAndTimeToDate(rp.getFlightPlans().get(j).getArrivalDate(),
		// 				rp.getFlightPlans().get(j).getFlight().getArrivalTime());
		// 		checkOut = this.serviceAStart.convertDateAndTimeToDate(rp.getFlightPlans().get(j + 1).getTakeOffDate(),
		// 				rp.getFlightPlans().get(j + 1).getFlight().getTakeOffTime());
		// 		sr.setCheckInDate(checkIn);
		// 		sr.setCheckOutDate(checkOut);
		// 		oWare = this.daoWarehouse.findByAirport_id(
		// 				rp.getFlightPlans().get(j).getFlight().getArrivalAirport().getId());
		// 		if (oWare.isPresent()) {
		// 			Warehouse warehouse = oWare.get();
		// 			sr.setWarehouse(warehouse);
		// 		} else {
		// 			System.out.println("No hay almacen? SR");
		// 		}
		// 		sr.setInWarehouse(false);
		// 		sr.setActive(true);
		// 		sr.setSimulated(simulated);
		// 		this.daoStorageRegister.save(sr);
		// 	} else {
		// 		sr = new StorageRegister();
		// 		sr.setPack(dispatch.getPack());
		// 		checkIn = this.serviceAStart.convertDateAndTimeToDate(rp.getFlightPlans().get(j).getArrivalDate(),
		// 				rp.getFlightPlans().get(j).getFlight().getArrivalTime());
		// 		sr.setCheckInDate(checkIn);
		// 		oWare = this.daoWarehouse.findByAirport_id(
		// 				rp.getFlightPlans().get(j).getFlight().getArrivalAirport().getId());
		// 		if (oWare.isPresent()) {
		// 			Warehouse warehouse = oWare.get();
		// 			sr.setWarehouse(warehouse);
		// 		} else {
		// 			System.out.println("No hay almacen? SR");
		// 		}
		// 		sr.setInWarehouse(false);
		// 		sr.setActive(true);
		// 		sr.setSimulated(simulated);
		// 		if(!simulated) {
		// 			this.daoStorageRegister.save(sr);
		// 		}
		// 		else {
		// 			Date checkcoutFake = this.serviceAStart.convertDateAndTimeToDate(rp.getFlightPlans().get(j).getArrivalDate(),
		// 					rp.getFlightPlans().get(j).getFlight().getArrivalTime());
		// 			Calendar c = Calendar.getInstance();
		// 			c.setTime(checkcoutFake);
		// 			c.add(Calendar.DATE, 1);
		// 			checkcoutFake=c.getTime();
		// 			sr.setCheckOutDate(checkcoutFake);
		// 			this.daoStorageRegister.save(sr);
		// 		}
		// 	}
		// 	j++;
		// }

		Dispatch dispatchSave = this.dao.save(dispatch);
		//GUARDAR REGISTRO TRACKING
		//INICIAL
		// TrackingHistory register = new TrackingHistory();
		// register.setRegisterDate(this.serviceAStart.convertToLocalDateViaInstant(
		// 		this.serviceAStart.convertToDateViaSqlDate(dispatch.getRegisterDate().toLocalDate().minusDays(1))));
		// register.setDescription("EL PAQUETE FUE RECIBIDO");
		// register.setDispatch(dispatchSave);
		// this.daoTracking.save(register);
		// //PRIMER AEROPUERTO
		// TrackingHistory register2 = new TrackingHistory();
		// register2.setRegisterDate(this.serviceAStart.convertToLocalDateViaInstant(
		// 		this.serviceAStart.convertToDateViaSqlDate(dispatch.getRegisterDate().toLocalDate().minusDays(1))));
		// register2.setDescription("EL PAQUETE ESTA EN EL ALMACEN DE " + dispatch.getOriginAirport().getCity().getName().toUpperCase());
		// register2.setDispatch(dispatchSave);
		//this.daoTracking.save(register2);
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
		String date;
		String time;
		Integer cantPackages;

		public PackageTemp(final String originAirport, final String destinationAirport, final String date, final String time, final Integer cantPackages) {
			this.originAirport = originAirport;
			this.destinationAirport = destinationAirport;
			this.date = date;
			this.time = time;
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

		public String getDate() {
			return this.date;
		}

		public void setDate(final String date) {
			this.date = date;
		}

		public LocalTime getTime() {
			return LocalTime.parse(this.time);
		}

		public void setTime(final String time) {
			this.time = time;
		}

		public Integer getCantPackages() {
			return this.cantPackages;
		}

		public void setCantPackages(final Integer cantPackages) {
			this.cantPackages = cantPackages;
		}
	}
	public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
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

	class PackageComparator implements Comparator<PackageTemp>{
		public int compare(PackageTemp a, PackageTemp b) {
			if ( a.getTime().isAfter(b.getTime()) ) return 1;
			else if (a.getTime().isBefore(b.getTime())) return -1;
			return 0;
		}
	}




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

	@Override
	public String masiveLoad(MultipartHttpServletRequest request) {
		//"2022-08-01
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		try {
			Iterator<String> it = request.getFileNames();
			MultipartFile mf = request.getFile(it.next());
			String datereq = request.getParameter("date").substring(1,11).replace("-", "");
			System.out.println("1 \n");
			//Convierte multifile a zip
			File tempFile = File.createTempFile("upload", null);
			mf.transferTo(tempFile);
			ZipFile zip = new ZipFile(tempFile);
			//loop por cada archivo del zip
			Enumeration<? extends ZipEntry> entries = zip.entries();
			ZipEntry entry = entries.nextElement();
			Integer i=0;
			PriorityQueue<PackageTemp> pq = new PriorityQueue<PackageTemp>(new PackageComparator());
			while (entries.hasMoreElements()) {
				//ZipEntry entry = entries.nextElement();
				entry = entries.nextElement();
				InputStream stream = zip.getInputStream(entry);
				InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
				Scanner inputStream = new Scanner(reader);
				//lectura de cada linea del archivo
				//Integer stateColapso = 0;
				SimpleDateFormat formatterDate = new SimpleDateFormat("yyyyMMdd");
				Date dateDate;
				dateDate = formatterDate.parse(datereq);
				LocalDate date1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(dateDate));
				String originAirport;
				String date;
				String time;
				String destinationAirport;
				Integer cantPackages;
				while (inputStream.hasNext()) {
					String data = inputStream.nextLine();
					List<String> line = Arrays.asList(data.split("-"));
					//datos de ingreso para el algoritmo
					originAirport = line.get(0).substring(0, 4);
					date = line.get(1);
					time = line.get(2);
					destinationAirport = line.get(3).substring(0, 4);
					cantPackages = Integer.parseInt(line.get(3).substring(5));
					// PROCESAR ALGORITMO
					PackageTemp p = new PackageTemp(originAirport, destinationAirport, date, time, cantPackages);
					if  (convertStringToLocalDate(date).isAfter(date1)) break;
					else if (convertStringToLocalDate(date).equals(date1)) 
						pq.add(p);
						//insetar(pq,p);
				}
				reader.close();
				inputStream.close();
			}
			while (pq.size()!=0) {
				PackageTemp pack=pq.poll();
				//System.out.println(pack.getOriginAirport() + "  " + pack.getDate() + " " + pack.getTime() + " " + pack.getDestinationAirport() + " " + pack.getCantPackages());
				Integer resultPlan = serviceAStart.insertHistoricPackage(pack.getOriginAirport(), pack.getDestinationAirport(), pack.getDate(), pack.getTime(), pack.getCantPackages());
				if (resultPlan!=1) {
					System.out.println(pack.getOriginAirport() + "  " + pack.getDate() + " " + pack.getTime() + " " + pack.getDestinationAirport() + " " + pack.getCantPackages());
					break;
				}
			}
		
			zip.close();
			tempFile.delete();
			return "OK";
		} catch (IOException io) {
			return "ERROR";
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
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
