package es.uam.eps.bmi.search.ui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.lucene.LuceneEngine;
import es.uam.eps.bmi.search.ui.view.BuilderPanel;
import es.uam.eps.bmi.search.ui.view.BuilderWindow;
import es.uam.eps.bmi.search.ui.view.SearchWindow;
import es.uam.eps.bmi.search.vsm.VSMEngine;

/**
 * Class that contains the controller of the login window Checks the user and
 * the password and throws a new window if it's all right
 * 
 * @author Pablo Marcos Manchon
 * @author Diana Rojas Lopez
 */
public class BuilderController implements ActionListener {

	/**
	 * Panel of the login window
	 */
	private BuilderPanel panel;

	/**
	 * Window that contains the panel
	 */
	private BuilderWindow window;

	/**
	 * Constructor of the class LoginController
	 * 
	 * @param window that contains the panel
	 * @param panel  of the login window
	 */
	public BuilderController(BuilderWindow window, BuilderPanel panel) {
		this.panel = panel;
		this.window = window;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String collectionPath = String.valueOf(panel.getCollectionPath());
		if (collectionPath.isEmpty()) {
			JOptionPane.showMessageDialog(panel, "Introduzca la ruta con la collecion de documentos");
			return;
		}

		String indexPath = String.valueOf(panel.getIndexPath());
		String engineType = String.valueOf(panel.getEngine());

		if (indexPath.isEmpty()) {
			JOptionPane.showMessageDialog(panel, "Introduzca la ruta donde crear el indice");
			return;
		}

		String modulesPath = String.valueOf(panel.getModulesPath());
		if (modulesPath.isEmpty()) {
			modulesPath = null;
		}


		window.setTitle("Construyendo indice...");


		LuceneBuilder builder = new LuceneBuilder();

		try {
			builder.build(collectionPath, indexPath, modulesPath);
			
		} catch (IOException e1) {


			JOptionPane.showMessageDialog(panel, "Error creando el indice: " + e1);
			new BuilderWindow();
			return;
		}

		AbstractEngine engine;

		if (engineType == "LuceneEngine") {
			try {
				engine = new LuceneEngine(indexPath);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(panel, "Error " + e1);
				e1.printStackTrace();
				window.setTitle("Busqueda y mineria de la informacion");
				return;
			}

		} else {
			LuceneIndex index;
			try {
				index = new LuceneIndex(indexPath);
				engine = new VSMEngine(index, modulesPath);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(panel, "Error " + e1);
				e1.printStackTrace();
				window.setTitle("Busqueda y mineria de la informacion");
				return;
			}

		}
		
		window.setVisible(false);
		window.dispose();

		new SearchWindow(engine);

	}

}