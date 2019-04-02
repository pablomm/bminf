package es.uam.eps.bmi.search.index.structure.positional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pablo Marcos Machon
 * Clase para almacenar postings posicionales editables
 *
 */
public class PositionalPostingEditable extends PositionalPosting {

	/**
	 * UID para la serializacion
	 */
	private static final long serialVersionUID = 6968565939820549272L;
	
	/**
	 * @param id Identificador del documento
	 */
	public PositionalPostingEditable(int id) {
		this(id, 0L, new ArrayList<Integer>());
	}

	public PositionalPostingEditable(int id, long f, List<Integer> pos) {
		super(id, f, pos);
	}
	
	/**
	 * @param position Nueva posicion del posting
	 */
	public void add(int position) {
		this.positions.add(position);
		
		// Aumenta en uno la frecuencia
		this.add1();
	}
}
