package es.uam.eps.bmi.sna.metric.user;

import java.util.HashMap;
import java.util.Set;

import es.uam.eps.bmi.sna.metric.LocalMetric;
import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.ranking.RankingImpl;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

public class PageRank<U extends Comparable<U>> implements LocalMetric<U, U> {
	int top;
	int iterations;
	double r;
	public PageRank(double r, int iterations, int top) {
		
		this.top = top;
		this.iterations = iterations;
		this.r = r;
	}
	
	public PageRank(double r, int iterations) {
		this(r, iterations, -1);
	}

	@Override
	public Ranking<U> compute(UndirectedSocialNetwork<U> network) {
		Ranking<U> ranking;
		if(this.top == -1) {
			ranking = new RankingImpl<U>();
		} else {
			ranking = new RankingImpl<U>(this.top);
		}
		HashMap<U, Double> pr = computePageRank(network, this.iterations, this.r);
		for (U user : pr.keySet()) {
			ranking.add(user, pr.get(user));
		}
		
		
		return ranking;
	}

	@Override
	public double compute(UndirectedSocialNetwork<U> network, U element) {

		return computePageRank(network, this.iterations, this.r).get(element);
	}

	
	protected HashMap<U, Double> computePageRank(UndirectedSocialNetwork<U> network, int iterations, double r) {
			Set<U> users = network.getUsers();
			int nUsers = users.size();
			
			// Juega el valor de P en el algoritmo
			HashMap<U, Double> values  = new HashMap<U, Double>();
			double init = 1./nUsers;
			for (U user : network.getUsers()) {
				values.put(user, init);
			
			
			// P'
			HashMap<U, Double> aux_values  = new HashMap<U, Double>();
			
			// Iteracion principal
			double rn = r / nUsers;
			for(int iter=0; iter<iterations; iter++) {
				
				//P'[i]=r/N
				for (U u : network.getUsers()) {
					aux_values.put(u, rn);
				}
				
				
				// Iteramos sobre todos los nodos
				for(U u : network.getUsers()) {
					
					double weight=0;
					
					// Peso que le corresponde a cada enlace saliente
					if(network.getContacts(u).size() != 0) {
						weight = values.get(u) * (1 - r) / network.getContacts(u).size();
					}
					
					// Repartimos peso entre los enlaces saliente
					for(U v : network.getContacts(u)) {
						aux_values.put(v, weight + aux_values.get(v));
					}	
				}
				
				// Sumamos constante para arreglar sumideros
				double sink =0;
				
				for(double p : aux_values.values()) {
					sink += p;
				}
				
				sink = (1 - sink) / nUsers;
				
				// Ajustamos peso de sumidero
				for(U v : network.getUsers()) {
					aux_values.put(v, sink + aux_values.get(v));
				}	
			}
		}
			return values;

	}
	
	public String toString() {
		return "pageRank";
	}
	
}
