package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.RecommendationImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingImpl;

/**
 * @author Miguel Laseca, Pablo Marcos
 *
 */
public abstract class AbstractRecommender implements Recommender {

	protected Ratings ratings;
	
	public AbstractRecommender(Ratings ratings) {
		this.ratings=ratings;
	}
	
	@Override
	public Recommendation recommend(int cutoff) {
		RecommendationImpl recommendation = new RecommendationImpl();
		
		// Creamos una ranking de recomendacion por usuario
		for (int user : ratings.getUsers()) {
			
			RankingImpl ranking = new RankingImpl(cutoff);
			
			// Iteramos sobre todos los Items
			for (int item : ratings.getItems()) {
				
				// Si el usuario no ha puntuado anteriormente el item lo incluimos
				if (ratings.getRating(user, item) == null) {
					double puntuacion = this.score(user, item);
					if (puntuacion != Double.NEGATIVE_INFINITY)
					ranking.add(item, puntuacion);
				}
			}
			
			// Ranking final en la recomendacion
			recommendation.add(user, ranking);
		}
		
		return recommendation;
	}

}
