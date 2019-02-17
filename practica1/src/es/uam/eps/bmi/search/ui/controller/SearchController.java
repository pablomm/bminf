package es.uam.eps.bmi.search.ui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

import es.uam.eps.bmi.search.ui.view.SearchPanel;

public class SearchController implements ActionListener {

	/**
	 * Panel of the controller
	 */
	private SearchPanel panel;

	/**
	 * Panel of the controller
	 */
	private AbstractEngine engine;

	/**
	 * table associated
	 */
	private JTable table;

	/**
	 * header of the table
	 */
	private final String itemHeader[] = new String[] { "Posicion", "Puntuacion", "DocID", "Ruta" };

	public SearchController(SearchPanel searchPanel, AbstractEngine engine) {
		this.panel = searchPanel;
		this.table = searchPanel.getTable();
		this.engine = engine;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		DefaultTableModel dtm = new DefaultTableModel();
		int cutoff = 0;

		try {
			cutoff = Integer.valueOf(panel.getCutoff());
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(panel, e1.toString());
		}

		dtm.setColumnIdentifiers(itemHeader);

		SearchRanking ranking;
		String query = panel.getQuery();

		try {
			ranking = engine.search(query, cutoff);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		int i = 1;

		for (SearchRankingDoc doc : ranking) {

			Vector<Object> data = new Vector<Object>();
			data.add(i++);
			data.add(doc.getScore());
			data.add(doc.getDocID());
			try {
				data.add(doc.getPath());
			} catch (IOException e1) {
				data.add("");
			}

			dtm.addRow(data);

		}

		table.setModel(dtm);

	}

}