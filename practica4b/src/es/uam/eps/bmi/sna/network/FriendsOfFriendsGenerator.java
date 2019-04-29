package es.uam.eps.bmi.sna.network;

import java.util.Arrays;
import java.util.Random;

import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetworkImpl;

/**
 * Modelo de grafo en el que a partir de un conjunto inicial de conexiones
 * Se van creando conexiones con mas probabilidad con los amigos en comun
 * @author Pablo Marcos
 *
 */
public class FriendsOfFriendsGenerator extends NetworkGenerator<Integer>{
	
	
	int m0;
	int m;
	int n;
	
	/**
	 * @param m0 Numero de conexiones iniciales aleatorias 
	 * @param m Numero de conexiones totales
	 * @param n Numero de nodos
	 */
	public FriendsOfFriendsGenerator(int m0, int m, int n) {
		this.m0 = m0;
		this.m = m;
		this.n = n;
	}
	
	@Override
	public UndirectedSocialNetwork<Integer> generate() {
		
		UndirectedSocialNetwork<Integer> network = new UndirectedSocialNetworkImpl<Integer>();
		
		// Primero generamos conjunto inicial de n nodos enlazados aleatoriamente
		Random r = new Random();
		for(int u=0; u<n; u++) {
			for(int i=0; i<m0; i++) {
				int v = r.nextInt(n);
				if (v != u) {
					network.addContact(u, v);
				}
			}
		}
		
		for(int i=m0; i<m; i++) {
			for (int u=m0; u <n; u++) {
				int[] p = prob_amigos_comun(network, u, n);
				int v = randomNode(p, u, n);
				network.addContact(u, v);
			}
		}
		
		return network;
	}
	
	private int[] prob_amigos_comun(UndirectedSocialNetwork<Integer> network, int u, int n) {
		
		int[] amigos_comun = new int[n + 1];
		Arrays.fill(amigos_comun, 0);
		
		for (Integer v : network.getContacts(u)) {
			for(Integer w: network.getContacts(v)) {
				amigos_comun[w]++;
			}
		}
		
		amigos_comun[u] = 0;
		int c = 0;
		int cum = 0;
		for(int i=0; i < n; i++) {
			if (network.connected(u, i) ) {
				amigos_comun[i] = 0;
			} else {
				c++;
				cum += amigos_comun[i];
			}
		}
		
		if (cum == 0) {
			cum = c;
			for(int i=0; i < n; i++) {
				if (!network.connected(u, i) ) {
					amigos_comun[i] = 1;
				}
			}
		}
		
		// Ponemos en el ultimo la suma total
		amigos_comun[n] = cum;
		
		return amigos_comun;
	}
	
	/**
	 * Generador aleatorio de numeros siguiendo distribucion empirica
	 * dada por el vector de indices
	 * @param indices
	 * @param max
	 * @param num_edges
	 * @return
	 */
	private int randomNode(int[] indices, int max, int sum) {
		

		double p = sum * Math.random();
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
