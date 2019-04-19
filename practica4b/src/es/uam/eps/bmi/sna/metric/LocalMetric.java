package es.uam.eps.bmi.sna.metric;

import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

/**
 *
 * @author pablo
 * 
 * Puede aplicarse a usuarios (T representa usuario tipo U) o arcos (T representa arcos tipo Edge<U>).
 */
public interface LocalMetric<T extends Comparable<T>,U> {
    public Ranking<T> compute(UndirectedSocialNetwork<U> network);
    public double compute(UndirectedSocialNetwork<U> network, T element);
}
