package es.uam.eps.bmi.search.test;

import java.io.IOException;
import java.util.List;



import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;

public class TermStats {
	
	public final static String freqFile = "termfreq.txt";
	
	public final static String docFreqFile = "termdocfreq.txt";
	
	public final static String collectionFile = "docs1k.zip";
	
	public final static String indexPath = "res/index";
	

	public static void main(String[] args) throws IOException {
		
		
		Index index = new LuceneIndex(indexPath);
		
		List<String> words = index.getAllTerms();
		
		for (String word : words) {
			
			// A medio hacer,
			
			System.out.println(word);
			System.out.println(index.getDocFreq(word));
			System.out.println(index.getTotalFreq(word));
		}

	}

}
