package es.uam.eps.bmi.recsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Miguel Laseca, Pablo Marcos
 */
public class RatingsImpl implements Ratings {
	
	/**
	 * Tabla conteniendo ratings (Usuario, Item) -> Rating
	 */
	Map<Integer, HashMap<Integer,Double>> ratings=
			new HashMap<Integer,HashMap<Integer,Double>> ();
	
	/**
	 * Tabla inversa de ratings (Item, Usuario) -> Rating
	 */ 
	Map<Integer, HashMap<Integer,Double>> inverseRatings=
			new HashMap<Integer,HashMap<Integer,Double>> ();
	
	/**
	 * Numero total de Ratings
	 */
	int nRatings=0;
	
	/**
	 * Constructor para inicializar los ratings vacios
	 */
	public RatingsImpl () {
		
	}

	/**
	 * Constructor para cargar los ratings desde un fichero
	 * La estructura del fichero sera en cada linea Usuario Item Rating
	 * @param path Direccion del fichero
	 * @param delimiter Delimitador entre ratings
	 * @throws FileNotFoundException
	 */
	public RatingsImpl(String path, String delimiter) throws FileNotFoundException {
		
		Scanner scanner = new Scanner (new File (path));
		
		// Recorremos el fichero linea a linea
		while (scanner.hasNextLine()) {
			
			// Separamos la linea por el delimitador
			String[] line = scanner.nextLine().split(delimiter);
			
			// Parseamos el usuario/Item/Rating
			int user = Integer.parseInt(line[0]);
			int item = Integer.parseInt(line[1]);
			Double rating = Double.parseDouble(line[2]);
			
			// Incluimos el rating en la estructura
			rate(user, item, rating);
		}
		scanner.close();
	}
	
	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#rate(int, int, java.lang.Double)
	 */
	@Override
	public void rate(int user, int item, Double rating) {
		
		// Creamos una entrada en item/usuario si no existe anteriormente
		ratings.putIfAbsent(user, new HashMap<Integer,Double>());
		inverseRatings.putIfAbsent(item, new HashMap<Integer,Double>());
		
		// Incluimos el rating
		ratings.get(user).put(item, rating);
		inverseRatings.get(item).put(user, rating);
		
		nRatings++;
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#getRating(int, int)
	 */
	@Override
	public Double getRating(int user, int item) {
		
		HashMap<Integer, Double> items = ratings.get(user);
		
		if (items == null) {
			return null;
		}
		return items.get(item);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#getUsers(int)
	 */
	@Override
	public Set<Integer> getUsers(int item) {
		
		HashMap<Integer, Double> users = inverseRatings.get(item);
		if (users == null) {
			return new HashSet<Integer>();
		}
		return users.keySet();
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#getItems(int)
	 */
	@Override
	public Set<Integer> getItems(int user) {
		
		HashMap<Integer, Double> items = ratings.get(user);
		
		if (items == null) {
			return new HashSet<Integer>();
		}
		return items.keySet();
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#getUsers()
	 */
	@Override
	public Set<Integer> getUsers() {
		return ratings.keySet();
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#getItems()
	 */
	@Override
	public Set<Integer> getItems() {
		return inverseRatings.keySet();
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#nRatings()
	 */
	@Override
	public int nRatings() {
		return nRatings;
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.data.Ratings#randomSplit(double)
	 */
	@Override
	public Ratings[] randomSplit(double ratio) {
		
		// Creamos dos ratings, el primero para train y el segundo para test
		Ratings[] split = { new RatingsImpl(), new RatingsImpl() };
		
		
		// Iteramos sobre todas las entradas
		for (int user : ratings.keySet()) {
			for (int item : ratings.get(user).keySet()) {
				
				// Aleatoriamente elegimos la particion utilizando el ratio
				split[(Math.random()<ratio)? 0:1]
						.rate(user, item, ratings.get(user).get(item));
			}
		}
		
		return split;
	}

}
