import java.io.IOException;


import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;

/**
 *
 * @author pablo
 */
public class PruebaLucene {
	
	@SuppressWarnings("unused")
	private final static String directoryCollection = "docs1k";
	
	@SuppressWarnings("unused")
	private final static String zipCollection = "docs1k.zip";
	
	@SuppressWarnings("unused")
	private final static String urlCollection = "urls.txt";
	
	private final static String indexPath = "res/index";

    public static void main (String a[]) throws IOException {

        
    	// Construccion del indice
        LuceneBuilder  builder = new LuceneBuilder();
        builder.build(directoryCollection, indexPath);
        
        // Apertura del indice
        LuceneIndex index = new LuceneIndex(indexPath);
        
        System.out.println(index.getAllTerms());
    }
        
    
}

