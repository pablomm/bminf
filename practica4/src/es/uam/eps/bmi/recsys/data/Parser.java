package es.uam.eps.bmi.recsys.data;

/**
 *
 * @author pablo
 * 
 * Para generalizar la lectura de datos de diferentes tipos en ficheros, por ejemplo 
 * características de ítems que pueden utilizar identificadores enteros o string.
 */
public interface Parser<T> {
    public T parse(String value);
}
