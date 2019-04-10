package es.uam.eps.bmi.recsys.recommender;

import java.util.ArrayList;
import java.util.Set;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.RecommendationImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;

public class AverageRecommender implements Recommender {

	Ratings rat;
	// List with the items with ratings over the specified minimum
	ArrayList<Integer> itemsList=new ArrayList<Integer>();
	
	public AverageRecommender(Ratings ratings,double min) {
		rat=ratings;
		
		// Fill the items list with those who satisfy the minimum rating
		for (int item : ratings.getItems()) {
			double average=0.0;
			Set<Integer> users = rat.getUsers(item);
			
			// Calculate the average score of the item
			for (int user : users)
				average+=rat.getRating(user, item);
			average=average/users.size();
			
			// Add the item if it passes the filter
			if (average>=min) itemsList.add(item);
		}
	}
	
	/**
	 * Not so sure if this is what this method is meant to do
	 */
	@Override
	public Recommendation recommend(int cutoff) {
		
		RecommendationImpl recommendation = new RecommendationImpl();
		
		for (int user : rat.getUsers()) {
			// We create and populate each user's corresponding ranking
			RankingImpl ranking = new RankingImpl(cutoff);
			for (int item : rat.getItems(user))
				// Filter the items that satisfy the minimum rating 
				if (itemsList.contains(item))
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
