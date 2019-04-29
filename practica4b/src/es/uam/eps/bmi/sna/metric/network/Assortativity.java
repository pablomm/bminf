package es.uam.eps.bmi.sna.metric.network;

import java.util.Set;

import es.uam.eps.bmi.sna.metric.GlobalMetric;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class Assortativity<U extends Comparable<U>> implements GlobalMetric<U> {

	@Override
	public double compute(UndirectedSocialNetwork<U> network) {
		
		double cuadrados = 0; // Acumulador de cuadrados de los grados
		double cubos = 0; // Acumulador de los cubos
		double termino_cruzado = 0.; // Acumulador de g(u)*g(v)
		
		// Iteramos sobre todos los usuarios
		for (U u : network.getUsers()) {
			
			Set<U> contactsU = network.getContacts(u);
			int gu = contactsU.size();
			int cuadrado = gu * gu;
			cuadrados += cuadrado;
			cubos += cuadrado*gu;
			
			// Iteramos sobre contactoes del usuario
			for(U v: contactsU) {
				if (u.compareTo(v) < 0) {
					termino_cruzado += gu*network.getContacts(v).size();
				}
			}
		}
		
		// sum(g(u)**2)**2
		cuadrados *= cuadrados;
		int m = network.nEdges();
		return (4 * m * termino_cruzado - cuadrados) / (2 * m * cubos - cuadrados);
	}
	
	@Override
	public String toString() {
		return "Assortativity";
	}

}
