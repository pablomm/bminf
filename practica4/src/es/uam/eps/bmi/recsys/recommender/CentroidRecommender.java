package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.data.Features;
import es.uam.eps.bmi.recsys.data.FeaturesImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.recommender.similarity.FeatureSimilarity;

public class CentroidRecommender<F> extends AbstractRecommender {
	
	// Store the users 'features'
	private FeatureSimilarity<F> sim;

	
	/**
	 * I'm still not sure about using |u| for the centroid
	 * @param rat Ratings from which generate the recommender
	 * @param s Calculator of similarity between users and items
	 */
	public CentroidRecommender (Ratings rat, FeatureSimilarity<F> s) {
		// Initialize the recommender
		super(rat);
		sim=s;
		// Store the users features
		Features<F> features=new FeaturesImpl<F> ();
		// Extract the items features
		Features<F> item_features=s.getFeatures();
		
		// Generate all users vectors of features
		for (int user : ratings.getUsers()) {
			// Stores |u|
			double user_mod=0;
			
			for (int item : ratings.getItems(user)) {
				double r=ratings.getRating(user, item);
				user_mod+=Math.pow(r, 2.);
				for (F feature : item_features.getFeatures(item)) {
					// The total value of each feature is the sum of each items corresponding feature by the rating the user gave to the item
					double new_value=features.getFeature(user, feature) +
							item_features.getFeature(item,feature)*r;
					
					features.setFeature(user, feature, new_value);
				}
			}
			
			user_mod=Math.sqrt(user_mod);
			// Divide by |u|
			for (F feature : features.getFeatures(user))
				features.setFeature(user, feature, features.getFeature(user, feature)/user_mod);
		}
		sim.setXFeatures(features);
	}
	
	@Override
	public double score(int user, int item) {
		return sim.sim(user, item);
	}
}
