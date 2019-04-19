package es.uam.eps.bmi.sna.ranking;

/**
 *
 * @author pablo
 * 
 * Aquí generalizamos las estructuras de ránking para que almacenen cualquier tipo de dato T con score (valor de métrica): usuarios, arcos, etc.
 * El tipo T lo declaramos comparable para deshacer los empates de score con un orden fijo de los elementos.
 */
public interface Ranking<T extends Comparable<T>> extends Iterable<RankingElement<T>> {
    public void add(T element, double score);
    public int size();
    public int totalSize();
}
