package es.uam.eps.bmi.search.ui.view;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.ui.controller.SearchController;

public class SearchPanel extends JPanel {

	/**
	 * Generated serial Version UID
	 */
	private static final long serialVersionUID = -5366197433959530864L;

	/**
	 * Field for the query
	 */
	private JTextField queryText = new JTextField(30);

	/**
	 * Combo box with the options
	 */
	private JTextField cutoff = new JTextField(3);

	/**
	 * Table to show the results, overrided to disable edition
	 */
	private JTable table = new CustomTable();

	/**
	 * Controler of the tables
	 */
	private ActionListener controller;



	public SearchPanel(SearchWindow window, AbstractEngine engine) {

		controller = new SearchController(this, engine);

		this.setLayout(new BorderLayout());

		// Panel with the Query options
		JPanel query = new JPanel();
		query.setLayout(new FlowLayout());

		// Adding text field
		queryText.addActionListener(controller);
		query.add(queryText);

		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());

				if (row >= 0 && evt.getClickCount() == 2) {
					String path = String.valueOf(table.getModel().getValueAt(row, 3));
					try {
						Desktop.getDesktop().open(new File(path));
					} catch (IOException e1) {

						JOptionPane.showMessageDialog(window,e1.toString());
						e1.printStackTrace();
					}

				}
			}
		});

		cutoff.addActionListener(controller);
		cutoff.setText("20");
		query.add(cutoff);

		// Adding search button
		JButton search = new JButton("Buscar");
		search.setSize(10, 30);
		search.addActionListener(controller);
		query.add(search);

		this.add(query, BorderLayout.NORTH);

		// Adding panel with the results
		JScrollPane results = new JScrollPane(table);
		results.setBorder(new EmptyBorder(15, 15, 10, 15));
		this.add(results, BorderLayout.CENTER);

		// Updating table
		controller.actionPerformed(null);

	}

	/**
	 * @return the text of the search
	 */
	public String getQuery() {
		return queryText.getText();
	}

	/**
	 * @return the table with the results
	 */
	public JTable getTable() {
		return table;
	}

	/**
	 * @return the combo box to select the search
	 */
	public String getCutoff() {
		return this.cutoff.getText();
	}

}

class CustomTable extends JTable {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 4121185250502047193L;

	/**
	 * New custom table
	 */
	public CustomTable() {
		super();
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@Override
	public boolean isCellEditable(int i, int i1) {
		return false;
	}

}