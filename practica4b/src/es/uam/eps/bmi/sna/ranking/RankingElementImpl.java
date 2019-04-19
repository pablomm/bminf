package es.uam.eps.bmi.sna.ranking;

/**
 *
 * @author pablo
 */
public class RankingElementImpl<T extends Comparable<T>> extends RankingElement<T> {
    T element;
    double score;
    
    public RankingElementImpl (T elem, double s) {
        element = elem;
        score = s;
    }
    public double getScore() {
        return score;
    }

    public T geElement() {
        return element;
    }
}
