package es.uam.eps.bmi.search.ranking.impl;

import java.util.List;

import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.ranking.SearchRankingIterator;

/**
 * Implementacion de SearchRankingIterator
 *
 */
public class RankingIterator implements SearchRankingIterator{
	
	
	/**
	 * Longitud del Ranking
	 */
	private int length;
	
	
	/**
	 * Posicion actual en el ranking
	 */
	private int n = 0;
	
	
	/**
	 * Lista de resultados
	 */
	private List<RankingDoc> results;
	
	/**
	 * @param results Lista de resultados de la consulta
	 */
	public RankingIterator(List<RankingDoc> results) {
		this.length = results.size();
		this.results = results;

		
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {

		return n < length;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public SearchRankingDoc next() {
		
		if(n >= length) {
			return null;
		}

		return results.get(n++);
	}
	

}
