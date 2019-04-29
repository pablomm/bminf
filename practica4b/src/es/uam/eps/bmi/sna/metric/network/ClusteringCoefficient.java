package es.uam.eps.bmi.sna.metric.network;

import es.uam.eps.bmi.sna.metric.GlobalMetric;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class ClusteringCoefficient<U extends Comparable<U>> implements GlobalMetric<U> {

	
	

	@Override
	public double compute(UndirectedSocialNetwork<U> network) {
		
		
		double triangulos = 0;

		// Numero caminos cerrados de long 2
		for (U u : network.getUsers()) {
			for (U v : network.getContacts(u)) {
				for (U w : network.getContacts(v)) {
					if (u.compareTo(w) < 0 && network.connected(u, w)) {
						triangulos += 1;
					}
				}
			}
		}
		
		double total_triangulos = 0;
		// Numero de caminos de long 2
		for (U u : network.getUsers()) {
			int conexiones = network.getContacts(u).size();
			total_triangulos += ((conexiones * (conexiones - 1)) / 2);
		}

		
		// TODO Auto-generated method stub
		return triangulos / total_triangulos;
	}
	
	@Override
	public String toString() {
		return "ClusteringCoefficient";
	}

	

}
