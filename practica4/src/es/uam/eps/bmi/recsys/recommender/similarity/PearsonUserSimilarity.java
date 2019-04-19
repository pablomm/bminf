package es.uam.eps.bmi.recsys.recommender.similarity;

import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;

/**
 * @author Miguel Laseca y Pablo Marcos
 *
 */
public class PearsonUserSimilarity implements Similarity {


	Ratings ratings;
	
	/**
	 * Similitud de pearson entre usuarios
	 * @param r Estructura con ratings
	 */
	public PearsonUserSimilarity(Ratings r) {
		ratings=r;
	}
	
	@Override
	public double sim(int x, int y) {
		
		// Calculamos media de x
		double x_mean = 0;
		Set<Integer> ratings_x = ratings.getItems(x);
		
		for (int item : ratings_x) {
			Double x_rating = ratings.getRating(x, item);
			x_mean += x_rating;
		}
		
		x_mean /= ratings_x.size();
		
		// Calculamos media de y
		double y_mean = 0;
		Set<Integer> ratings_y = ratings.getItems(y);
		
		for (int item : ratings_y) {
			Double y_rating = ratings.getRating(y, item);
			y_mean += y_rating;
		}
		
		y_mean /= ratings_y.size();
	
		
		double score=0, x_mod=0;
		
		// Calculamos score
		for (int item : ratings_x) {
			
			Double y_rating = ratings.getRating(y, item);
			Double x_rating = ratings.getRating(x, item);
			
			if (y_rating != null) {
				score+= (x_rating - x_mean) * (y_rating - y_mean);
			
			}
			// Vamos calculando el modulo de x
			x_mod+= Math.pow(x_rating - x_mean,2);
		}
		
		double y_mod = 0;
		
		// Calculamos el modulo de y
		for (int item : ratings.getItems(y)) {
			y_mod+= Math.pow(ratings.getRating(y, item) - y_mean, 2);
		}
		
		
		return score/(Math.sqrt(x_mod * y_mod));
	}
	
	@Override
	public String toString() {
		return "pearson correlation similarity";
	}

}

