package petrinet_model;

/**
 * Dieses Interface enthaelt die Methoden die jedes EG-Kante-Objekt enthalten sollte. 
 * 
 * @author Richard Herzog, 3208133
 *
 */
public interface IRGArc {

    /**
     * Zur Abfrage der Quelle von dieser Kante.
     * 
     * @return
     *          Quelle von dieser Kante.
     */
    IRGNode getSource();

    /**
     * Zur Abfrage des Ziels von dieser Kante.
     * 
     * @return
     *          Ziel von dieser Kante.
     */
    IRGNode getTarget();

    /**
     * Zur Abfrage der Beschriftung von dieser Kante.
     * 
     * @return
     *          Beschriftung von dieser Kante.
     */
    String getLabel();

    /**
     * Zur Abfrage der ID von dieser Kante.
     * 
     * @return
     *          ID von dieser Kante.
     */
    String getID();
}