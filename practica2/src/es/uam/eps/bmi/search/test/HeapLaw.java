package es.uam.eps.bmi.search.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import es.uam.eps.bmi.search.index.ForwardIndex;
import es.uam.eps.bmi.search.index.freq.FreqVector;
import es.uam.eps.bmi.search.index.freq.TermFreq;
import es.uam.eps.bmi.search.index.lucene.LuceneForwardIndex;
import es.uam.eps.bmi.search.index.lucene.LuceneForwardIndexBuilder;

public class HeapLaw {

	/**
	 * Vuelca en un fichero los datos correspondientes para comprobar
	 * la ley de heap
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		
		
		 String collPath = "collections/docs100k.zip";
	     String baseIndexPath = "index/heap";
	     
	     String fileOut = "heapLaw100k.txt";
	     
	        
	    // Construccion de Lucene Forward Index
	    new LuceneForwardIndexBuilder().build(collPath, baseIndexPath + "/lucene/forward");
	     
		ForwardIndex index = new LuceneForwardIndex(baseIndexPath + "/lucene/forward");
		
		
		
		PrintWriter writer = new PrintWriter(new File(fileOut));
		
		// Iteramos sobre los documentos
		for(int docID=0; docID< index.numDocs(); docID++) {
			FreqVector vec = index.getDocVector(docID);
			
			int total = 0;
			
			for(TermFreq freq : vec) {
				total += freq.getFreq();
			}
			
			// Volcamos total de apariciones y el #tokens
			writer.println(total + " " + vec.size());
		}
		
		writer.close();
		
		
	}

}
