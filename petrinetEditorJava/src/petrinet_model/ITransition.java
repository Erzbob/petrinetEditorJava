package petrinet_model;

import java.awt.Point;
import java.util.List;

/**
 * Dieses Interface enthaelt die Methoden die jedes Petrinetz-Transition-Objekt enthalten sollte. 
 * 
 * @author Richard Herzog, 3208133
 *
 */
public interface ITransition {

    /**
     * zur Abfrage der ID die dieser Transition bei seiner Erzeugung zugeteilt wurde.
     * 
     * @return
     *          ID von dieser Transition.
     */
    String getID();

    /**
     * Legt die Position dieser Transition fest. Die Position kann bspw. zur 
     * graphischen Praesentation der Transition verwendet werden.
     * 
     * @param xPos
     *          x Position von dieser Transition.
     * @param yPos
     *          y Position von dieser Transition.
     */
    void setPosition(int xPos, int yPos);

    /**
     * Zur Abfrage der Position von dieser Transition.
     * 
     * @return
     *          Position von dieser Transition.
     */
    Point getPosition();

    /**
     * Legt den Namen von dieser Transition fest.
     * 
     * @param elementName
     *          Name von dieser Transition.
     */ 
    void setName(String elementName);

    /**
     * Zur Abfrage des Namens von dieser Transition.
     * 
     * @return
     *          Name von dieser Transition.
     */
    String getName();

    /**
     * Zur Abfrage des Aktiviert-Status von dieser Transition.
     * 
     * @return
     *          true wenn diese Transition aktiviert ist, false sonst.
     */
    boolean isActivated();

    /**
     * Aktiviert diese Transition.
     */
    void setActivated();

    /**
     * Schaltet diese Transition.
     */
    void switchTransition();

    /**
     * Zur Abfrage der Stellen im Vorbereich von dieser Transition.
     * 
     * @return
     *          Stellen aus dem Vorbereich von dieser Transiton.
     */
    List<IPlace> getPreArea();

    /**
     * Zur Abfrage der Stellen im Nachbereich von dieser Transition.
     * 
     * @return
     *          Stellen aus dem Nachbereich von dieser Transition.
     */
    List<IPlace> getPostArea();

}