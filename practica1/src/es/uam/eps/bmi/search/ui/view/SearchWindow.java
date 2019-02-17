package es.uam.eps.bmi.search.ui.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import es.uam.eps.bmi.search.AbstractEngine;

public class SearchWindow extends JFrame {

	/**
	 * Generated serial Version UID
	 */
	private static final long serialVersionUID = 838748157767064427L;

	/**
	 * Title of the frame
	 */
	private static final String TITLE = "Busqueda y mineria de la informacion";

	/**
	 * Width of the frame
	 */
	private static final int WIDTH = 880;

	/**
	 * Height of the frame
	 */
	private static final int HEIGHT = 660;

	/**
	 * Constructor of the class, launch a new frame
	 */
	public SearchWindow(AbstractEngine engine) {

		// Configurating the window
		setTitle(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);

		this.add(new SearchPanel(this, engine), BorderLayout.CENTER);

		// Setting Visible
		setVisible(true);
	}

}
