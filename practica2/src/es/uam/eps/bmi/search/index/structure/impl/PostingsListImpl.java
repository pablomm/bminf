package es.uam.eps.bmi.search.index.structure.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;

/**
 * Clase para almacenar la lista de postings de un termino
 */
public class PostingsListImpl implements PostingsList, Serializable {

	/**
	 * Serial version para almacenar en disco
	 */
	private static final long serialVersionUID = 6030912350618715235L;

	/**
	 * Lista con postings del termino
	 */
	private List<Posting> postingList;

	/**
	 * Frecuencia total del termino
	 */
	private long termTotalFreq = 0;

	/**
	 * @param postingList
	 */
	public PostingsListImpl(List<Posting> postingList) {
		this.postingList = postingList;
	}

	public PostingsListImpl() {
		this(new ArrayList<Posting>());
	}

	@Override
	public Iterator<Posting> iterator() {

		return new PostingsIteratorImpl(postingList);
	}

	@Override
	public int size() {

		return postingList.size();
	}

	public long getTotalFreq() {
		return this.termTotalFreq;
	}

	/**
	 * @param posting Posting a incluir en la lista
	 */
	public void add(Posting posting) {
		postingList.add(posting);

		// Actualizamos dinamicamente la frecuencia total
		// del termino
		this.termTotalFreq += posting.getFreq();
	}

	/**
	 * @param docId
	 * @param freq
	 */
	public void add(int docId, long freq) {
		add(new Posting(docId, freq));
	}

}
