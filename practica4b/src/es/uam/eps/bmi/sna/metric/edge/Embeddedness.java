package es.uam.eps.bmi.sna.metric.edge;

import java.util.Set;

import es.uam.eps.bmi.sna.metric.LocalMetric;
import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.ranking.RankingImpl;
import es.uam.eps.bmi.sna.structure.Edge;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

/**
 * @author Pablo Marcos
 *
 * @param <U> Tipo de los usuarios de la red
 */
public class Embeddedness<U extends Comparable<U>> implements LocalMetric<Edge<U>, U> {
	
	int top;
	
	public Embeddedness(int top) {
		this.top = top;
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.sna.metric.LocalMetric#compute(es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork)
	 */
	@Override
	public Ranking<Edge<U>> compute(UndirectedSocialNetwork<U> network) {
		
		Ranking<Edge<U>> ranking = new RankingImpl<Edge<U>>(this.top);
		
		for(U u : network.getUsers()) {
			for (U v : network.getUsers()) {
				
				if (u.compareTo(v) < 0) { // Utilizamos que el grafo es no dirigido
					Edge<U> edge = new Edge<U>(u, v);
					ranking.add(edge, this.compute(network, edge));
					
				}
			}
		}
		
		return ranking;
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.sna.metric.LocalMetric#compute(es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork, java.lang.Comparable)
	 */
	@Override
	public double compute(UndirectedSocialNetwork<U> network, Edge<U> element) {
		
		U u = element.getFirst();
		U v = element.getSecond();
		Set<U> contactosU = network.getContacts(u);
		Set<U> contactosV = network.getContacts(v);
		
		boolean amigos = contactosV.contains(u);
		
		// Caso unico amigo el otro eje
		if (amigos && (contactosV.size() == 1 || contactosU.size() == 1)) {
			return 0;
		} 
		
		double interseccion = 0;
		
		for (U user : contactosU) {
			if (contactosV.contains(user)) {
				interseccion += 1;
			}
		}
		
		// Caso hay que quitar la amistad de los conjuntos
		if (amigos) {
			return interseccion / (contactosU.size() + contactosV.size() - interseccion - 2);
		}
		
		// Caso sin enlace entre u y v
		return interseccion / (contactosU.size() + contactosV.size() - interseccion);
		
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Embeddedness";
	}
}
