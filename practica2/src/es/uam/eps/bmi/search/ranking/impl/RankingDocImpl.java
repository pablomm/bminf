package es.uam.eps.bmi.search.ranking.impl;


import java.io.IOException;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

/**
 * Implementacion del SarchRankingDoc
 */
public class RankingDocImpl extends SearchRankingDoc {

	/**
	 * Puntuacion del documento
	 */
	private double score;

	/**
	 * Id del documento
	 */
	private int docID;

	/**
	 * Ruta del documento
	 */
	private String path;
	
	
	/**
	 * Indice donde esta almacenado el documento 
	 */
	private Index index = null;

	/**
	 * Implementacion de Search ranking doc
	 * 
	 * @param score Puntuacion del documento
	 * @param docID Id del documento
	 */
	public RankingDocImpl(Index index, double score, int docID) {
		this(score, docID, null);
		this.index = index;

	}


	/**
	 * Implementacion de Search ranking doc
	 * 
	 * @param score Puntuacion del documento
	 * @param docID Id del documento
	 * @param path  Ruta del documento
	 */
	public RankingDocImpl(double score, int docID, String path) {
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
		if (path == null) {
			this.path = this.index.getDocPath(this.docID);
		}
			return path;
	}

	/**
	 * @param path Nuevo path 
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
