package es.uam.eps.bmi.search.ui.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class BuilderWindow extends JFrame {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4730431597229348258L;

	/**
	 * Title of the window
	 */
	private static final String TITLE = "Busqueda y mineria de la informacion";

	/**
	 * Width of the window
	 */
	private static final int WIDTH = 400;

	/**
	 * Height of the window
	 */
	private static final int HEIGHT = 500;

	/**
	 * Constructor of the class Launches the builder window
	 */
	public BuilderWindow() {

		// creating a window
		this.setTitle(TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		// Opening the window in the center of the screen
		this.setSize(WIDTH, HEIGHT);

		// Setting Layout
		this.getContentPane().setLayout(new BorderLayout());

		// Adding the panel
		this.getContentPane().add(new BuilderPanel(this));

		// Opening in the center
		this.setLocationRelativeTo(null);

		// Setting visible
		this.setVisible(true);
	}
}
