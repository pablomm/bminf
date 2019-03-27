package es.uam.eps.bmi.search.ranking.impl;

import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.ranking.SearchRankingIterator;
import java.util.PriorityQueue;

/**
 *
 * @author pablo
 */
public class RankingIteratorImpl implements SearchRankingIterator {
    SearchRankingDoc ranking[];
    int pos;

    public RankingIteratorImpl (PriorityQueue<SearchRankingDoc> r) {
        PriorityQueue<SearchRankingDoc> results = new PriorityQueue<SearchRankingDoc>(r);
        ranking = new SearchRankingDoc[results.size()];
        pos = ranking.length;
        while (!results.isEmpty())
            ranking[--pos] = results.poll();
    }
    
    public boolean hasNext() {
        return pos < ranking.length;
    }

    public SearchRankingDoc next() {
         return ranking[pos++];
    }
}
