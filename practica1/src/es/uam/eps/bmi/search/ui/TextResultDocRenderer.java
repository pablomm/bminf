package es.uam.eps.bmi.search.ui;

import java.io.IOException;

import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

public class TextResultDocRenderer {

	private SearchRankingDoc rankingDoc;

	public TextResultDocRenderer(SearchRankingDoc rankingDoc) {
		this.rankingDoc = rankingDoc;
	}

	@Override
	public String toString() {
		String s = "";

		try {
			s = String.format("%.8f", rankingDoc.getScore()) + "\t" + rankingDoc.getPath();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}

}
