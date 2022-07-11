package petrinet_model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repraesentiert ein moegliches Model einer Stelle im Petrinetz. <br><br>
 * 
 * Ein Place-Objekt kennt die Transitionen die direkt vor ihm und direkt nach ihm liegen,
 * sowie die Kanten von denen es die Quelle und das Ziel ist.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class Place implements IPlace {
    private String id, name;
    private Point position; 
    private int marks; // Anzahl der Marken von dieser Stelle
    private List<ITransition> transitionsBefore, transitionsAfter; // Transitionen die direkt vor und nach dieser Stelle kommen.
    private List<IPArc> arcsFromTransitionsBefore, arcsToTransitionsAfter; // Kanten von denen diese Stelle die Quelle oder das Ziel ist.
    
    /**
     * Erzeugt und initialisiert ein neues Place-Objekts mit der uebergebenen ID.
     * 
     * @param id
     *          ID von dem neuen Place-Objekt.
     */
    public Place(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setMarks(int marks) {
        this.marks = marks;
    }

    @Override
    public int getMarks() {
        return marks;
    }

    @Override
    public void decrementMarks() {
        if (marks <= 0) {
            return;
        } else marks --;
    }    
    
    @Override
    public void incrementMarks() {
        marks ++;
    }
    
    /**
     * Fuegt der Menge der Transitionen, die direkt vor dieser Stelle liegen, eine Transition hinzu.
     * 
     * @param transition
     *          die Transition die hinzugefuegt wird.
     */
    public void addTransitionBefore(ITransition transition) {
        if(transitionsBefore == null) {
            transitionsBefore = new ArrayList<ITransition>();
        }
        transitionsBefore.add(transition);
    }
    
    /**
     * Fuegt der Menge der Transitionen, die direkt nach dieser Stelle liegen, eine Transition hinzu.
     * 
     * @param transition
     *          die Transition die hinzugefuegt wird.
     */
    public void addTransitionAfter(ITransition transition) {
        if(transitionsAfter == null) {
            transitionsAfter = new ArrayList<ITransition>();
        }
        transitionsAfter.add(transition);
    }
    
    /**
     * Fuegt der Menge der Kanten, dessen Ziel diese Stelle ist, eine Kante hinzu.
     * 
     * @param arc
     *          die Kante die hinzugefuegt wird.
     */
    public void addArcFromTransitionBefore(IPArc arc) {
        if(((PArc) arc).getPlaceAsTarget() == this) {
            if(arcsFromTransitionsBefore == null) {
                arcsFromTransitionsBefore = new ArrayList<IPArc>();
            }
            arcsFromTransitionsBefore.add(arc);
        }
    }
    
    /**
     * Fuegt der Menge der Kanten, dessen Quelle diese Stelle ist, eine Kante hinzu.
     * 
     * @param arc
     *          die Kante die hinzugefuegt wird.
     */
    public void addArcToTransitionAfter(IPArc arc) {
        if(((PArc) arc).getPlaceAsSource() == this) {
            if(arcsToTransitionsAfter == null) {
                arcsToTransitionsAfter = new ArrayList<IPArc>();
            }
            arcsToTransitionsAfter.add(arc);
        }
    }
        
}
















