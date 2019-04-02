package es.uam.eps.bmi.search.index.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import es.uam.eps.bmi.search.index.structure.impl.PositionalHashDictionary;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPostingEditable;

/**
 * @author Pablo Marcos Manchon
 * Constructor del indice posicional, utilizando la implementacion de RAMIndexBuilder
 */
public class PositionalIndexBuilder extends SerializedRAMIndexBuilder {
	
	
	/**
	 * Mapa utilizado para el parseo de documentos
	 */
	private Map<String, PositionalPostingEditable> parseMap;

	@Override
    public void init(String indexPath) throws IOException {
        clear(indexPath);
        this.parseMap = new HashMap<String, PositionalPostingEditable>();
        nDocs = 0;
        dictionary = new PositionalHashDictionary();
        docPaths = new ArrayList<String>();
    }
    
	@Override
    public void indexText(String text, String path) throws IOException {
		
		// Separamos el texto y lo normalizamos
		String terms[] = text.toLowerCase().split("\\P{Alpha}+");
		
		// Creamos postings posicionales e incluimos las posiciones
        for (int i=0; i < terms.length; i++) {
        	PositionalPostingEditable posting = parseMap.get(terms[i]);
        	if (posting == null) {
        		posting = new PositionalPostingEditable(nDocs);
        		parseMap.put(terms[i], posting);
        	}

        	posting.add(i);
        }
        
        // Incluimos los postings del documento en el diccionario de listas de postings
        for(Entry<String, PositionalPostingEditable> entry : parseMap.entrySet()) {
        	((PositionalHashDictionary)this.dictionary).add(entry.getKey(), entry.getValue());
        }
        
        // Actualizamos para la siguiente indexacion
        docPaths.add(path);
        nDocs++;
		parseMap.clear();
    }
	
	
}
