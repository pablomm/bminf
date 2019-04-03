package es.uam.eps.bmi.recsys.ranking;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author pablo
 * 
 * Implementación de heap de ranking. Vale tanto para resultados de una recomendación, como para ordenar y seleccionar k vecinos más similares.
 */
public class RankingImpl implements Ranking {
    PriorityQueue<RankingElement> rankingHeap;
    int cutoff;
    int nElements;
    
    public RankingImpl (int n) {
        cutoff = n;
        nElements = 0;
        rankingHeap = new PriorityQueue<RankingElement>(Comparator.reverseOrder());
    }
    
    public void add(int id, double score) {
        if (rankingHeap.size() < cutoff || score > rankingHeap.peek().getScore()) {
            if (rankingHeap.size() == cutoff) rankingHeap.poll();
            rankingHeap.add(new RankingElementImpl(id, score));
        }
        nElements++;
    }
    
    public Iterator<RankingElement> iterator() {
        return new RankingIteratorImpl(rankingHeap);
    }

    public int size() {
        return rankingHeap.size();
    }

    public int totalSize() {
        return nElements;
    }
}
