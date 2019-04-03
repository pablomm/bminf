package es.uam.eps.bmi.recsys.ranking;

/**
 *
 * @author pablo
 * 
 * En esta práctica esta interfaz sirve para recomendaciones, pero también para ránkings de los k vecinos más similares.
 */
public interface Ranking extends Iterable<RankingElement> {
    public void add(int element, double score);
    public int size();
    public int totalSize();
}
