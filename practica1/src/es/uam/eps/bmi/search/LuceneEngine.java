package es.uam.eps.bmi.search;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;

public class LuceneEngine extends AbstractEngine {

	public IndexSearcher searcher = null;
	QueryParser parser = null;
	Analyzer analyzer = null;
	
	public LuceneEngine(LuceneIndex idx) {
		super(idx);
		searcher = new IndexSearcher(idx.getIndex());
		analyzer = new StandardAnalyzer();
		
		parser = new QueryParser("content", analyzer);
	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException, ParseException {
		// TODO Auto-generated method stub
		Query q = parser.parse(query);
		ScoreDoc result[] = searcher.search(q, cutoff).scoreDocs;
	}

}
