package es.uam.eps.bmi.search.ranking.lucene;

import java.util.Iterator;

import org.apache.lucene.search.ScoreDoc;

import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class LuceneRanking implements SearchRanking {

	private LuceneRankingIterator rankingIterator;
	
	public LuceneRanking(LuceneIndex index, ScoreDoc[] r) {
		this.rankingIterator = new LuceneRankingIterator(index, r);
	}
	
	
	@Override
	public Iterator<SearchRankingDoc> iterator() {
		// TODO Auto-generated method stub
		return rankingIterator;
	}

	@Override
	public int size() {
		
		// Devuelve la longitud de resultados
		// Almacenados en el iterador
		return rankingIterator.results.length;
	}

}
