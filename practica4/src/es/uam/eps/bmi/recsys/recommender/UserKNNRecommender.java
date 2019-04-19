package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingElement;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

/**
 * @author Miguel Laseca, Pablo Marcos
 *
 */
public class UserKNNRecommender extends AbstractRecommender {
	
	/**
	 * Mapa para incluir la vecindad, calculada off-line 
	 */
	HashMap<Integer,Ranking> neighborhood = new HashMap<Integer,Ranking> ();
	
	/**
	 * Nombre de la similitud usada 
	 */
	String sim;

	/**
	 * Recomendador knn basado en usuario sin normalizacion
	 * @param rat Estructura con los ratings
	 * @param s Similitud
	 * @param k Numero de vecinos
	 */
	public UserKNNRecommender (Ratings rat, Similarity s, int k) {
		
		super(rat);
		
		// Nombre del tipo de similitud
		sim=s.toString();
		
		// Calculamos la similitud de todos con todos.
		for (int user1 : rat.getUsers()) {
			// Rank the other users based on their similarity to the first one
			RankingImpl ranking = new RankingImpl(k);
			
			for (int user2 : rat.getUsers()) {
				if (user1!=user2) ranking.add(user2, s.sim(user1, user2));
			}
			
			// Incluimos el ranking de vecindades del user1
			neighborhood.put(user1,ranking);
		}
	}

	@Override
	public double score(int user, int item) {
		
		double score=0;
		
		// Iteramos sobre la vecindad
		for (RankingElement rank : neighborhood.get(user)) {
			
			// Obtenemos el rating del vecino
			Double rating = ratings.getRating(rank.getID(), item);;
			
			if (rating != null) {
				// score += r(u,i)*sim(u,v)
				score += rank.getScore() * rating;
			}
		}
		// No recomendamos cosas sin datos
		if (score == 0) {
			return Double.NEGATIVE_INFINITY;
		}
		
		return score;
	}

	@Override
	public String toString() {
		return "user-based kNN ("+ sim +")";
	}
}
