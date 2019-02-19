package es.uam.eps.bmi.search.ui;

import java.awt.Frame;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;

public class MainFrame extends Frame {
	
	@SuppressWarnings("unused")
	private final static String directoryCollection = "docs1k";
	
	@SuppressWarnings("unused")
	private final static String zipCollection = "docs1k.zip";
	
	@SuppressWarnings("unused")
	private final static String urlCollection = "urls.txt";
	
	private final static String indexPath = "res/index";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainFrame() {
		
	}

	public static void main (String a[]) throws IOException, ParseException {
        // Algunos valores globales
		// Construccion del indice
        LuceneBuilder  builder = new LuceneBuilder();
        builder.build(directoryCollection, indexPath);
        
        // Apertura del indice
        LuceneIndex index = new LuceneIndex(indexPath);
        

        // 1. Añadir documentos al índice
        for (String url : urls) {
            Document doc = new Document();
            doc.add(new TextField("path", url, Field.Store.YES));
            FieldType type = new FieldType();
            type.setIndexOptions (IndexOptions.DOCS_AND_FREQS);
            type.setStoreTermVectors (true);
            String text = Jsoup.parse(new URL(url), 10000).text();
            doc.add(new Field("content", text, type));
            builder.addDocument(doc);
        }
        builder.close();
        
        // 2. Buscar en el índice
        IndexReader index = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(index);
        QueryParser parser = new QueryParser("content", analyzer);
        Query q = parser.parse(query);
        // Se puede cambiar el modelo IR del buscador con setSimilarity(Similarity)
        ScoreDoc result[] = searcher.search(q, cutoff).scoreDocs;
        System.out.println("Result for query \"" + query + "\"");
        for (ScoreDoc d : result) 
            System.out.println(d.score + "\t" + index.document(d.doc).get("path"));

        // 3. Inspeccionar el índice
        for (int docID = 0; docID < index.numDocs(); docID++) {
            Terms docVector = index.getTermVector(docID, "content");
            TermFreq terms[] = new TermFreq[(int) docVector.size()];
            TermsEnum iter = docVector.iterator();
            iter.next();
            for (int i = 0; i < terms.length; i++) {
                String termString = iter.term().utf8ToString();
                terms[i] = new TermFreq(termString, iter.totalTermFreq(), index.docFreq(new Term("content", termString)));
                iter.next();
            }
            Arrays.sort(terms, new Comparator<TermFreq>() {
                public int compare(TermFreq t1, TermFreq t2) {
                    return (int) (t2.tf - t1.tf);
                }
            });
            System.out.println("\n---------\n" + index.document(docID).get("path") + "\n---------");
            int nTokens = 0;
            for (int i = 0; i < topTerms; i++) 
                System.out.println(terms[i].term + "\t" + terms[i].tf + "\t" + terms[i].df);
            for (TermFreq term : terms) nTokens += term.tf;
            System.out.println("\nNr terms: " + terms.length);
            System.out.println("Nr tokens: " + nTokens);
        }
        
        index.close();
        directory.close();
    }
}