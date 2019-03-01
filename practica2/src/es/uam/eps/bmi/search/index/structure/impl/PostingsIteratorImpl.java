package es.uam.eps.bmi.search.index.structure.impl;

import java.util.Iterator;
import java.util.List;

import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsListIterator;

public class PostingsIteratorImpl implements PostingsListIterator{

	/**
	 * Iterador interno sobre la lista de postings
	 */
	private Iterator<Posting> iterator;
	
	public PostingsIteratorImpl(List<Posting> postingList) {

		this.iterator = postingList.iterator();
	}

	@Override
	public boolean hasNext() {

		return iterator.hasNext();
	}

	@Override
	public Posting next() {

		return iterator.next();
	}

}
