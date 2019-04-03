package es.uam.eps.bmi.recsys.metric;

import es.uam.eps.bmi.recsys.Recommendation;

/**
 *
 * @author pablo
 */
public interface Metric {
    public double compute(Recommendation rec);    
}
