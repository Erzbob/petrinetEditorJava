package petrinet_model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repraesentiert ein moegliches Model einer Transition im Petrinetz. <br><br>
 * 
 * Ein Transition-Objekt kennt die Stellen die direkt vor ihm (Vorbereich) und direkt nach ihm (Nachbereich) liegen,
 * sowie die Kanten von denen es die Quelle und das Ziel ist.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class Transition implements ITransition {
    
    private String id, name;
    private Point position;
    private boolean activated; // gibt an ob die Transition geschaltet werden kann oder nicht
    private List<IPlace> preArea = new ArrayList<IPlace>(); // alle Stellen im Vorbereich
    private List<IPlace> postArea = new ArrayList<IPlace>(); // alle Stellen im Nachbereich
    private List<IPArc> arcsFromPreArea, arcsToPostArea; // alle Kanten von denen diese Transition die Quelle oder das Ziel ist
 
    /**
     * Erzeugt und initialisiert ein neues Transition-Objekts mit der uebergebenen ID.
     * 
     * @param id
     *          ID von dem neuen Transition-Objekt.
     */
    public Transition(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }

    /**
     * Hinzufuegen einer Kante zur Menge der Kanten von denen diese Transition das Ziel ist
     * 
     * @param arc
     *          die Kante die hinzugefuegt wird.
     */
    public void addArcFromPreArea(IPArc arc) {
        if(((PArc) arc).getTransitionAsTarget() == this) {
            if(arcsFromPreArea == null) {
                arcsFromPreArea = new ArrayList<IPArc>();
            }
            arcsFromPreArea.add(arc);
        }
    }
    
    /**
     * Hinzufuegen einer Kante zur Menge der Kanten von denen diese Transition die Quelle ist
     * 
     * @param arc
     *          die Kante die hinzugefuegt wird.
     */
    public void addArcToPostArea(IPArc arc) {
        if(((PArc) arc).getTransitionAsSource() == this) {
            if(arcsToPostArea == null) {
                arcsToPostArea = new ArrayList<IPArc>();
            }
            arcsToPostArea.add(arc);
        }
    }
    
    @Override
    public List<IPlace> getPreArea() {
        return preArea;
    }
    
    @Override
    public List<IPlace> getPostArea() {
        return postArea;
    }
    
    @Override
    public void setPosition(int xPos, int yPos) {
        position = new Point(xPos, yPos);
    }

    @Override
    public Point getPosition() {
        return position;
    }
    
    @Override
    public void setName(String elementName) {
        name = elementName;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean isActivated() {
        return activated;
    }
    
    /**
     * Aktiviert dies Transition, sofern die Bedingungen bezueglich 
     * der Anzahl der Marken der Stellen im Vorbereich erfuellt sind.
     */
    @Override
    public void setActivated() {
        // Falls keine stelle im Vorbereich ist, ist die Transition automatisch aktiviert
        if(preArea.size() == 0) {
            activated = true;
            return;
        } 
        // Falls der Vorbereich Stellen enthaelt, muessen alle Stellen im Vorbereich eine Anzahl 
        // von Marken groesser null haben damit diese Transition aktiviert werden kann.
        else {
            for(IPlace place : preArea) {
                if(place.getMarks() == 0) {
                    activated = false;
                    return;
                }
            }
            // Die Bedingung das alle Stellen im Vorbereich eine Anzahl 
            // von Marken groesser null haben, ist hier erfuellt.
            activated = true;
        }
    }
    
    /**
     * Hinzufuegen einer Stelle zum Vorbereich von dieser Transition.
     * 
     * @param place
     *          Stelle aus dem Vorbereich dieser Transition.
     */
    public void addPlaceToPreArea(IPlace place) {
        preArea.add(place);
    }
    
    /**
     * Hinzufuegen einer Stelle zum Nachbereich dieser Transition.
     * 
     * @param place
     *          Stelle aus dem Nachbereich dieser Transition.
     */
    public void addPlaceToPostArea(IPlace place) {
        postArea.add(place);
    }
    
    /**
     * Schaltet diese Transition wenn sie aktiviert ist. 
     * Erhoeht die Anzahl der Marken aller Stellen im Nachbereich um Eins.
     * Verringert die Anzahl der Marken aller Stellen im Vorbereich um Eins.
     */
    @Override
    public void switchTransition() {
        if(this.isActivated()) {
            this.incrementMarkingOfPlacesInPostArea();
            this.decrementMarkingOfPlacesInPreArea();
        } else {
            System.out.println("Methode switchTransition, Class Transition: Die Transition " + id + " ist nicht aktiviert.");
        }
    }
     
    /**
     * Verringert die Anzahl der Marken aller Stellen im Vorbereich um Eins.
     */
    private void decrementMarkingOfPlacesInPreArea() {
        for(IPlace place : preArea) {
            place.decrementMarks();
        }
    }
    
    /**
     * Erhoeht die Anzahl der Marken aller Stellen im Nachbereich um Eins.
     */
    private void incrementMarkingOfPlacesInPostArea() {
        for(IPlace place : postArea) {
            place.incrementMarks();
        }
    }
}