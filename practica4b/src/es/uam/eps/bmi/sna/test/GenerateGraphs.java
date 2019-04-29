package es.uam.eps.bmi.sna.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import es.uam.eps.bmi.sna.network.FriendsOfFriendsGenerator;
import es.uam.eps.bmi.sna.network.FullConnectGenerator;
import es.uam.eps.bmi.sna.network.NetworkGenerator;
import es.uam.eps.bmi.sna.structure.IntParser;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetworkImpl;

public class GenerateGraphs {
	public static void main (String a[]) throws IOException {
		
		//System.out.println("Generando red erdos");
		//NetworkGenerator<Integer> erdosRenyi = new ErdosRenyiGenerator(1000, 0.01);
		//erdosRenyi.dump("graph/erdos.csv", ",");
		//paradoja_amistad("graph/erdos.csv", "erdos-stats.txt");
		
		//System.out.println("Generando red barabasi");
		//NetworkGenerator<Integer> barabasi = new BarabasiAlbertGenerator(40, 20, 1000);
		//barabasi.dump("graph/barabasi.csv", ",");
		//paradoja_amistad("graph/barabasi.csv", "barabasi-stats.txt");
		
		//System.out.println("Generando grafo amigos de amigos");
		//NetworkGenerator<Integer> friends = new FriendsOfFriendsGenerator(10, 50, 1000);
		//friends.dump("graph/friends.csv", ",");
		//paradoja_amistad("graph/friends.csv", "friends-stats.txt");
		
		System.out.println("Generando grafo totalmente conectado");
		NetworkGenerator<Integer> friends = new FullConnectGenerator(20);
		friends.dump("graph/full.csv", ",");
		paradoja_amistad("graph/full.csv", "full-stats.txt");
		
		
	}
	
	public static void paradoja_amistad(String filename, String dumpFilename) throws FileNotFoundException {
		UndirectedSocialNetwork<Integer> network = new UndirectedSocialNetworkImpl<Integer>(filename, ",", new IntParser());
		
		File f = new File(dumpFilename);
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
