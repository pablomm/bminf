package es.uam.eps.bmi.search.lucene;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.lucene.LuceneRanking;

public class LuceneEngine extends AbstractEngine {

	private IndexSearcher searcher = null;
	private String indexPath;

	/**
	 * @param indexPath Path al indice
	 * @throws IOException
	 */
	public LuceneEngine(String indexPath) throws IOException {

		super(new LuceneIndex(indexPath));
		this.indexPath = indexPath;

	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		// Separamos la consulta por blancos
		String[] terms = query.split(" ");

		// Constructor de la consulta booleana
		BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

		// Incluimos todas las palabras de la consulta en el constructor
		for (String term : terms) {
			TermQuery termQuery = new TermQuery(new Term("content", term));

			Occur ocurr = null;

			if (term.startsWith("-")) {
				// Si el termino comienza en - filtramos esa palabra
				term = term.substring(1);
				ocurr = BooleanClause.Occur.MUST_NOT;

			} else if (term.startsWith("+")) {
				// Si el termino comienza en + obligamos a que contenga la palabra
				term = term.substring(1);
				ocurr = BooleanClause.Occur.MUST;

			} else {
				// En otro caso la aparicion de la palabra no es obligatoria
				ocurr = BooleanClause.Occur.SHOULD;

			}

			queryBuilder.add(termQuery, ocurr);
		}

		// Construimos la query y la ejecutamos
		BooleanQuery booleanQuery = queryBuilder.build();
		TopDocs topDocs = this.getSearcher().search(booleanQuery, cutoff);

		// Devolvemos el ranking con los resultados de la busqueda
		return new LuceneRanking((LuceneIndex) index, topDocs.scoreDocs);

	}

	/**
	 * Singleton para indexSearcher
	 * 
	 * @return
	 */
	private IndexSearcher getSearcher() {
		if (this.searcher == null) {

			LuceneIndex luceneIndex;
			try {
				luceneIndex = new LuceneIndex(this.indexPath);
				this.searcher = new IndexSearcher(luceneIndex.getIndex());
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
		return this.searcher;
	}

}
