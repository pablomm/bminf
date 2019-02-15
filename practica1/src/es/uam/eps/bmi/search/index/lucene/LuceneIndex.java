package es.uam.eps.bmi.search.index.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.freq.FreqVector;
import es.uam.eps.bmi.search.index.freq.lucene.LuceneFreqVector;

/**
 * Implementacion de indice con Lucene
 *
 */
public class LuceneIndex implements Index {

	/**
	 * Index Reader de Lucene
	 */
	private IndexReader index = null;

	/**
	 * Constructor del indice implementado con las funciones de Lucene
	 * 
	 * @param indexPath: Path del indice creado anteriormente
	 * @throws IOException
	 */
	public LuceneIndex(String indexPath) throws IOException {

		// Directorio donde esta guardado el indice creado por Builder
		Directory directory = FSDirectory.open(Paths.get(indexPath));

		this.index = DirectoryReader.open(directory);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getAllTerms()
	 */
	@Override
	public List<String> getAllTerms() throws IOException {

		List<String> termList = new ArrayList<String>();

		// Iterador sobre los contenidos de los documentos
		TermsEnum terms = MultiFields.getFields(this.index).terms("content").iterator();

		// Incluimos los terminos en la lista de terminos
		while (terms.next() != null)
			termList.add(terms.term().utf8ToString());

		return termList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getTotalFreq(java.lang.String)
	 */
	@Override
	public long getTotalFreq(String word) throws IOException {

		Term term = new Term("content", word);

		return index.totalTermFreq(term);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getTermFreq(java.lang.String, int)
	 */
	@Override
	public long getTermFreq(String word, int docID) throws IOException {

		return this.getDocVector(docID).getFreq(word);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getDocVector(int)
	 */
	@Override
	public FreqVector getDocVector(int docID) throws IOException {

		// Obtenemos terminos del documento
		Terms terms = index.getTermVector(docID, "content");

		return new LuceneFreqVector(terms);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getDocPath(int)
	 */
	@Override
	public String getDocPath(int docID) throws IOException {

		// Devuelve el campo path del documento
		return index.document(docID).get("path");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getDocFreq(java.lang.String)
	 */
	@Override
	public int getDocFreq(String word) throws IOException {

		Term term = new Term("content", word);

		return index.docFreq(term);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#close()
	 */
	@Override
	public void close() throws IOException {
		index.close();
		index = null;
	}

	/**
	 * @return Devuelve el index Reader
	 */
	public IndexReader getIndex() {
		return index;
	}

}
