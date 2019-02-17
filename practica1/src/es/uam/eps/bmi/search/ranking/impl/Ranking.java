package es.uam.eps.bmi.search.ranking.impl;

import java.util.Iterator;
import java.util.List;

import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

/**
 * Implementacion de SearchRanking
 *
 */
public class Ranking implements SearchRanking {

	/**
	 * Iterador del ranking
	 */
	private RankingIterator iterator;

	/**
	 * Numero de resultados en el ranking
	 */
	private int length;

	/**
	 * @param results Array con resultados de la consulta
	 */
	public Ranking(List<RankingDoc> results) {

		this.iterator = new RankingIterator(results);
		this.length = results.size();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<SearchRankingDoc> iterator() {
		// TODO Auto-generated method stub
		return iterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.ranking.SearchRanking#size()
	 */
	@Override
	public int size() {
		return this.length;
	}

}
