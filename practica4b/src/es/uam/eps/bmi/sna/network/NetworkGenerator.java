package es.uam.eps.bmi.sna.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

/**
 * Clase abstracta para generar redes simuladas
 * @author Pablo Marcos
 *
 * @param <U> Tipo de usuario
 */
public abstract class NetworkGenerator<U extends Comparable<U>> {

	/**
	 * @return Red generada
	 */
	public abstract UndirectedSocialNetwork<U> generate();
	
	/**
	 * Vuelca en un fichero el grafo no dirigido
	 * @param filename Nombre del fichero
	 * @throws FileNotFoundException
	 */
	public void dump(String filename, String sep) throws FileNotFoundException {
		dump(this.generate(), filename, sep);
	}
	
	/**
	 * Vuelca en un fichero el grafo no dirigido
	 * @param network Red
	 * @param filename Nombre del fichero
	 * @param sep Separador
	 * @throws FileNotFoundException
	 */
	public void dump (UndirectedSocialNetwork<U> network, String filename, String sep) throws FileNotFoundException {
		
		File f = new File(filename);
		PrintWriter writer = new PrintWriter(f);
		
		for(U u : network.getUsers()) {
			for (U v : network.getContacts(u)) {
				if (u.compareTo(v) < 0) {
					writer.println(u.toString() + sep + v.toString());
				}
			}
		}
		
		writer.close();
	}
}
