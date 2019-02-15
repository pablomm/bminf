package es.uam.eps.bmi.search.ranking;

import java.io.IOException;

public abstract class SearchRankingDoc implements Comparable<SearchRankingDoc> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SearchRankingDoc o) {

		return (int) Math.signum(this.getScore() - o.getScore());
	}

	/**
	 * @return Score del documento
	 */
	public abstract double getScore();

	/**
	 * @return Id del documento
	 */
	public abstract int getDocID();

	/**
	 * @return Path del documento
	 * @throws IOException
	 */
	public abstract String getPath() throws IOException;

}
