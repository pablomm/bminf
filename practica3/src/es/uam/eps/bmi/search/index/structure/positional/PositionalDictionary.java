package es.uam.eps.bmi.search.index.structure.positional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map;

import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.Dictionary;
import es.uam.eps.bmi.search.index.structure.EditableDictionary;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.DiskHashDictionary;

public class PositionalDictionary implements Dictionary {

	Map<String, List<Integer>> positionPostings;
	Map<String,Long> termPostings;
    String indexFolder;

    public PositionalDictionary(String path) {
        indexFolder = path;
        termPostings = new TreeMap<String,Long>();
        positionPostings = new TreeMap<String, List<Integer>>();
	}

	@Override
	public PostingsList getPostings(String term) throws IOException {
        PositionalPostingsList postings = new PositionalPostingsList();
        if (!termPostings.containsKey(term)) return postings;
        RandomAccessFile postingsFile = new RandomAccessFile(indexFolder + "/" + Config.POSTINGS_FILE, "r");
        postingsFile.seek(termPostings.get(term));
        int length = postingsFile.readInt();
        while (length-- > 0) {
        	ArrayList<Integer> l = new ArrayList<Integer> ();
        	int docID=postingsFile.readInt();
        	long freq=postingsFile.readLong();
        	// After the frequency the file contains the offsets of every occurrence found
        	long freq_aux=freq;
        	while (freq_aux-- > 0)
        		l.add(postingsFile.readInt());
        	
            postings.add(docID, freq, l);
        }
        postingsFile.close();
        return postings;
    }
	
	public void add(String term, long address) {
        termPostings.put(term, address);
    }
    
    public Collection<String> getAllTerms() {
        return termPostings.keySet();
    }
	
	public void load() throws IOException {
        File f = new File(indexFolder + "/" + Config.DICTIONARY_FILE);
        if (!f.exists()) throw new NoIndexException(indexFolder);
        Scanner scn = new Scanner(new FileInputStream(f));
        while(scn.hasNext()) {
            String s[] = scn.nextLine().split("\t");
            termPostings.put(s[0], new Long(s[1]));
            
            ArrayList<Integer> l = new ArrayList<Integer> ();
            for (int i=2; i<s.length; i++)
            	l.add(new Integer(s[i]));
            positionPostings.put(s[0], l);
        }
        scn.close();
    }
	
	public void save() throws FileNotFoundException  {
        PrintStream dictFile = new PrintStream(indexFolder + "/" + Config.DICTIONARY_FILE);
        for (String term : termPostings.keySet()) {
            dictFile.print(term + "\t" + termPostings.get(term));
            
            for (int i : positionPostings.get(term))
            	dictFile.print("\t" + i);
            dictFile.println();
        }
        dictFile.close();
    }
	
	@Override
    public long getDocFreq(String term) throws IOException {
        if (!termPostings.containsKey(term)) return 0;
        RandomAccessFile postingsFile = new RandomAccessFile(indexFolder + "/" + Config.POSTINGS_FILE, "r");
        postingsFile.seek(termPostings.get(term));
        return postingsFile.readInt();
    }
}
