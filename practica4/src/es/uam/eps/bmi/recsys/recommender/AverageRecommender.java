package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;
import java.util.Map;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.RecommendationImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;

public class AverageRecommender extends AbstractRecommender {
	Map<Integer,Double> ratingSum;
	
	public AverageRecommender(Ratings ratings,double min) {
		super(ratings);
        ratingSum = new HashMap<Integer,Double>();
        for (int item : ratings.getItems()) {
        	int size=0;
            double sum = 0;
            for (int u : ratings.getUsers(item)) {
                sum += ratings.getRating(u, item);
                size++;
            }
            if (size>=min) ratingSum.put(item, sum/size);
        }
	}
	
	/**
	 * Not so sure if this is what this method is meant to do
	 */
	@Override
	public Recommendation recommend(int cutoff) {
		
		RecommendationImpl recommendation = new RecommendationImpl();
		
		for (int user : ratings.getUsers()) {
			// We create and populate each user's corresponding ranking
			RankingImpl ranking = new RankingImpl(cutoff);
			
			for (int item : ratingSum.keySet())
				// Get the average score of the item
				ranking.add(item, ratingSum.get(item));
			// The ranking is added to the return
			recommendation.add(user, ranking);
		}
		
		return recommendation;
	}

	@Override
	public double score(int user, int item) {
		
		// Scores the item given the score of the user and the overall average
		double average = 0.0;
		int size=0;
		
		// Calculate the average score of the item
		for (int user_aux : ratings.getUsers()) {
			average+=ratings.getRating(user_aux, item);
			size++;
		}
		average=average/size;
		
		return average;
	}
	
	public String toString() {
        return "average";
    }
}
