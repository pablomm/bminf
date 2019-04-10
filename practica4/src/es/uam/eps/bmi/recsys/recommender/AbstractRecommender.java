package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.RecommendationImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;

public abstract class AbstractRecommender implements Recommender {

	protected Ratings ratings;
	
	public AbstractRecommender(Ratings ratings) {
		this.ratings=ratings;
	}
	
	@Override
	public Recommendation recommend(int cutoff) {
		RecommendationImpl recommendation = new RecommendationImpl();
		
		for (int user : ratings.getUsers()) {
			// We create and populate each user's corresponding ranking
			RankingImpl ranking = new RankingImpl(cutoff);
			for (int item : ratings.getItems(user))
				ranking.add(item, ratings.getRating(user, item));
			// The ranking is added to the return
			recommendation.add(user, ranking);
		}
		
		return recommendation;
	}

	@Override
	public double score(int user, int item) {
		return ratings.getRating(user, item);
	}

}
