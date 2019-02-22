package es.uam.eps.bmi.search.ranking.impl;

import org.apache.lucene.search.ScoreDoc;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.ranking.SearchRankingIterator;

public class RankingIteratorImpl  implements SearchRankingIterator {
	
	private ScoreDoc results[];
    private Index index;
    int n = 0;

    public RankingIteratorImpl (Index index, ScoreDoc results[]) {
        this.index = index;
        this.results = results;
    }

	@Override
	public boolean hasNext() {

		return n < results.length;
	}

	@Override
	public SearchRankingDoc next() {
		return new RankingDocImpl(index, results[n++]);
	}

}
