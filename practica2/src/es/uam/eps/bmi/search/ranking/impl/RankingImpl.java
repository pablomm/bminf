package es.uam.eps.bmi.search.ranking.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.apache.lucene.search.ScoreDoc;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class RankingImpl implements SearchRanking {

	private PriorityQueue<ScoreDoc> heap;
	private int cutoff;
	private Index index;

	public RankingImpl(Index index, int cutoff) {
		this.cutoff = cutoff;
		this.index = index;
		// Heap con orden ascendente
		heap = new PriorityQueue<ScoreDoc>(cutoff);
	}

	@Override
	public Iterator<SearchRankingDoc> iterator() {

		return new RankingIteratorImpl(index, heap.toArray(new ScoreDoc[0]));
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void add(int docId, double score) {
		
		heap.add(new ScoreDoc(docId, (float) score));
		
	}

}
