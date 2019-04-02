package es.uam.eps.bmi.search.test;

import java.io.IOException;

import es.uam.eps.bmi.search.index.IndexBuilder;
import es.uam.eps.bmi.search.index.WebCrawler;
import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;

public class Crawling {

	public static void main (String a[]) throws IOException {
		
		String seedFile = "collections/urls.txt";
		String indexPath = "crawler/urls";
		
		
		IndexBuilder builder = new LuceneBuilder();
		WebCrawler crawler = new WebCrawler(builder, 100, 10);
		
		long startTime = System.nanoTime();
		crawler.build(seedFile, indexPath);
		long endTime = System.nanoTime();
		
		System.out.println("Se han indexado " + crawler.getNumDocs() + " documentos en "
				+ (endTime-startTime)/1000000 + " milisegundos.");
	}

}
