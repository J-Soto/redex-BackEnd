package pucp.dp1.redex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pucp.dp1.redex.model.utils.SummaryCase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public  class RedexApplication {
	public static String masiveLoad() {
		try {
			//Iterator<String> it = request.getFileNames();
			//MultipartFile mf = request.getFile(it.next());
			//System.out.println("1 \n");
			//Convierte multifile a zip
			//File tempFile = File.createTempFile("upload", null);
			//mf.transferTo(tempFile);
			ZipFile zip = new ZipFile("C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\historicos.zip");
			//loop por cada archivo del zip
			Enumeration<? extends ZipEntry> entries = zip.entries();
			ZipEntry entry = entries.nextElement();
			while (entries.hasMoreElements()) {
				entry = entries.nextElement();
				System.out.println(entry.getName());
				InputStream stream = zip.getInputStream(entry);
				InputStreamReader reader = new InputStreamReader(stream);
				Scanner inputStream = new Scanner(reader);
				//lectura de cada linea del archivo
				while (inputStream.hasNext()) {
					String data = inputStream.nextLine();
					List<String> line = Arrays.asList(data.split("-"));
					//datos de ingreso para el algoritmo
					String originAirport = line.get(0).substring(0, 4);
					String date = line.get(1);
					String time = line.get(2);
					String destinationAirport = line.get(3).substring(0, 4);
					System.out.println("Fecha: "+date+"\t" + "Hora: " + time +"\t"+ "Origen: " + originAirport +"\t"+ "Destino: " + destinationAirport+"\n");
					// PROCESAR ALGORITMO
//					System.out.println(originAirport + "  " + date + " " + time + " " + destinationAirport);
//					Integer resultPlan = serviceAStart.insertHistoricPackage(originAirport, destinationAirport, date, time);
//					if(resultPlan==1) {
//						Optional<SummaryCase> sc = this.daoSummary.findById(1);
//						if(sc.isPresent()) {
//							sc.get().setOk(sc.get().getOk()+1);
//							this.daoSummary.save(sc.get());
//						} else if(resultPlan==0) {
//							SummaryCase nsc = new SummaryCase();
//							nsc.setFails(0);
//							nsc.setOk(1);
//							nsc.setLate(0);
//							this.daoSummary.save(nsc);
//						}
//					} else if (resultPlan==0){
//						Optional<SummaryCase> sc = this.daoSummary.findById(1);
//						if(sc.isPresent()) {
//							sc.get().setFails(sc.get().getFails()+1);
//							this.daoSummary.save(sc.get());
//						} else {
//							SummaryCase nsc = new SummaryCase();
//							nsc.setFails(1);
//							nsc.setOk(0);
//							nsc.setLate(0);
//							this.daoSummary.save(nsc);
//						}
//					} else if (resultPlan==2){
//						Optional<SummaryCase> sc = this.daoSummary.findById(1);
//						if(sc.isPresent()) {
//							sc.get().setLate(sc.get().getLate()+1);
//							this.daoSummary.save(sc.get());
//						} else {
//							SummaryCase nsc = new SummaryCase();
//							nsc.setFails(0);
//							nsc.setOk(0);
//							nsc.setLate(0);
//							this.daoSummary.save(nsc);
//						}
//					}
				}
				//System.out.println("Exitosos: "+exitosos.toString()+" Fallos: "+fallidos.toString());
				reader.close();
				inputStream.close();
			}
			zip.close();
			return "OK";
		} catch (IOException io) {
			return "ERROR";
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(RedexApplication.class, args);
		//System.out.println(masiveLoad());

	}

}