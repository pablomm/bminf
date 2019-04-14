package es.uam.eps.bmi.recsys.recommender.similarity;

import es.uam.eps.bmi.recsys.data.Ratings;

public class CosineUserSimilarity implements Similarity {

	Ratings ratings;
	
	public CosineUserSimilarity(Ratings r) {
		ratings=r;
	}
	
	@Override
	public double sim(int x, int y) {
		
		// The total score, the similarity between the two users
		double score=0;
		// The module of both users' scores
		double x_mod=0, y_mod=0;
		
		for (int item : ratings.getItems()) {
			// Score based on the scores both users do to each item they have in common
			// If one of the users didn't score the item this adds 0 to score 
			score+=ratings.getRating(x, item)*ratings.getRating(y, item);
			// Increment the modules with the 2nd power of each score
			x_mod+=Math.pow(ratings.getRating(x, item),2.);
			y_mod+=Math.pow(ratings.getRating(y, item),2.);
		}
		
		return score/(Math.sqrt(x_mod)*Math.sqrt(y_mod));
	}

}
