package es.uam.eps.bmi.recsys.recommender.similarity;

import es.uam.eps.bmi.recsys.data.Ratings;

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
		
		for (int user : ratings.getUsers()) {
			// Score based on the scores both users do to each item they have in common
			// If the user didn't score X or Y this adds 0 to score
			score+=ratings.getRating(user, x)*ratings.getRating(user, y);
			// Increment the modules with the 2nd power of each score
			x_mod+=Math.pow(ratings.getRating(user, x),2.);
			y_mod+=Math.pow(ratings.getRating(user, y),2.);
		}
		
		return score/(Math.sqrt(x_mod)*Math.sqrt(y_mod));
	}

}
