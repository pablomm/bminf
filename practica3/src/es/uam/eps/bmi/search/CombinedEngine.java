package es.uam.eps.bmi.search;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import es.uam.eps.bmi.search.index.DocumentMap;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class CombinedEngine implements SearchEngine, DocumentMap {
	
	private SearchEngine[] searchEngines;
	private double[] weights;
	private int nEngines;
	private HashMap<String,Integer> docIDs = new HashMap<String, Integer>();
	private HashMap<Integer,String> docPaths = new HashMap<Integer, String>();
	private int numDocs = 0;

	public CombinedEngine(SearchEngine[] searchEngines, double[] weights) {
		this.searchEngines = searchEngines;
		this.weights = weights;
		this.nEngines = this.searchEngines.length;
		
	}
	
	private Integer getDocId(String path) {
		
		Integer docID = this.docIDs.get(path);
		if(docID == null) {
			this.docIDs.put(path, this.numDocs);
			this.docPaths.put(this.numDocs, path);
			this.numDocs++;
		}
		return this.numDocs - 1;
		
	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
		
		SearchRanking[] rankings = new SearchRanking[this.nEngines];
		for(int i=0; i < nEngines; i++) {
			rankings[i] = this.searchEngines[i].search(query, cutoff);
		}
		
		HashMap<Integer, Double> scores = new HashMap<Integer, Double>();
		
		
		for(int i=0; i <nEngines; i++) {
			
			double min = Integer.MAX_VALUE;
			double max = Integer.MIN_VALUE;
			
			// Obtenemos maximo y minimo para estandarizar las puntuaciones
			for(SearchRankingDoc doc : rankings[i]) {
				min = Math.min(doc.getScore(), min);
				max = Math.max(doc.getScore(), max);
			}
			// Incluimos puntuaciones estandarizadas
			double l = max - min;
			for(SearchRankingDoc doc : rankings[i]) {
				String path = doc.getPath();
				double score = scores.getOrDefault(path, 0.0);
				
				int docId = this.getDocId(path);
				scores.put(docId, score + this.weights[i]*(doc.getScore() - min)/l);
			}	
		}
		
		// Incluimos en un ranking comun
		RankingImpl rank = new RankingImpl(this, cutoff);
		
		for(Entry<Integer, Double> entry : scores.entrySet()) {
			rank.add(entry.getKey(), entry.getValue());
		}
		
		return rank;
	}

	@Override
	public DocumentMap getDocMap() {
		return this;
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		return this.docPaths.get(docID);
	}

	@Override
	public double getDocNorm(int docID) throws IOException {
		
		return 1;
	}

	@Override
	public int numDocs() {
		return this.numDocs;
	}

}
