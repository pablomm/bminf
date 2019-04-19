package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;


/**
 * @author Pablo Marcos, Miguel Laseca
 *
 */
public class ItemNNRecommender extends AbstractRecommender {
	
	/**
	 * Mapa para incluir la vecindad, calculada off-line  (ItemId, Ranking Similitudes)
	 */
	HashMap<Integer, HashMap<Integer, Double>> neighborhood = new HashMap<Integer, HashMap<Integer, Double>> ();
	
	/**
	 * Nombre de la similitud usada 
	 */
	String sim;

	/**
	 * Recomendador nn basado en item sin normalizacion
	 * @param rat Estructura con los ratings
	 * @param s Similitud
	 */
	public ItemNNRecommender (Ratings rat, Similarity s) {
		
		super(rat);
		
		// Nombre del tipo de similitud
		sim = s.toString();
		
		// Calculamos la similitud de todos los items con todos.
		// Si suponemos la similitud simetrica podriamos ahorrarnos en este
		// caso la mitad del computo
		for (int item1 : rat.getItems()) {
			
			// Similitudes de item1
			HashMap<Integer, Double> ranking = new HashMap<Integer, Double>();
			
			for (int item2 : rat.getItems()) {
				if (item1!=item2) ranking.put(item2, s.sim(item1, item2));
			}
			
			// Incluimos el ranking de vecindades del user1
			neighborhood.put(item1, ranking);
		}
	}

	@Override
	public double score(int user, int item) {
		
		double score=0;
		HashMap<Integer, Double> similitudes = this.neighborhood.get(item);
		
		// Iteramos sobre los items que ha puntuado el usuario
		for (int item1 : this.ratings.getItems(user)) {
			
			// Obtenemos la puntuacion que le ha dado el usuario
			Double rating = ratings.getRating(user, item1);;
			
			// Rating * similitud 
			score += rating * similitudes.get(item1);
			
		}
		
		return score;
	}

	@Override
	public String toString() {
		return "item-based NN ("+ sim +")";
	}
}
