package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;
import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

/**
 * @author Miguel Laseca, Pablo Marcos
 *
 */
public class UserCenteredKNNRecommender extends UserKNNRecommender {
	
	HashMap<Integer, Double> means = new HashMap<Integer, Double>();
	
	public UserCenteredKNNRecommender (Ratings rat, Similarity s, int k) {
		
		// Calcula vecindades 
		super(rat, s, k);
		
		// Calculamos todas las medias
		for(int user : rat.getUsers()) {
			
			double sum = 0;
			
			Set<Integer> items = rat.getItems(user);
			// Iteramos sobre items que ha puntuado
			for(int item : items) {
				
				sum += rat.getRating(user, item);
				
			}
			if (items.size() > 0) {
				sum /= items.size();
			}
			means.put(user, sum);
		}
	}

	@Override
	public double score(int user, int item) {
		
		double score=0;
		boolean flag = true;
		
		// Iteramos sobre la vecindad
		for (RankingElement rank : neighborhood.get(user)) {
			
			int vecino = rank.getID();
			
			// Obtenemos el rating del vecino
			Double rating = ratings.getRating(vecino, item);
			
			if (rating != null) {
				flag = false; // Si al menos hay una recomendacion
				
				// score += r(u,i)*sim(u,v)
				score += rank.getScore() * (rating - means.get(vecino));
			}
		}
		// No recomendamos cosas sin datos
		if (flag) {
			return Double.NEGATIVE_INFINITY;
		}
		
		return means.get(user) + score;
	}

	@Override
	public String toString() {
		return "centered " + super.toString();
	}
}
