package es.uam.eps.bmi.search.index.structure.positional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.uam.eps.bmi.search.index.structure.EditablePostingsList;

public class PositionalPostingsList extends EditablePostingsList {

    public PositionalPostingsList() {
        super();
    }
    
    public PositionalPostingsList(int docID) {
        super(docID);
    }
    
    @Override
    public void add(int docID, long freq) {
    	add(new PositionalPosting(docID, freq, new ArrayList<Integer> ()));
    }
    
    // docIDs are supposed to be added by increasing docID
    public void add(int docID, long freq, List<Integer> pos) {
        add(new PositionalPosting(docID, freq, pos));
    }
    
    @Override
    public void add(int docID) {
        if (!postings.isEmpty() && docID == postings.get(postings.size() - 1).getDocID())
        postings.get(postings.size() - 1).add1();
        else add(docID, 1);
    }
}
