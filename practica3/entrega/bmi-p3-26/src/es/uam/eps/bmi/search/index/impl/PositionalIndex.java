package es.uam.eps.bmi.search.index.impl;

import java.io.IOException;

import es.uam.eps.bmi.search.index.structure.Dictionary;

public class PositionalIndex extends SerializedRAMIndex {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1141107483525702942L;

	public PositionalIndex(String indexFolder) throws IOException {
		super(indexFolder);
	}
	
	public PositionalIndex(Dictionary dic, int nDocs) {
		super(dic, nDocs);
	}

}
