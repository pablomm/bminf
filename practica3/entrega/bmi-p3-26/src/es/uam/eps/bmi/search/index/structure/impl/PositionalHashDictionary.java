package es.uam.eps.bmi.search.index.structure.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import es.uam.eps.bmi.search.index.structure.EditableDictionary;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPosting;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPostingsListEditable;

public class PositionalHashDictionary implements EditableDictionary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3367998737703066443L;
	
	private Map<String, PositionalPostingsListEditable>  postingLists = new HashMap<String, PositionalPostingsListEditable>();

	@Override
	public PostingsList getPostings(String term) throws IOException {
		return this.postingLists.get(term);
	}

	@Override
	public Collection<String> getAllTerms() {
		return this.postingLists.keySet();
	}

	@Override
	public long getDocFreq(String term) throws IOException {
		PostingsList post = this.getPostings(term);
		if(post == null) {
			return 0;
		}
		return post.size();
	}

	@Override
	public void add(String term, int docID) {
		// Este metodo no es usado, pero por conveniencia hereda de esta clase
	}
	
	public void add(String term, PositionalPosting post) throws IOException {
		
		PositionalPostingsListEditable postinglist = this.postingLists.get(term);
		if(postinglist == null) {
			postinglist = new PositionalPostingsListEditable();
			this.postingLists.put(term, postinglist);
		}
		postinglist.add(post);
		
	}

}
