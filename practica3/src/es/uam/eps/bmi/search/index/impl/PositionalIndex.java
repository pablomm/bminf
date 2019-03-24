package es.uam.eps.bmi.search.index.impl;

import java.io.IOException;

public class PositionalIndex extends DiskIndex {

	public PositionalIndex(String indexFolder) throws IOException {
		super(indexFolder);
	}

}
