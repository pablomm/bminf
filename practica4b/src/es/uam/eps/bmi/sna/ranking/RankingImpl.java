package es.uam.eps.bmi.sna.ranking;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author pablo
 * 
 * Implementación de heap de ranking. 
 */
public class RankingImpl<T extends Comparable<T>> implements Ranking<T> {
    PriorityQueue<RankingElement<T>> rankingHeap;
    int cutoff;
    int nElements;
    
    public RankingImpl () {
        this(Integer.MAX_VALUE);
    }
    
    public RankingImpl (int n) {
        cutoff = n;
        nElements = 0;
        rankingHeap = new PriorityQueue<RankingElement<T>>(Comparator.reverseOrder());
    }
    
    public void add(T element, double score) {
        if (rankingHeap.size() < cutoff || score > rankingHeap.peek().getScore() 
                // Para fijar el elemento que entra en el ránking en caso de empate en score (y que no dependa del orden 
                // en que se recorren externamente los elementos.
                || (score == rankingHeap.peek().getScore() && element.compareTo(rankingHeap.peek().geElement()) > 0)) {
            if (rankingHeap.size() == cutoff) rankingHeap.poll();
            rankingHeap.add(new RankingElementImpl<T>(element, score));
        }
        nElements++;
    }
    
    public Iterator<RankingElement<T>> iterator() {
        return new RankingIteratorImpl<T>(rankingHeap);
    }

    public int size() {
        return rankingHeap.size();
    }

    public int totalSize() {
        return nElements;
    }
}
