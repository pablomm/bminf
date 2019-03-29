package es.uam.eps.bmi.search.index.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.Index;

/**
 *
 * @author pablo
 */
public class SerializedRAMIndexBuilder extends BaseIndexBuilder {
    public void save(String indexPath) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(indexPath + "/" + Config.INDEX_FILE));
        out.writeObject(dictionary);
        out.close();
    }

    protected Index getCoreIndex() {
        return new SerializedRAMIndex(dictionary, nDocs);
    }
}
