package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.uam.eps.bmi.recsys.data.Ratings;

/**
 * @author Pablo Marcos, Miguel Laseca
 *
 */
public class AverageRecommender extends AbstractRecommender {

	Map<Integer,Double> ratingAvg = new HashMap<Integer,Double>();
	
	/**
	 * Recomendador por media
	 * @param ratings Estructura con los ratings
	 * @param minRatings Minimo de ratings para recomendar un Item
	 */
	public AverageRecommender(Ratings ratings, int minRatings) {
        super(ratings);

        // Iteramos sobre todos los Items
        for (int item : ratings.getItems()) {
            
        	// Obtenemos los usuarios que han puntuado ese item
            Set<Integer> users = ratings.getUsers(item);
            int n_ratings = users.size();
            
            // Si hay mas que el minimo establecido incluimos la media
            if (n_ratings >= minRatings) {
            	double sum = 0;
            	 for (int u : users) {
            		 sum += ratings.getRating(u, item);
            	 }
            	 
            	 ratingAvg.put(item, sum / n_ratings);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see es.uam.eps.bmi.recsys.recommender.Recommender#score(int, int)
     */
	@Override
    public double score (int user, int item) {
    	
        Double puntuacion =  ratingAvg.get(item);
        // Como hemos puesto un cutoff es posible que no haya
        // Recomendacion para algunos, devolvemos -inf
        if (puntuacion == null) {
        	return Double.NEGATIVE_INFINITY;
        }
        
        return puntuacion;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
	@Override
    public String toString() {
        return "average";
    }

}
