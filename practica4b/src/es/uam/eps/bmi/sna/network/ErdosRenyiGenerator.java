package es.uam.eps.bmi.sna.network;

import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetworkImpl;

/**
 * @author Pablo Marcos
 * Generador de redes de Erdos-Renyi, se generarn n enlaces.
 * Con probabilidad p de que un enlace aleatorio u-v este conectado
 */
public class ErdosRenyiGenerator extends NetworkGenerator<Integer> {
	int n;
	double p;
	
	public ErdosRenyiGenerator(int n, double p) {
		this.n = n;
		this.p = p;
	}
	
	/**
	 * @param n Numero de nodos de la red
	 * @param p Probabilidad de que 
	 * @return
	 */
	public UndirectedSocialNetwork<Integer> generate() {
		
		UndirectedSocialNetworkImpl<Integer> network = new UndirectedSocialNetworkImpl<Integer>();
		
		for(int u=0; u<n; u++) {
			for(int v=0; v<u; v++) {
				if (Math.random() <= p) {
					network.addContact(u, v);
				}
				
			}
		}
		return network;
	}

}
