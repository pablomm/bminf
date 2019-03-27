package es.uam.eps.bmi.search.ranking.impl;

import es.uam.eps.bmi.search.index.DocumentMap;
import es.uam.eps.bmi.search.ranking.SearchRankingIterator;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author pablo
 */
public class RankingImpl implements SearchRanking {
    DocumentMap docMap;
    PriorityQueue<SearchRankingDoc> rankingHeap;
    int cutoff;
    int nResults;
    
    public RankingImpl (DocumentMap m, int n) {
        docMap = m;
        cutoff = n;
        nResults = 0;
        rankingHeap = new PriorityQueue<SearchRankingDoc>(Comparator.reverseOrder());
    }
    
    public void add(int docID, double score) {
        if (rankingHeap.size() < cutoff || score > rankingHeap.peek().getScore()) {
            if (rankingHeap.size() == cutoff) rankingHeap.poll();
            rankingHeap.add(new RankingDocImpl(docMap, docID, score));
        }
        nResults++;
    }
    
    public SearchRankingIterator iterator() {
        return new RankingIteratorImpl(rankingHeap);
    }

    public int size() {
        return rankingHeap.size();
    }

    public int nResults() {
        return nResults;
    }
}
