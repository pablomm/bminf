package es.uam.eps.bmi.recsys.recommender.similarity;

import es.uam.eps.bmi.recsys.data.Ratings;

/**
 * @author Pablo Marcos, Miguel Laseca
 *
 */
public class CosineItemSimilarity implements Similarity {

	Ratings ratings;
	
	public CosineItemSimilarity(Ratings r) {
		ratings=r;
	}
	
	@Override
	public double sim(int x, int y) {
		
		// The total score, the similarity between the two users
		double score=0;
		
		// The module of both users' scores
		double x_mod=0, y_mod=0;
		
		// Score cruzada y modulo de x
		for(int user : ratings.getUsers(x)) {
			
			Double x_rating = ratings.getRating(user, x);
			Double y_rating = ratings.getRating(user, y);
			
			if (y_rating != null)
				score+= x_rating * y_rating;

			x_mod += x_rating * x_rating;
			
		}
		
		// Modulo de y
		for (int user : ratings.getUsers(y)) {
	
			Double y_rating = ratings.getRating(user, y);
			y_mod += y_rating*y_rating;
		}
		
		return score/(Math.sqrt(x_mod * y_mod));
	}
	
	@Override
	public String toString() {
		return "cosine";
	}

}
