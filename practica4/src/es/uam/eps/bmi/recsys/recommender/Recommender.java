package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.Recommendation;

/**
 *
 * @author pablo
 */
public interface Recommender {
    public Recommendation recommend(int cutoff);
    public double score(int user, int item);
}
