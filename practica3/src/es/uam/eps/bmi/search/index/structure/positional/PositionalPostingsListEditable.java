package es.uam.eps.bmi.search.index.structure.positional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;

public class PositionalPostingsListEditable implements PostingsList, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<PositionalPosting> postings;
	
	public PositionalPostingsListEditable(List<PositionalPosting> postings) {
		this.postings = postings;
	}
	
	public PositionalPostingsListEditable() {
		this(new ArrayList<PositionalPosting>());
	}
	
	public void add(PositionalPosting posting) {
		this.postings.add(posting);
	}

	@Override
	public Iterator<Posting> iterator() {

		return new PositionalPostingsListIterator(postings);
	}

	@Override
	public int size() {
		return this.postings.size();
	}

}

class PositionalPostingsListIterator implements Iterator<Posting> {

	private Iterator<PositionalPosting> iterator;

	public PositionalPostingsListIterator(List<PositionalPosting> postings) {
		this.iterator = postings.iterator();
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return iterator.hasNext();
	}

	@Override
	public Posting next() {
		// TODO Auto-generated method stub
		return iterator.next();
	}
	
	
	
}


