package es.uam.eps.bmi.search.ui.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import es.uam.eps.bmi.search.ui.controller.BuilderController;

public class BuilderPanel extends JPanel {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -307524897592695421L;

	private JTextField indexPath = new JTextField(40);

	private JTextField collectionPath = new JTextField(40);

	private JTextField modulesPath = new JTextField(40);

	private static final String ENGINES[] = { "LuceneEngine", "VSMEngine" };

	private JComboBox<String> engine = new JComboBox<String>(ENGINES);

	public BuilderPanel(BuilderWindow window) {

		this.setBounds(0, 0, 400, 500);
		this.setLayout(null);

		JLabel collectionlabel = new JLabel("Coleccion de documentos");
		collectionlabel.setBounds(80, 70, 240, 25);
		this.add(collectionlabel);

		// Adding user text field
		collectionPath.setBounds(80, 100, 240, 25);
		collectionPath.addActionListener(new BuilderController(window, this));
		this.add(collectionPath);

		// Adding the user label
		JLabel indexlabel = new JLabel("Ruta del indice");
		indexlabel.setBounds(80, 130, 240, 25);
		this.add(indexlabel);

		// Adding user text field
		indexPath.setBounds(80, 160, 240, 25);
		indexPath.setText("res/index");
		indexPath.addActionListener(new BuilderController(window, this));
		this.add(indexPath);

		// Adding password label
		JLabel modulesLabel = new JLabel("Fichero con modulos");
		modulesLabel.setBounds(80, 190, 240, 25);
		this.add(modulesLabel);

		// Adding password field
		modulesPath.setBounds(80, 220, 240, 25);
		modulesPath.setText("modules.txt");
		modulesPath.addActionListener(new BuilderController(window, this));
		this.add(modulesPath);

		// Adding password label
		JLabel engineLabel = new JLabel("Engine");
		engineLabel.setBounds(80, 250, 240, 25);
		this.add(engineLabel);

		// Adding Combo box
		engine.setBounds(80, 280, 240, 25);
		engine.setLightWeightPopupEnabled(false);
		this.add(engine);

		// Adding the login button
		JButton confirmar = new JButton("Crear indice");
		confirmar.addActionListener(new BuilderController(window, this));
		confirmar.setBounds(110, 350, 190, 30);
		this.add(confirmar);

	}

	public String getIndexPath() {
		return this.indexPath.getText();
	}

	public String getCollectionPath() {
		return this.collectionPath.getText();
	}

	public String getEngine() {
		return this.engine.getSelectedItem().toString();
	}

	public String getModulesPath() {
		return String.valueOf(this.modulesPath.getText());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		GradientPaint gp = new GradientPaint(0, 0, Color.ORANGE, 0, getHeight(), Color.GRAY);
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

}
