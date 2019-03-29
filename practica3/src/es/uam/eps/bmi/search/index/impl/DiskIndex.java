package es.uam.eps.bmi.search.index.impl;

import java.io.IOException;

import es.uam.eps.bmi.search.index.structure.Dictionary;
import es.uam.eps.bmi.search.index.structure.impl.DiskHashDictionary;

/**
 *
 * @author pablo
 */
public class DiskIndex extends BaseIndex {
    public DiskIndex(String indexFolder) throws IOException {
        super(indexFolder);
        dictionary = new DiskHashDictionary(indexFolder);
        ((DiskHashDictionary) dictionary).load();
    }

    public DiskIndex(Dictionary dic, int nDocs) {
        super(dic, nDocs);
    }
}
