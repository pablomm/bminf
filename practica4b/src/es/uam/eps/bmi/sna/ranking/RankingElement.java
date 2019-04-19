package es.uam.eps.bmi.sna.ranking;

/**
 *
 * @author pablo
 * 
 */
public abstract class RankingElement<T extends Comparable<T>> implements Comparable<RankingElement<T>> {
    public abstract double getScore();
    public abstract T geElement();
    
    public int compareTo(RankingElement<T> elem) {
       int s = (int) Math.signum(elem.getScore() - getScore());
       // Si hay empate en score, ordenar por ID
       return s == 0? geElement().compareTo(elem.geElement()) : s;
    }
}
