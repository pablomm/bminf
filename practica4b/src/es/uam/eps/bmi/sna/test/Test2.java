package es.uam.eps.bmi.sna.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import es.uam.eps.bmi.sna.network.BarabasiAlbertGenerator;
import es.uam.eps.bmi.sna.network.ErdosRenyiGenerator;
import es.uam.eps.bmi.sna.network.NetworkGenerator;
import es.uam.eps.bmi.sna.structure.IntParser;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetworkImpl;

public class Test2 {
	public static void main (String a[]) throws IOException {
		
		//System.out.println("Generando red erdos");
		//NetworkGenerator<Integer> erdosRenyi = new ErdosRenyiGenerator(1000, 0.01);
		//erdosRenyi.dump("graph/erdos.csv", ",");
		//System.out.println("Generando red barabasi");
		//NetworkGenerator<Integer> barabasi = new BarabasiAlbertGenerator(40, 20, 1000);
		//barabasi.dump("graph/barabasi.csv", ",");
		
		
		UndirectedSocialNetwork<Integer> network = new UndirectedSocialNetworkImpl<Integer>("graph/barabasi.csv", ",", new IntParser());
		
		File f = new File("barabasi-stats.txt");
		PrintWriter writer = new PrintWriter(f);
		// Extracion de datos para comprobar paradoja de la amistad
		for(Integer u : network.getUsers()) {
			double mean = 0;
			Set<Integer> contactosU = network.getContacts(u);
			for (int v : contactosU) {
				mean += network.getContacts(v).size();
			}
			mean /= contactosU.size();
			writer.println(contactosU.size() + " " +  mean);
			
			
		}
		writer.close();
	}
	
	
	
}
