package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.data.Ratings;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pablo
 */
public class MajorityRecommender extends AbstractRecommender {
    Map<Integer,Double> ratingSum;
    
    public MajorityRecommender(Ratings ratings) {
        super(ratings);
        ratingSum = new HashMap<Integer,Double>();
        for (int item : ratings.getItems()) {
            double sum = 0;
            for (int u : ratings.getUsers(item))
                sum += ratings.getRating(u, item);
            ratingSum.put(item, sum);
        }
    }
    
    public double score (int user, int item) {
        return ratingSum.get(item);
    }

    public String toString() {
        return "majority";
    }
}
