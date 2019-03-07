package es.uam.eps.bmi.search.ranking.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class RankingImpl implements SearchRanking {

	private PriorityQueue<RankingDocImpl> heap;

	private Index index;
	private int cutoff;


	public RankingImpl(Index index, int cutoff) {

		this.index = index;
		this.cutoff = cutoff;
		
		// Heap con orden ascendente
		heap = new PriorityQueue<RankingDocImpl>(cutoff+1, Collections.reverseOrder());

	}

	@Override
	public Iterator<SearchRankingDoc> iterator() {
		
		
		
		List<RankingDocImpl> results = new ArrayList<RankingDocImpl>();
		// Extracts in order 
		while(!heap.isEmpty())
			results.add(heap.poll());
		
		Collections.reverse(results);

		// Resultados ordenados inversamente
		return new RankingIteratorImpl(results);
	}

	@Override
	public int size() {

		return heap.size();
	}
	

	public void add(int docId, double score) {
		
		heap.add(new RankingDocImpl(index, score, docId));
		
		if (heap.size() > cutoff) {
			heap.poll();
		}
	}

}
