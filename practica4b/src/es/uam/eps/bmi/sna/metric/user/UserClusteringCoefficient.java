package es.uam.eps.bmi.sna.metric.user;

import java.util.Set;

import es.uam.eps.bmi.sna.metric.LocalMetric;
import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.ranking.RankingImpl;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

/**
 * @author Pablo Marcos
 *
 * @param <U> Tipo de usuario
 */
public class UserClusteringCoefficient<U extends Comparable<U>> implements LocalMetric<U, U> {

	int top;
	
	/**
	 * @param top Numero de resultados a devolver
	 */
	public UserClusteringCoefficient(int top) {
		
		this.top = top;
	}
	
	public UserClusteringCoefficient() {
		this(-1);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.sna.metric.LocalMetric#compute(es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork)
	 */
	@Override
	public Ranking<U> compute(UndirectedSocialNetwork<U> network) {
		
		
		Ranking<U> ranking;
		if(this.top == -1) {
			ranking = new RankingImpl<U>();
		} else {
			ranking = new RankingImpl<U>(this.top);
		}
		
		for (U user : network.getUsers()) {
			ranking.add(user, this.compute(network, user));
		}
		
		
		return ranking;
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.sna.metric.LocalMetric#compute(es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork, java.lang.Comparable)
	 */
	@Override
	public double compute(UndirectedSocialNetwork<U> network, U element) {
		
		Set<U> contactos = network.getContacts(element);
		int degree = contactos.size();
		
		if (degree < 2) {
			return 0.;
		}
		
		double n_conexiones =0;
		for (U contact : contactos) {
			
			for (U contact2 : network.getContacts(contact)) {
				
				if (contact2 != element && contactos.contains(contact2)) {
					n_conexiones+=1;
				}
			}
			
		}
		
		return n_conexiones / (degree * (degree - 1));	
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserClusteringCoefficient";
	}
	
	

}
