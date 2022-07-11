package petrinet_model;

/**
 * Diese Klasse repraesentiert ein moegliches Model einer Kante im Petrinetz. <br><br>
 * Die Quelle der Kante kann entweder vom Typ {@link IPlace} oder vom Typ {@link ITransition} sein.
 * Das Ziel der Kante kann entweder vom Typ {@link IPlace} oder vom Typ {@link ITransition} sein.
 * Daher gibt es in dieser Klasse, fuer die Initialisierung und fuer das Abfragen der Quelle und des Ziels, 
 * jeweils zwei verschiedene Methoden. fuer Initialisierung und Abfragen gibt es somit acht Methoden.
 *
 * @author Richard Herzog, 3208133
 *
 */
public class PArc implements IPArc {

    private String id; // die ID von dieser Kante
    private ITransition transitionAsSource, transitionAsTarget; // Transition entweder als Quelle oder Ziel dieser Kante. Nur eines der Beiden wird mit einem Wert belegt
    private IPlace placeAsSource, placeAsTarget; // Stelle entweder als Quelle oder Ziel dieser Kante. Nur eines der Beiden wird mit einem Wert belegt
    
    /**
     * Initialisiert ein neu erstelltes PArc-Objekt mit der uebergebenen ID.
     * 
     * @param id
     *          die ID der Kante.
     */
    public PArc(String id) {
        this.id = id;
    }
    
    @Override
    public String getID() {
        return id;
    }
    
    /**
     * Die Quelle dieser Kante ist die uebergebene Stelle.
     * 
     * @param placeAsSource
     *          eine Stelle als Quelle dieser Kante.
     */
    public void setPlaceAsSource(IPlace placeAsSource) {
        // Stelle kann nur dann Quelle sein, wenn keine Stelle das Ziel ist und keine Transition die Quelle ist
        if(transitionAsSource == null && placeAsTarget == null) {
            this.placeAsSource = placeAsSource;
        } else {
            System.out.println("Stelle als Quelle festlegen fehlgeschlagen: Das Ziel dieser"
                    + " Kante ist eine Stelle und/oder die Quelle ist eine Transition");
        }
    }
    
    /**
     * Die Quelle dieser Kante ist die uebergebene Transition.
     * 
     * @param transitionAsSource
     *          eine Transition als Quelle dieser Kante.
     */
    public void setTransitionAsSource(ITransition transitionAsSource) {
        // Transition kann nur dann Quelle sein, wenn keine Transition das Ziel ist und keine Stelle die Quelle ist
        if(placeAsSource == null && transitionAsTarget == null) {
            this.transitionAsSource = transitionAsSource;
        } else {
            System.out.println("Stelle als Ziel festlegen fehlgeschlagen: Das Ziel dieser"
                    + " Kante ist eine Transition und/oder die Quelle ist eine Stelle");
        }
    }
    
    /**
     * Das Ziel dieser Kante ist die uebergebene Stelle.
     * 
     * @param placeAsTarget
     *          eine Stelle als Ziel dieser Kante.
     */
    public void setPlaceAsTarget(IPlace placeAsTarget) {
        // Stelle kann nur dann Ziel sein, wenn keine Transition das Ziel ist und keine Stelle die Quelle ist
        if(placeAsSource == null && transitionAsTarget == null) {
            this.placeAsTarget = placeAsTarget;
        } else {
            System.out.println("Stelle als Ziel festlegen fehlgeschlagen: Das Ziel dieser"
                    + " Kante ist eine Transition und/oder die Quelle ist eine Stelle");
        }
    }
    
    /**
     * Das Ziel dieser Kante ist die uebergebene Transition.
     * 
     * @param transitionAsTarget
     *          eine Transition als Ziel dieser Kante.
     */
    public void setTransitionAsTarget(ITransition transitionAsTarget) {
        // Transition kann nur dann Ziel sein, wenn keine Stelle das Ziel ist und keine Transition die Quelle ist
        if(transitionAsSource == null && placeAsTarget == null) {
            this.transitionAsTarget = transitionAsTarget;
        } else {
            System.out.println("Transition als Ziel festlegen fehlgeschlagen: Das Ziel dieser"
                    + " Kante ist eine Stelle und/oder die Quelle ist eine Transition");
        }
    }
    
    /**
     * Gibt eine Stelle zurueck falls die Quelle dieser Kante eine Stelle ist, sonst null.
     * 
     * @return
     *          eine Stelle als Quelle dieser Kante
     */
    public IPlace getPlaceAsSource() {
       return placeAsSource;
    }
    
    /**
     * Gibt eine Transition zurueck falls die Quelle dieser Kante eine Transition ist, sonst null.
     * 
     * @return
     *          eine Transition als Quelle dieser Kante
     */
    public ITransition getTransitionAsSource() {
        return transitionAsSource;
    }
    
    /**
     * Gibt eine Stelle zurueck falls das Ziel dieser Kante eine Stelle ist, sonst null.
     * 
     * @return
     *          eine Stelle als Ziel dieser Kante
     */
    public IPlace getPlaceAsTarget() {
        return placeAsTarget;
    }
    
    /**
     * Gibt eine Transition zurueck falls das Ziel dieser Kante eine Transition ist, sonst null.
     * 
     * @return
     *          eine Transition als Ziel dieser Kante 
     */
    public ITransition getTransitionAsTarget() {
        return transitionAsTarget;
    }
}