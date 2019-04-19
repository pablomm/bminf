package es.uam.eps.bmi.sna.structure;

/**
 *
 * @author pablo
 */
public interface Parser<T> {
    public T parse(String value);
}
