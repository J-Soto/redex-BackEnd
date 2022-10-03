package pucp.dp1.redex.controller.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pucp.dp1.redex.model.response.Estado;
import pucp.dp1.redex.model.response.ResponseObject;
import pucp.dp1.redex.router.algorithms.AStar;
import pucp.dp1.redex.router.algorithms.AStar2;
import pucp.dp1.redex.router.algorithms.Dijkstra;
import pucp.dp1.redex.router.algorithms.Node;
import pucp.dp1.redex.testing.FileReader;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class FileReaderController {

	@Autowired
	private FileReader service;

	@Autowired
	private Dijkstra servicioD;
	@Autowired

	private AStar2 servicioA;

	@GetMapping(path = "/testing", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseObject> consultarAeropuertos() {
		ResponseObject response = new ResponseObject();
		try {
			/* primer dataset */
			/*
			 * File f1 = new File(out1); File f2 = new File(out2); if(!f1.exists()) {
			 * f1.createNewFile(); } if(!f2.exists()) { f2.createNewFile(); }
			 */
			List<List<String>> lista = this.service.readFiles("C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\DataSets\\500-DataSet.csv");
			/* 5 vueltas */
			for (int i = 0; i < 5; i++) {
				String out1 = "C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\Outputs\\500-DataSet-output-dijkstra" + i + ".txt";
				String out2 = "C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\Outputs\\500-DataSet-output-astar" + i + ".txt";
				int j = 0;
				while (j < lista.size()) {
					List<String> test = lista.get(j);
					int first = Integer.valueOf(test.get(0));
					int second = Integer.valueOf(test.get(1));
					long start = System.currentTimeMillis();
					Node datosDijkstra = servicioD.getShortestPath(first, second);
					long end = System.currentTimeMillis();
					double time = end - start;
					this.service.writeToFile(out1, datosDijkstra, time, first, second);
					start = System.currentTimeMillis();
					Node datosAStar = servicioA.getShortestPath(first, second);
					end = System.currentTimeMillis();
					time = end - start;
					this.service.writeToFile(out2, datosAStar, time, first, second);
					j++;
				}
			}
			/* segundo dataset */
			lista = this.service.readFiles("C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\DataSets\\1000-DataSet.csv");
			/* 5 vueltas */
			for (int i = 0; i < 5; i++) {
				String out1 = "C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\Outputs\\1000-DataSet-output-dijkstra" + i + ".txt";
				String out2 = "C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\Outputs\\1000-DataSet-output-astar" + i + ".txt";
				int j = 0;
				while (j < lista.size()) {
					List<String> test = lista.get(j);
					int first = Integer.valueOf(test.get(0));
					int second = Integer.valueOf(test.get(1));
					long start = System.currentTimeMillis();
					Node datosDijkstra = servicioD.getShortestPath(first, second);
					long end = System.currentTimeMillis();
					double time = end - start;
					this.service.writeToFile(out1, datosDijkstra, time, first, second);
					start = System.currentTimeMillis();
					Node datosAStar = servicioA.getShortestPath(first, second);
					end = System.currentTimeMillis();
					time = end - start;
					this.service.writeToFile(out2, datosAStar, time, first, second);
					j++;
				}
			}
			/* tercer dataset */
			lista = this.service.readFiles("C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\DataSets\\3000-DataSet.csv");
			/* 5 vueltas */
			for (int i = 0; i < 5; i++) {
				String out1 = "C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\Outputs\\3000-DataSet-output-dijkstra" + i + ".txt";
				String out2 = "C:\\Users\\DUXAT\\Documents\\Uni\\2022-2\\DP1\\redex-BackEnd\\src\\main\\java\\pucp\\dp1\\redex\\Outputs\\3000-DataSet-output-astar" + i + ".txt";
				int j = 0;
				while (j < lista.size()) {
					List<String> test = lista.get(j);
					int first = Integer.valueOf(test.get(0));
					int second = Integer.valueOf(test.get(1));
					long start = System.currentTimeMillis();
					Node datosDijkstra = servicioD.getShortestPath(first, second);
					long end = System.currentTimeMillis();
					double time = end - start;
					this.service.writeToFile(out1, datosDijkstra, time, first, second);
					start = System.currentTimeMillis();
					Node datosAStar = servicioA.getShortestPath(first, second);
					end = System.currentTimeMillis();
					time = end - start;
					this.service.writeToFile(out2, datosAStar, time, first, second);
					j++;
				}
			}
			response.setResultado(lista);
			response.setEstado(Estado.OK);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setError(1, "Error", e.getMessage());
			response.setEstado(Estado.ERROR);
			return new ResponseEntity<ResponseObject>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}