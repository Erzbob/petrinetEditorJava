package petrinet_view;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import petrinet_model.*;

/**
 * Der Graph der ein {@link Model_Petrinet}-Objekt auf dem Bildschirm anzeigt. 
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class View_PetrinetGraph extends MultiGraph{

    // Repraesentiert das stylesheet fuer das Graph-Layout.
    private static String CSS_FILE = "url(" + View_PetrinetGraph.class.getResource("/graph.css") + ")"; // diese Variante der Pfadangabe funktioniert auch aus einem JAR heraus
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForDisabledTransitiont = "disabledTransition";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForEnabledTransition = "enabledTransition";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForHighlightedPlace = "placeHighlight";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForNodeLabel = "nodeLabel";
    
    // der SpriteManager des Graphen
    private SpriteManager spriteMan;
    
    // das Modell auf das sich dieser Graph bezieht 
    private Model_Petrinet petrinet; 
    
    // in dieser Liste werden die Knoten des Graphen nochmals gespeichert, 
    // da die Klasse MulitGraph keine Methode bieted mit der ich ueber 
    // die Menge aller Knoten in diesem Graphen itterieren kann.
    private List<Node> nodes = new ArrayList<Node>(); 
    
    // hier gilt das gleiche wie fuer die List<Node> von Oben.
    private List<Edge> edges = new ArrayList<Edge>();

    /**
     * Erzeugen und initialisieren eines {@link View_PetrinetGraph}-Objekts mit der ID id, 
     * der das Petrinetz petrinet darstellt. 
     * 
     * @param id
     *          die ID von diesem Graph. 
     * @param petrinet
     *          das Petrinetz das dargestellt werden soll.
     */
    public View_PetrinetGraph(String id, Model_Petrinet petrinet) {
        super(id);
        this.petrinet = petrinet;
        // Angabe einer css-Datei fuer das Layout des Graphen
        this.setAttribute("ui.stylesheet", CSS_FILE);
        // einen SpriteManger fuer diesen Graphen erzeugen
        spriteMan = new SpriteManager(this);
        // zur Vergabe von fortlaufenden IDs fuer die Sprites der Elemente.
        // Stellen aus dem Petrinetz-Modell abfragen und Knoten fuer den Graph,
        // anhand der Eigenschaften und Attribute der Stellen, erzeugen.
        for(IPlace place : this.petrinet.getPlaces()) {
            int marks = place.getMarks();
            String iD = place.getID();
            // neuen Knoten erzeugen, initialisieren und zum Graph hinzufuegen
            Node newNode = this.addNode(iD);
            // Anzeige der Marken einer Stelle als >9 wenn die Anzahl der Marken groesser als neun ist.
            if(marks > 9) {
                newNode.setAttribute("ui.label", ">9");
            } 
            // Anzeige der Marken der Stellen als eine Nummer von 1 bis 9 wenn die Anzahl der Marken zwischen eins und neun liegt.
            // Wenn die Anzahl der Marken der Stelle null ist, dann wird nichts angezeigt.
            else if (marks > 0) {
                newNode.setAttribute("ui.label", marks);
            }
            // Position des Knotens festlegen
            newNode.setAttribute("xy", place.getPosition().x, -(place.getPosition().y));
            // Sprite des Knotens festlegen
            Sprite newSprite = spriteMan.addSprite("s" + iD);
            newSprite.attachToNode(iD);
            newSprite.setPosition(0);
            // Beschriftung der Stelle festlegen
            newSprite.setAttribute("ui.label", "["+iD+"] " + place.getName() +" <"+marks+">");
            // Style der Beschriftung festlegen
            newSprite.setAttribute("ui.class", View_PetrinetGraph.stylesheetEntryForNodeLabel);
            nodes.add(newNode);
        }
        // Transitionen aus dem Petrinetz-Modell abfragen und Knoten fuer den Graph,
        // anhand der Eigenschaften und Attribute der Transitionen, erzeugen.
        for(ITransition transition : this.petrinet.getTransitions()) {
            String iD = transition.getID();
            // neuen Knoten erzeugen, initialisieren und zum Graph hinzufuegen
            Node newNode = this.addNode(iD);
            // Position des Knotens festlegen
            newNode.setAttribute("xy", transition.getPosition().x, -(transition.getPosition().y));
            // Sprite des Knotens festlegen
            Sprite newSprite = spriteMan.addSprite("s" + iD);
            newSprite.attachToNode(iD);
            newSprite.setPosition(0);
            // Beschriftung der Transition festlegen
            newSprite.setAttribute("ui.label", "["+iD+"] " + transition.getName());
            // Style der Beschriftung festlegen
            newSprite.setAttribute("ui.class", View_PetrinetGraph.stylesheetEntryForNodeLabel);
            // Transition anhand ihres Aktiviert-Status zeichnen
            if(transition.isActivated()) {
                newNode.setAttribute("ui.class", stylesheetEntryForEnabledTransition);
            } else {
                newNode.setAttribute("ui.class", stylesheetEntryForDisabledTransitiont);
            }
            nodes.add(newNode);
        }
        // Kante aus dem Petrinetz-Modell abfragen und Kante fuer den Graph,
        // anhand der Eigenschaften und Attribute der Kante, erzeugen.
        if(this.petrinet.getArcs() != null) {
            // Quell- und Zielknoten der Kante suchen
            for(IPArc arc : this.petrinet.getArcs()) {
                Node source = null;
                Node target = null;
                // Alle Knoten im Graph als potentielle Quelle oder Ziel pruefen
                for(Node node : nodes) {
                    String iD = node.getId();
                    // die Quelle ist eine Stelle
                    if(((PArc) arc).getPlaceAsSource() != null) {
                        if(((PArc) arc).getPlaceAsSource().getID().equals(iD)) {
                            source = node;
                        }
                    } 
                    // die Quelle ist eine Transition
                    else if(((PArc) arc).getTransitionAsSource() != null) {
                        if(((PArc) arc).getTransitionAsSource().getID().equals(iD)) {
                            source = node;
                        }
                    }
                    // das Ziel ist eine Stelle
                    if(((PArc) arc).getPlaceAsTarget() != null) {
                        if(((PArc) arc).getPlaceAsTarget().getID().equals(iD)) {
                            target = node;
                        }
                    } 
                    // das Ziel ist eine Transition
                    else if(((PArc) arc).getTransitionAsTarget() != null) {
                        if(((PArc) arc).getTransitionAsTarget().getID().equals(iD)) {
                            target = node;
                        }
                    } 
                }
                // neue Kante erzeugen, initialisieren und zum Graph hinzufuegen
                String iD = arc.getID();
                Edge newArc = this.addEdge(iD, source, target, true);
                // Beschriftung fuer diese Kante festlegen
                newArc.setAttribute("ui.label", "["+iD+"]");
                edges.add(newArc);
            }
        }
    }

    /**
     * Fuehrt ein Neuzeichnen des Petrintz-Graph durch.
     * 
     * @param iD
     *            ID des Knotens, der angeklickt wurde
     */
    public void repaintPetrinetGraph(String iD) {
        if(iD.substring(0, 1).equals("p")) {
            togglePlace(iD);
        } else if(iD.substring(0, 1).equals("t")) {
            toggleTransition(iD);
        }
    }
    
    /**
     * Wenn auf eine Stelle im Graph geklickt wird, dann veraendert diese Methode
     * das Erscheinungsbild der Stelle zu "Anzahl der Marken ist veraenderbar" (hervorgehoben) oder
     * "Anzahl der Marken ist nicht veraenderbar" (nicht hervorgehoben) oder beides falls die Stelle
     * auf die nicht geklickt wurde hervorgehoben ist und die Stelle auf die geklickt wurde nicht
     * hervorgehoben ist.
     * 
     * @param iD
     *          Die ID der Stelle auf die geklickt wurde.
     */
    private void togglePlace(String iD) {
        Node node = this.getNode(iD);
        // eine Stelle ist bereits hervorgehoben
        if(petrinet.getHighlightedPlace() != null) {
            // die Stelle auf die geklickt wurde ist hervorgehoben
            if(iD.equals(petrinet.getHighlightedPlace().getID())) {
                // die hervorgehobene Stelle muss zu unhervorgehoben geaendert werden.
                node.removeAttribute("ui.class");
            } 
            // die Stelle auf die geklick wurde ist nicht hervorgehoben
            else {
                // die hervorgehobene Stelle muss zu unhervorgehoben geaendert werden.
                for (Node compare : nodes) {
                    // die Pruefung ob es sich um eine Stelle handelt, muss hier nochmals
                    // gemacht werden, da Stellen und Transitionen beiderseits, einfach Knoten im Graph sind.
                    if (compare.getId().substring(0, 1).equals("p")) {
                        // der Knoten im aktuellen Schleifendurchgang gehoert zu einer Stelle
                        if (compare.hasAttribute("ui.class")) {
                            // der Knoten im aktuellen Schleifendurchgang ist hervorgehoben
                            compare.removeAttribute("ui.class");
                        }
                    }
                }
                // die geklickte Stelle hervorheben
                node.setAttribute("ui.class", stylesheetEntryForHighlightedPlace);
            }
        } 
        // keine Stelle ist hervorgehoben
        else {
            // die geklickte Stelle hervorheben
            node.setAttribute("ui.class", stylesheetEntryForHighlightedPlace);
        }
    }
    
    /**
     * Neuzeichnen von Vor- und Nachbereich der Transition mit der ID id.
     * Neuzeichnen aller Transitionen.
     * 
     * @param iD
     *          die Transition die geklickt wurde.
     */
    private void toggleTransition(String iD) {
        Node node = this.getNode(iD);
        repaintPreArea(node);
        repaintPostArea(node);
        repaintTransitions();
    }

    /**
     * Neuzeichen der Stellen aus dem Nachbereich einer Transition.
     * 
     * @param transition
     *          die Transition, deren Nachbereich neu gezeichnet wird.
     *          
     */
    private void repaintPostArea(Node node) {
        ITransition t = null;
        for(ITransition transition : petrinet.getTransitions()) {
            if(transition.getID().equals(node.getId())) {
                t = transition;
            }
        }
        // ueber den Nachbereich der Transition itterieren
        if(t != null) {
            for(IPlace place : t.getPostArea()) {
                int marks = place.getMarks();
                // Die Knoten anhand der IDs, der Stellen aus dem Nachbereich des Transiton-Model, neu zeichnen.
                for(Node n : nodes) {
                    String iD = n.getId();
                    // Stelle gehoert zum Nachbereich der Transition
                    if(place.getID().equals(iD)) {
                        if(marks > 9) {
                            n.setAttribute("ui.label", ">9");
                        } else if(marks > 0) {
                            n.setAttribute("ui.label", marks);
                        } else {
                            n.removeAttribute("ui.label");
                        }
                        // Beschriftung von dieser Stelle anpassen
                        Sprite s = spriteMan.getSprite("s" + iD);
                        s.setAttribute("ui.label", "["+iD+"] " + place.getName() +" <"+marks+">");
                    }
                }
            }
        }
    }

    /**
     * Neuzeichnen der Stellen aus dem Vorbereich einer Transition.
     * 
     * @param transition
     *          die Transition, deren Vorbereich neu gezeichnet wird.
     */
    private void repaintPreArea(Node node) {
        ITransition t = null;
        for(ITransition transition : petrinet.getTransitions()) {
            if(transition.getID().equals(node.getId())) {
                t = transition;
            }
        }
        // ueber den Vorbereich der Transition itterieren
        if(t != null) {
            for(IPlace place : t.getPreArea()) {
                int marks = place.getMarks();
                // Die Knoten anhand der IDs, der Stellen aus dem Vorbereich des Transiton-Model, neu zeichnen.
                for(Node n : nodes) {
                    String iD = n.getId();
                    // Stelle gehoert zum Vorbereich der Transition
                    if(place.getID().equals(iD)) {
                        if(marks > 9) {
                            n.setAttribute("ui.label", ">9");
                        } else if(marks > 0) {
                            n.setAttribute("ui.label", marks);
                        } else {
                            n.removeAttribute("ui.label");
                        }
                        // Beschriftung von dieser Stelle anpassen
                        Sprite s = spriteMan.getSprite("s" + iD);
                        s.setAttribute("ui.label", "["+iD+"] " + place.getName() +" <"+marks+">");
                    }
                }
            }
        }
    }
    
    /**
     * Hiermit werden die Transitionen anhand ihrer Attribute und 
     * Eigenschaften aus dem Petrinetz-Datenmodell neu gezeichnet.
     */
    public void repaintTransitions() {
        for(ITransition transition : petrinet.getTransitions()) {
            // Neuzeichen der aktivierten Transitionen
            if(transition.isActivated()) {
                for(Node node : nodes) {
                    if(transition.getID().equals(node.getId())) {
                        node.setAttribute("ui.class", View_PetrinetGraph.stylesheetEntryForEnabledTransition);
                    }
                }
            } 
            // Neuzeichnen der nicht aktivierten Transitionen
            else {
                for(Node node : nodes) {
                    if(transition.getID().equals(node.getId())) {
                            node.setAttribute("ui.class", View_PetrinetGraph.stylesheetEntryForDisabledTransitiont);
                    }
                }
            }
        }
    }

    /**
     * Hiermit werden die Stellen anhand der Marken der Stellen aus dem Petrinetz-Datenmodell neu gezeichnet.
     */
    public void repaintPlaces() {
        for(IPlace place : petrinet.getPlaces()) {
            int marks = place.getMarks();
            for(Node node : nodes) {
                String iD = node.getId();
                if(place.getID().equals(iD)) {
                    if(marks > 9) {
                        node.setAttribute("ui.label", ">9");
                    } else if(marks > 0) {
                        node.setAttribute("ui.label", marks);
                    } else {
                        node.removeAttribute("ui.label");
                    }
                    // Beschriftung von dieser Stelle anpassen
                    Sprite s = spriteMan.getSprite("s" + iD);
                    s.setAttribute("ui.label", "["+iD+"] " + place.getName() +" <"+marks+">");
                }
            }
        }
    }
    
    /**
     * uebergibt dem Knoten dessen ID id bezeichnet die Attribute die als Parameter uebergeben wurden.
     * 
     * @param id
     *          ID eines Knotens aus diesem Graph.
     * @param attribute
     *          ein Attribut das uebergeben werden soll.
     * @param values
     *          der Wert der im Attribut zu finden ist.
     */
    public void setNodeAttributeByNodeID(String id, String attribute, Object values) {
        Node node = this.getNode(id);
        node.setAttribute(attribute, values);
    }
}