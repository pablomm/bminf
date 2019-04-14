package es.uam.eps.bmi.recsys.recommender.similarity;

import es.uam.eps.bmi.recsys.data.Features;

public class CosineFeatureSimilarity<F> extends FeatureSimilarity<F> {
	
	public CosineFeatureSimilarity (Features<F> features) {
		super(features);
	}

	@Override
	public double sim(int x, int y) {
		// The total score, the similarity between the two users
		double score=0;
		// The module of both users' scores
		double x_mod=0, y_mod=0;
		for (F feature : xFeatures.getFeatures(x)) {
			// Score based on the scores both users do to each item they have in common
			// If one of the users didn't score the item this adds 0 to score 
			score+=xFeatures.getFeature(x, feature)*yFeatures.getFeature(y, feature);
			// Increment the modules with the 2nd power of each score
			x_mod+=Math.pow(xFeatures.getFeature(x, feature),2.);
			y_mod+=Math.pow(yFeatures.getFeature(y, feature),2.);
		}
		
		return score/(Math.sqrt(x_mod)*Math.sqrt(y_mod));
	}
	
}
