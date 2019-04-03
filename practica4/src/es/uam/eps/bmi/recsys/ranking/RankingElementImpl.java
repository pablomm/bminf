package es.uam.eps.bmi.recsys.ranking;

/**
 *
 * @author pablo
 * 
 * Puede ser un item recomendado con un score, o un usuario (vecino) con un valor de similitud, o un item con un valor de similitud.
 */
public class RankingElementImpl extends RankingElement {
    int id;
    double score;
    
    public RankingElementImpl (int elem, double s) {
        id = elem;
        score = s;
    }
    public double getScore() {
        return score;
    }

    public int getID() {
        return id;
    }
}
