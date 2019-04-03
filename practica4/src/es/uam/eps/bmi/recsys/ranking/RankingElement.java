package es.uam.eps.bmi.recsys.ranking;

/**
 *
 * @author pablo
 * 
 * Puede ser un item recomendado con un score, o un usuario (vecino) con un valor de similitud, o un item con un valor de similitud.
 */
public abstract class RankingElement implements Comparable<RankingElement> {
    public abstract double getScore();
    public abstract int getID();
    
    public int compareTo(RankingElement elem) {
       int s = (int) Math.signum(elem.getScore() - getScore());
       // Si hay empate en score, ordenar por ID
       return s == 0? getID() - elem.getID() : s;
    }
}
