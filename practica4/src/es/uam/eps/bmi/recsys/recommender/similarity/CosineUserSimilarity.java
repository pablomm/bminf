package es.uam.eps.bmi.recsys.recommender.similarity;

import es.uam.eps.bmi.recsys.data.Ratings;

/**
 * @author Miguel Laseca y Pablo Marcos
 *
 */
public class CosineUserSimilarity implements Similarity {


	Ratings ratings;
	
	/**
	 * Similitud coseno entre usuarios
	 * @param r Estructura con ratings
	 */
	public CosineUserSimilarity(Ratings r) {
		ratings=r;
	}
	
	@Override
	public double sim(int x, int y) {
		

		double score=0;
		double x_mod = 0;
	
		
		// Iteramos sobre los elementos puntuados por el usuario x
		for (int item : ratings.getItems(x)) {
			
			Double y_rating = ratings.getRating(y, item);
			Double x_rating = ratings.getRating(x, item);
			
			if (y_rating != null) {
				score+= x_rating * y_rating;
			
			}
			// Vamos calculando el modulo de x
			x_mod+= Math.pow(x_rating,2);
		}
		
		double y_mod = 0;
		
		// Calculamos el modulo de y
		for (int item : ratings.getItems(y)) {
			y_mod+= Math.pow(ratings.getRating(y, item), 2);
		}
		
		
		return score/(Math.sqrt(x_mod * y_mod));
	}
	
	@Override
	public String toString() {
		return "cosine";
	}

}
