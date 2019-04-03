package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.data.Ratings;
import java.util.Random;

/**
 *
 * @author pablo
 */
public class RandomRecommender extends AbstractRecommender {
    Random rnd;
    
    public RandomRecommender(Ratings ratings) {
        super(ratings);
        rnd = new Random();
    }
    
    public double score (int user, int item) {
        return rnd.nextDouble();
    }

    public String toString() {
        return "random";
    }
}
