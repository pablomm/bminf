package es.uam.eps.bmi.search.ranking.impl;

import java.io.IOException;

import org.apache.lucene.search.ScoreDoc;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

/**
 * @author pablomm
 *
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
	private String path = null;

	/**
	 * Indice
	 */
	private Index index = null;

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

	/**
	 * Implementacion de Search ranking doc
	 * 
	 * @param index Indice donde se almacena el documento
	 * @param score Puntuacion del documento
	 * @param docID Id del documento
	 *
	 */
	public RankingDocImpl(Index index, ScoreDoc scoreDoc) {
		this.score = scoreDoc.score;
		this.docID = scoreDoc.doc;
		this.index = index;
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

		if (path != null)

			return path;

		return index.getDocPath(docID);
	}

}
