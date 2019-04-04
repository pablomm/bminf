package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;

import es.uam.eps.bmi.recsys.Recommendation;

public class UserKNNRecommender implements Recommender {
	
	// off-line neighbourhood checking  
	HashMap<Integer,HashMap<Integer,Double>> neighbourhood=
			new HashMap<Integer,HashMap<Integer,Double>> ();

	public UserKNNRecommender () {
		
	}
	
	@Override
	public Recommendation recommend(int cutoff) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double score(int user, int item) {
		// TODO Auto-generated method stub
		return 0;
	}

}
