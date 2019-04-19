package es.uam.eps.bmi.recsys.metric;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

public class Rmse implements Metric {

	Ratings test;
	
	public Rmse(Ratings test) {
		this.test = test;
	}

	@Override
	public double compute(Recommendation rec) {
		
		int n_test = 0;
		double rmse = 0;
		
		// Iteramos sobre los usuarios de la recomendacion
		for (int user : rec.getUsers()) {
			
			// Iteramos sobre lo que se le ha recomendado
			for (RankingElement e : rec.getRecommendation(user)) {
				
				if (! Double.isNaN(e.getScore())) {
					Double rating = test.getRating(user, e.getID());
					
					if (rating != null) {
						
						double diff = rating - e.getScore();
						rmse += diff * diff;
						n_test++;
						
					}
				}
			}
		}
		
		
		if (n_test > 0) {
			return Math.sqrt(rmse / n_test);
		}
		
		// Si no hay ninguno recomendado y puntuado a la vez devolvemos 0
		return 0;
		
	}
	
	@Override
	public String toString() {
		return "Rmse";
	}

}
