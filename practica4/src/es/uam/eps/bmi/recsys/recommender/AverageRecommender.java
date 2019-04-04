package es.uam.eps.bmi.recsys.recommender;

import java.util.Set;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.RecommendationImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;

public class AverageRecommender implements Recommender {

	double min;
	Ratings rat;
	
	public AverageRecommender(Ratings ratings,double min) {
		this.min=min;
		rat=ratings;
	}
	
	@Override
	public Recommendation recommend(int cutoff) {
		
		RecommendationImpl recommendation = new RecommendationImpl();
		
		for (int user : rat.getUsers()) {
			// We create and populate each user's corresponding ranking
			RankingImpl ranking = new RankingImpl(cutoff);
			for (int item : rat.getItems(user))
				ranking.add(item, rat.getRating(user, item));
			// The ranking is added to the return
			recommendation.add(user, ranking);
		}
		
		return recommendation;
	}

	@Override
	public double score(int user, int item) {
		
		// Scores the item given the score of the user and the overall average
		double average = 0.0;
		Set<Integer> users = rat.getUsers(item);
		
		// Calculate the average score of the item
		for (int user_aux : users)
			average+=rat.getRating(user_aux, item);
		average=average/users.size();
		
		return rat.getRating(user, item)/average;
	}
	
	public String toString() {
        return "average";
    }
}
