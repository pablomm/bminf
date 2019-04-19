package es.uam.eps.bmi.recsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Miguel Laseca y Pablo Marcos
 *
 * @param <T> Tipo de las Features
 */
public class FeaturesImpl<T> implements Features<T> {
	
	/**
	 * Hash Map indexado por id (en principio de item) con Features y Valores
	 */
	Map<Integer, HashMap<T,Double>> values=new HashMap<Integer,HashMap<T,Double>> ();
	
	/**
	 * Inicializa la estructura vacia
	 */
	public FeaturesImpl() {
		// Empty constructor
	}
	
	/**
	 * Constructor para cargar desde un fichero las features
	 * @param path Ruta al fichero
	 * @param delimiter Delimitador
	 * @param parser Parseador de las Features
	 * @throws FileNotFoundException
	 */
	public FeaturesImpl(String path, String delimiter, Parser<T> parser) throws FileNotFoundException {
		
		Scanner scanner = new Scanner (new File (path));
		
		// Iteramos sobre las lineas del fichero
		while (scanner.hasNextLine()) {
			
			String[] line = scanner.nextLine().split(delimiter);
			
			try {
			// Obtenemos el id, Feature y Valor
			int item = Integer.parseInt(line[0]);
			T feature = parser.parse(line[1]);
			Double value = Double.parseDouble(line[2]);
			
			// Incluimos entrada si el id del item es nuevo
			values.putIfAbsent(item, new HashMap<T,Double>());

			// Incluimos la feature
			values.get(item).put(feature,value);
			} catch (NumberFormatException e) {
				// Try catch para evitar cabeceras del fichero csv
			}
		}
		scanner.close();
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Features#getFeatures(int)
	 */
	@Override
	public Set<T> getFeatures(int id) {
		
		HashMap<T, Double> features = values.get(id);
		
		if (features == null) {
			return new HashSet<T>();
		}
		return features.keySet();
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Features#getFeature(int, java.lang.Object)
	 */
	@Override
	public Double getFeature(int id, T feature) {
		
		HashMap<T, Double> features = values.get(id);
		if( features == null) {
			return null;
		}
		
		return features.get(feature);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Features#setFeature(int, java.lang.Object, double)
	 */
	@Override
	public void setFeature(int id, T feature, double value) {
		
		// Incluimos entrada si el id del item es nuevo
		values.putIfAbsent(id, new HashMap<T,Double>());
		
		// Incluimos la feature
		values.get(id).put(feature,value);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Features#getIDs()
	 */
	@Override
	public Set<Integer> getIDs() {
		return values.keySet();
	}

}
