package es.uam.eps.bmi.search.ranking.impl;


import java.util.Iterator;
import java.util.List;

import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.ranking.SearchRankingIterator;

/**
 * Implementacion propia del iterador del ranking
 * Es un wrapper entre el iterador de la lista interna de resultados
 */
public class RankingIteratorImpl  implements SearchRankingIterator {
	
	/**
	 * Iterador de resultados
	 */
	private Iterator<RankingDocImpl> it;

    public RankingIteratorImpl (List<RankingDocImpl> results) {
        this.it = results.iterator();
    }

	@Override
	public boolean hasNext() {

		return it.hasNext();
	}

	@Override
	public SearchRankingDoc next() {
		
		return it.next();
	}

}
