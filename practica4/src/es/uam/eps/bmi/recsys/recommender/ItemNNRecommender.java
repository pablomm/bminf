package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;
import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;


/**
 * @author Pablo Marcos, Miguel Laseca
 *
 */
public class ItemNNRecommender extends AbstractRecommender {
	
	/**
	 * Matriz con similitudes
	 */
	
	double[][] similitudes;
	
	/**
	 * Mapa entre posiciones en la matriz y itemID
	 */
	HashMap<Integer, Integer> posiciones = new HashMap<Integer, Integer>();
	/**
	 * Nombre de la similitud usada 
	 */
	String sim;
	
	int n_items = 0;

	/**
	 * Recomendador nn basado en item sin normalizacion
	 * @param rat Estructura con los ratings
	 * @param s Similitud
	 */
	public ItemNNRecommender (Ratings rat, Similarity s) {
		
		super(rat);
		
		// Nombre del tipo de similitud
		sim = s.toString();
		
		Set<Integer> items = rat.getItems();
		int tam = items.size();
		similitudes = new double[tam][tam];
		
		// Calculamos la similitud de todos los items con todos.
		// Si suponemos la similitud simetrica podriamos ahorrarnos en este
		// caso la mitad del computo
		for (int item1 : rat.getItems()) {
			int pos1 = this.getID(item1);
			
			for (int item2 : items) {
				int pos2 = this.getID(item2);
				
				if (item1!=item2) similitudes[pos1][pos2] = s.sim(item1, item2);
			}
			

		}
	}

	
	private int getID(int item) {
		
		Integer pos = posiciones.get(item);
		
		if (pos == null) {
			posiciones.put(item, n_items);
			pos = n_items;
			n_items++;
		}
		
		return pos;
	}
	
	@Override
	public double score(int user, int item) {
		
		double score=0;
		Integer pos1 = posiciones.get(item);
		
		if (pos1 == null) {
			System.out.println("HEHEHEHEHEHEH");
			return Double.NEGATIVE_INFINITY;
		}
		
		//HashMap<Integer, Double> similitudes = this.neighborhood.get(item);
		
		// Iteramos sobre los items que ha puntuado el usuario
		for (int item1 : this.ratings.getItems(user)) {
			
			// Obtenemos la puntuacion que le ha dado el usuario
			Double rating = ratings.getRating(user, item1);
			
			Integer pos2 = posiciones.get(item1);
			
			// Rating * similitud 
			score += rating * similitudes[pos1][pos2];
			
		}
		
		return score;
	}

	@Override
	public String toString() {
		return "item-based NN ("+ sim +")";
	}
}
