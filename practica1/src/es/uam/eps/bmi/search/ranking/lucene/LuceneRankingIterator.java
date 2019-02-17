package es.uam.eps.bmi.search.ranking.lucene;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.ranking.SearchRankingIterator;
import org.apache.lucene.search.ScoreDoc;

/**
 *
 * @author pablo
 */
public class LuceneRankingIterator implements SearchRankingIterator {
	ScoreDoc results[];
	Index index;
	int n = 0;

	public LuceneRankingIterator(Index idx, ScoreDoc r[]) {
		index = idx;
		results = r;
	}

	// Empty result list
	public LuceneRankingIterator() {
		index = null;
		results = new ScoreDoc[0];
	}

	public boolean hasNext() {
		return n < results.length;
	}

	public SearchRankingDoc next() {
		return new LuceneRankingDoc(index, results[n++]);
	}
}
