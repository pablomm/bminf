package es.uam.eps.bmi.search.ranking.impl;

import java.io.IOException;

import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

/**
 * Implementacion de SearchRankingDoc
 *
 */
public class RankingDoc extends SearchRankingDoc {

	/**
	 * Score del documento en el ranking
	 */
	private double score;

	/**
	 * Id del documento
	 */
	private int docID;

	/**
	 * Path del documento
	 */
	private String path;

	/**
	 * @param score Score del documento en el ranking
	 * @param docID Id del documento
	 * @param path  Path del documento
	 */
	public RankingDoc(double score, int docID, String path) {
		this.score = score;
		this.docID = docID;
		this.path = path;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.ranking.SearchRankingDoc#getScore()
	 */
	@Override
	public double getScore() {

		return score;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.ranking.SearchRankingDoc#getDocID()
	 */
	@Override
	public int getDocID() {

		return docID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.ranking.SearchRankingDoc#getPath()
	 */
	@Override
	public String getPath() throws IOException {

		return path;
	}

}
