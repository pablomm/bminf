package es.uam.eps.bmi.search.index;

import java.io.IOException;

/**
 *
 * @author pablo
 */
public interface IndexBuilder {
	public void build(String collectionPath, String indexPath) throws IOException;
}
