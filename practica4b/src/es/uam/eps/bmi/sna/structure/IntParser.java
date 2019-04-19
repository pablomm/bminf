package es.uam.eps.bmi.sna.structure;

/**
 *
 * @author pablo
 */
public class IntParser implements Parser<Integer> {
    public Integer parse(String value) {
        return new Integer(value);
    }
}
