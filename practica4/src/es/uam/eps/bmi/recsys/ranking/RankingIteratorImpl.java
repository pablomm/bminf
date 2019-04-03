package es.uam.eps.bmi.recsys.ranking;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author pablo
 */
public class RankingIteratorImpl implements Iterator<RankingElement> {
    RankingElement ranking[];
    int pos;

    public RankingIteratorImpl (PriorityQueue<RankingElement> r) {
        PriorityQueue<RankingElement> results = new PriorityQueue<RankingElement>(r);
        ranking = new RankingElement[results.size()];
        pos = ranking.length;
        while (!results.isEmpty())
            ranking[--pos] = results.poll();
    }
    
    public boolean hasNext() {
        return pos < ranking.length;
    }

    public RankingElement next() {
         return ranking[pos++];
    }
}
