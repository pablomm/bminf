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
			
			Double x_score = xFeatures.getFeature(x, feature);
			Double y_score = yFeatures.getFeature(y, feature);
			
			if (y_score != null) {
				score+= x_score * y_score;
			}
						
			x_mod+= x_score * x_score;
		}
		
		for (F feature : yFeatures.getFeatures(y)) {
			y_mod += yFeatures.getFeature(y, feature);;
		}
		
		return score/(Math.sqrt(x_mod * y_mod));
	}
	
	@Override
	public String toString() {
		return "cosine on user centroid";
	}
	
}
