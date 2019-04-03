package es.uam.eps.bmi.recsys.data;

import java.util.Set;

/**
 *
 * @author pablo
 * 
 * En principio características de ítems, pero también podrían ser de usuario.
 */
public interface Features<F> {
    public Set<F> getFeatures(int id);
    public Double getFeature(int id, F feature);
    public void setFeature(int id, F feature, double value);
    public Set<Integer> getIDs();
}
