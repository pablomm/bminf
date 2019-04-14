package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.RecommendationImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

public class NormUserKNNRecommender extends UserKNNRecommender {
	
	// Array containing the items that pass the minimum neighbors required
	// private ArrayList<Integer> filtered_items;
	private int n;

	public NormUserKNNRecommender(Ratings ratings, Similarity sim, int k, int min) {
		
		super(ratings, sim, k);
		n=min;
		
		/*filtered_items=new ArrayList<Integer>();
		for (int item : ratings.getItems())
			if (ratings.getUsers(item).size()>=min)
				filtered_items.add(item);*/
	}
	
	@Override
	public Recommendation recommend(int cutoff) {
		RecommendationImpl recommendation = new RecommendationImpl();
		
		for (int user : ratings.getUsers()) {
			// We create and populate each user's corresponding ranking
			RankingImpl ranking = new RankingImpl(cutoff);
			for (int item : ratings.getItems(user))
				// Add the item only in case it fulfills the requirement of a minimal amount of ratings 
				if (ratings.getUsers(item).size()>=n)
					ranking.add(item, score(user, item));
			// The ranking is added to the return
			recommendation.add(user, ranking);
		}
		
		return recommendation;
	}
}
