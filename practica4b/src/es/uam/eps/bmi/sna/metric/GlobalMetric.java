package es.uam.eps.bmi.sna.metric;

import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

/**
 *
 * @author pablo
 * 
 * Métrica global sobre una red.
 */
public interface GlobalMetric<U> {
    public double compute(UndirectedSocialNetwork<U> network);
}
