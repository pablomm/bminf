package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

public class NormUserKNNRecommender extends UserKNNRecommender {

	public NormUserKNNRecommender(Ratings ratings, Similarity sim, int k, int min) {
		super(ratings, sim, k);
	}
}
