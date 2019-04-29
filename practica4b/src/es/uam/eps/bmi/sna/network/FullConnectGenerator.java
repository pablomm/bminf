package es.uam.eps.bmi.sna.network;

import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetworkImpl;

/**
 * Generador de redes completamente conectadas
 * @author Pablo Marcos, Miguel Laseca
 *
 */
public class FullConnectGenerator extends NetworkGenerator<Integer> {
	
	int n;
	public FullConnectGenerator(int n) {
		this.n = n;
	}

	@Override
	public UndirectedSocialNetwork<Integer> generate() {
		
		UndirectedSocialNetwork<Integer> network = new UndirectedSocialNetworkImpl<Integer>();
		
		for (int u=0; u<n; u++) {
			for(int v=0; v<u; v++) {
				network.addContact(u, v);
			}
		}
		
		return network;
	}

}
