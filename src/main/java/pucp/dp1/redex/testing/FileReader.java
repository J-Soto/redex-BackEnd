package pucp.dp1.redex.testing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Service;

import pucp.dp1.redex.router.algorithms.Node;

@Service
public class FileReader {
	
	public List<List<String>> readFiles(String ruta) {
		List<List<String>> records = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(ruta));) {
		    while (scanner.hasNextLine()) {
		        records.add(getRecordFromLine(scanner.nextLine()));
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return records; 
	}
	public List<String> getRecordFromLine(String line) {
	    List<String> values = new ArrayList<String>();
	    try (Scanner rowScanner = new Scanner(line)) {
	        rowScanner.useDelimiter(",");
	        while (rowScanner.hasNext()) {
	            values.add(rowScanner.next());
	        }
	    }
	    return values;
	}
	public void writeToFile(String out_file, Node node, double time, int origen, int destino) {
		try(FileWriter fw = new FileWriter(out_file,true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw)){
			 /*tiempo de ruta*/
			 NumberFormat formatter = new DecimalFormat("#0.00000");
			 Double distance = node.getDistance()/60;
			 boolean routeNotExist = node.getShortestPath().isEmpty();
			 if(routeNotExist) {
				 distance=0.0;
			 }
			 String data = origen + " -> " + destino + 
					 " " + formatter.format(distance) + " " + !routeNotExist + " " + time; 
	         out.println(data);
	     } catch(IOException e){
	         e.printStackTrace();
	      }
	}

}
