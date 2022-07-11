package petrinet_model;

import java.awt.Point;

/**
 * Dieses Interface enthaelt die Methoden die jedes Petrinetz-Stelle-Objekt enthalten sollte. 
 * 
 * @author Richard Herzog, 3208133
 *
 */
public interface IPlace {

    /**
     * zur Abfrage der ID die dieser Stelle bei seiner Erzeugung zugeteilt wurde.
     * 
     * @return
     *          ID von dieser Stelle.
     */
    String getID();

    /**
     * Legt die Position dieser Stelle fest. Die Position kann bspw. zur 
     * graphischen Praesentation der Stelle verwendet werden.
     * 
     * @param xPos
     *          x Position von dieser Stelle.
     * @param yPos
     *          y Position von dieser Stelle.
     */
    void setPosition(int xPos, int yPos);

    /**
     * Zur Abfrage der Position von dieser Stelle.
     * 
     * @return
     *          Position von dieser Stelle.
     */
    Point getPosition();

    /**
     * Legt den Namen von dieser Stelle fest.
     * 
     * @param name
     *          Name von dieser Stelle.
     */     
    void setName(String name);

    /**
     * Zur Abfrage des Namens von dieser Stelle.
     * 
     * @return
     *          Name von dieser Stelle.
     */
    String getName();

    /**
     * Legt die Anzahl der Marken von dieser Stelle fest.
     * 
     * @param marking
     *          Anzahl der Marken von dieser Stelle.
     */
    void setMarks(int marking);

    /**
     * Zur Abfrage der Anzahl der Marken von dieser Stelle.
     * 
     * @return
     *          Anzahl der Marken von dieser Stelle.
     */
    int getMarks();

    /**
     * Verringert die Anzahl der Marken von dieser Stelle.
     */
    void decrementMarks();

    /**
     * Erhoeht die Anzahl der Marken von dieser Stelle.
     */
    void incrementMarks();


}