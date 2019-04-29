package es.uam.eps.bmi.sna.network;

import java.util.Arrays;
import java.util.Random;

import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetworkImpl;

public class BarabasiAlbertGenerator extends NetworkGenerator<Integer> {
	int m0;
	int m;
	int n;
	
	public BarabasiAlbertGenerator(int m0, int m, int n) {
		this.m0 = m0;
		this.m = m;
		this.n = n;
	}
	
	@Override
	public UndirectedSocialNetwork<Integer> generate() {
		
		UndirectedSocialNetwork<Integer> network = new UndirectedSocialNetworkImpl<Integer>();
		
		// Lista con numero de conexiones por nodo
		int[] indices = new int[this.n];
		Arrays.fill(indices, 0);
		
		// Primero generamos conjunto inicial de m0 nodos enlazados aleatoriamente
		Random r = new Random();
		int num_edges = 0;
		
		for(int u=0; u<m0; u++) {
			for(int i=0; i<m; i++) {
				int v = r.nextInt(m0);
				if (v != u) {
					indices[u]++;
					indices[v]++;
					num_edges++;
					network.addContact(u, v);
				}
			}
		}
		
		
		for (int u=m0; u <n; u++) {
			for (int i=0; i<m; i++) {
				int v = randomNode(indices, u, num_edges);
				if (v != u) {
					indices[u]++;
					indices[v]++;
					num_edges++;
					network.addContact(u, v);
				}
			}
		}
		
		
		return network;
	}
	
	/**
	 * Generador aleatorio de numeros siguiendo distribucion empirica
	 * dada por el vector de indices
	 * @param indices
	 * @param max
	 * @param num_edges
	 * @return
	 */
	private int randomNode(int[] indices, int max, int num_edges) {
		

		double p = 2 * num_edges * Math.random();
		double cumulativeProbability = 0.0;
		
		for (int i=0; i<max; i++) {
		    cumulativeProbability += indices[i];
		    if (p <= cumulativeProbability) {
		        return i;
		    }
		}
		
		return max;
	}
	

}
