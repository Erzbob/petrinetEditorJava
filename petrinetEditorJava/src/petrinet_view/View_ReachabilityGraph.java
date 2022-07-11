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
 * Der Graph der ein {@link Model_ReachabilityGraph}-Objekt auf dem Bildschirm anzeigt. 
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class View_ReachabilityGraph extends MultiGraph{

    // Repraesentiert das stylesheet fuer das Graph-Layout.
    private static String CSS_FILE = "url(" + View_ReachabilityGraph.class.getResource("/graph.css") + ")"; // diese Variante der Pfadangabe funktioniert auch aus einem JAR heraus
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForRGNode = "egNode";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForRGInitialNode = "egInitialNode";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForRGCurrentNode = "egCurrentNode";
   
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForRGNodeM = "egNodeM";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForRGNodeGreaterM = "egNodeGreaterM";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForUnlimitedPath = "unlimitedPath";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForEdgeLabel = "edgeLabel";
    
    /**
     * Repraesentiert einen Eintrag im stylesheet fuer das Graph-Layout.
     */
    public static String stylesheetEntryForUnlimitedPathsEdgeLabels = "edgeLabelUnlimitedPath";
    
    // der SpriteManager des Graphen
    private SpriteManager spriteMan;
    
    // das Modell auf das sich dieser Graph bezieht 
    private Model_Petrinet petrinet;
    
    // in dieser Liste werden die Knoten des Graphen nochmals gespeichert, 
    // da die Klasse MulitGraph keine Methode bieted mit der ich ueber 
    // die Menge aller Knoten in diesem Graphen itterieren kann.
    // wird nur zum Entfernen von Knoten aus diesem Graph benoetigt
    private List<Node> nodes = new ArrayList<Node>(); 
    
    // hier gilt das gleiche wie fuer die List<Node> von Oben.
    private List<Edge> edges = new ArrayList<Edge>();
    
    // Die Menge aller Sprites der Elemente von diesem Graph.
    private List<Sprite> sprites = new ArrayList<Sprite>();
    
    // Referenz auf den Anfangsknoten von diesem Graph.
    private Node initialNode;
    
    // wird bei der Positionsberechnung eins neuen Knotens im pEG verwendet.
    private int nodePositionY = 0;
    
    // wird bei der Positionsberechnung eines neuen Knotens im pEG verwendet
    private int nodePositionX = -100;
    
    // wird fuer die Breite der gezeichneten Knoten im View, 
    // die sich nach der Anzahl der Stellen orientiert, verwendet.
    private int numberOfPlaces;

    
    /**
     * Erzeugen und initialisieren eines {@link View_ReachabilityGraph}-Objekts mit der ID id, 
     * der den Erreichbarkeits-Graph vom Petrinetz petrinet darstellt. 
     * 
     * @param id
     *          die ID von diesem Graph. 
     * @param petrinet
     *          der EG der dargestellt werden soll.
     */          
    public View_ReachabilityGraph(String id, Model_Petrinet petrinet) {
        super(id);
        this.petrinet = petrinet;
        // Anzahl der Stellen aus dem Petrinetz-Model holen
        numberOfPlaces = petrinet.getPlaces().size();
        // Angabe einer css-Datei fuer das Layout des Graphen
        this.setAttribute("ui.stylesheet", CSS_FILE);
        // einen SpriteManger fuer diesen Graphen erzeugen
        spriteMan = new SpriteManager(this);
        // Erzeugen von Knoten
        Node firstNode = this.addNode(this.petrinet.getReachabilityGraphModel().getInitialNode().getID());
        firstNode.setAttribute("ui.label", createNodeLabelFromRGNodesMarking(petrinet.getReachabilityGraphModel().getInitialNode()));
        firstNode.setAttribute("xy", 0, nodePositionY);
        nodePositionY -= 200;
        // die Breite der Knoten im EG-View wird dadurch an die Anzahl der Stellen angepasst
        if(numberOfPlaces > 10) {
            firstNode.setAttribute("ui.class", stylesheetEntryForRGCurrentNode);
        } else {
            firstNode.setAttribute("ui.class", stylesheetEntryForRGCurrentNode + numberOfPlaces);
        }
        this.initialNode = firstNode;
        nodes.add(firstNode);
    }

    /**
     * Loescht alle Knoten aus dem Graph, ausser den Anfangsknoten.
     */
    public void removeNodesExceptInitialNode() {
        List<String> nodeIDs = new ArrayList<String>();
        // die IDs der Knoten aus der Liste mit Knoten holen
        for(Node node : nodes) {
            nodeIDs.add(node.getId());
        }
        // Die Liste mit Knoten leeren
        nodes.clear();
        // Den Anfangsknoten in die Liste mit Knoten einfuegen
        nodes.add(initialNode);
        // Die Knoten aus dem Graph anhand der zuvor geholten IDs loeschen
        for(String iD : nodeIDs) {
            // Anfangsknoten wird nicht geloescht
            if(this.getNode(iD) != initialNode) {
                this.removeNode(iD);
            }
        }
        // y wird nicht null, da der Anfangsknoten schon auf Position null ist.
        nodePositionY = -200;
        nodePositionX = -100;
    }
    
    /**
     * Alle Knoten aus dem Graph loeschen.
     */
    public void removeNodes() {
        List<String> nodeIDs = new ArrayList<String>();
        // die IDs der Knoten aus der Liste mit Knoten holen
        for(Node node : nodes) {
            nodeIDs.add(node.getId());
        }
        // Die Liste mit Knoten leeren
        nodes.clear();
        // Die Knoten aus dem Graph anhand der zuvor geholten IDs loeschen
        for(String iD : nodeIDs) {
            this.removeNode(iD);
        }
        nodePositionY = 0;
        nodePositionX = -100;
        initialNode = null;
    }
    
    /**
     * Den Anfangsknoten hervorheben.
     */
    public void highlightInitialNode() {
        Node node = this.getNode(petrinet.getReachabilityGraphModel().getInitialNode().getID());
        if(numberOfPlaces > 10) {
            node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode);
        } else {
            node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode + numberOfPlaces);
        }
    }
     
    /**
     * Hier werden die Attribute der Knoten anhand des stylesheets angepasst, sodass nach
     * Ausfuehren dieser Methode genau derjenige Knoten hervorgehoben ist, dessen Markierung
     * gerade im Petrinetz-Graph angezeigt wird. <br><br>
     * 
     * Es gibt fuenf verschiedene Attribut-Klassen, die die Knoten farblich unterschiedlich
     * darstellen: aktueller Knoten (gruen), Anfangsknoten (grau), Knoten m (gelb), Knoten m' 
     * (rot), alle anderen Knoten (weiss). <br><br>
     * 
     * Falls die Anzahl der Stellen vom Petrinetz zehn uebersteigt, wird eine andere
     * Darstellungsform fuer die Knoten gewaehlt.
     */
    public void changeToCurrentNode() {
        // Alle Knoten im Graph werden anhand ihrer Merkmale neu gezeichnet
        for (IRGNode rgnode : petrinet.getReachabilityGraphModel().getNodes()) {
            Node node = this.getNode(rgnode.getID());
            // falls die Analyse schon statt fand und das Petrinetz
            // als unbeschraenkt identifiziert wurde, dann muss es auch einen Knoten m im Petrinetz geben.
            if(petrinet.getUnboundedPathMarkingSmaller() != null) {
                // Knoten auf den geklickt wurde ist oder wird zum aktuellen Knoten
                if (node.getId().equals(petrinet.getCurrentMarkingByCurrentPlaceMarkings().toString())) {
                    if(numberOfPlaces > 10) {
                        node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode);
                    } else {
                        node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode + numberOfPlaces);
                    }
                } 
                // der Knoten m wurde nicht geklickt
                else if(node.getId().equals(petrinet.getUnboundedPathMarkingSmaller().toString())){
                    if(numberOfPlaces > 10) {
                        node.setAttribute("ui.class", stylesheetEntryForRGNodeM);
                    } else {
                        node.setAttribute("ui.class", stylesheetEntryForRGNodeM + numberOfPlaces);
                    }
                } 
                // der Knoten m' wurde nicht geklickt
                else if(node.getId().equals(petrinet.getUnboundedPathMarkingGreater().toString())) {
                    if(numberOfPlaces > 10) {
                        node.setAttribute("ui.class", stylesheetEntryForRGNodeGreaterM);
                    } else {
                        node.setAttribute("ui.class", stylesheetEntryForRGNodeGreaterM + numberOfPlaces);
                    }
                } 
                // der Anfangsknoten wurde nicht geklickt
                else if (node.getId().equals(petrinet.getInitialMarking().toString())) {
                    if(numberOfPlaces > 10) {
                        node.setAttribute("ui.class", stylesheetEntryForRGInitialNode);
                    } else {
                        node.setAttribute("ui.class", stylesheetEntryForRGInitialNode + numberOfPlaces);
                    }
                } 
                // alle anderen Knoten ohne bestimmtes Merkmal bleiben weiss
                else {
                    node.setAttribute("ui.label", createNodeLabelFromRGNodesMarking(rgnode));
                    if(numberOfPlaces > 10) {
                        node.setAttribute("ui.class", stylesheetEntryForRGNode);
                    } else {
                        node.setAttribute("ui.class", stylesheetEntryForRGNode + numberOfPlaces);
                    }
                }
            } 
            // Analyse fand noch nicht statt oder das Petrinetz wurde als beschraenkt identifiziert
            else if (node.getId().equals(petrinet.getCurrentMarkingByCurrentPlaceMarkings().toString())) {
                // Knoten auf den geklickt wurde ist oder wird zum aktuellen Knoten
                if(numberOfPlaces > 10) {
                    node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode);
                } else {
                    node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode + numberOfPlaces);
                }
            } 
            // der Anfangsknoten wurde nicht geklickt
            else if (node.getId().equals(petrinet.getInitialMarking().toString())) {
                if(numberOfPlaces > 10) {
                    node.setAttribute("ui.class", stylesheetEntryForRGInitialNode);
                } else {
                    node.setAttribute("ui.class", stylesheetEntryForRGInitialNode + numberOfPlaces);
                } 
            } 
            // alle anderen Knoten ohne bestimmtes Merkmal bleiben weiss
            else {
                node.setAttribute("ui.label", createNodeLabelFromRGNodesMarking(rgnode));
                if(numberOfPlaces > 10) {
                    node.setAttribute("ui.class", stylesheetEntryForRGNode);
                } else {
                    node.setAttribute("ui.class", stylesheetEntryForRGNode + numberOfPlaces);
                }
            }
        }
    }
    
    /**
     * Erstellen und zeichnen der Knoten fuer diesen Graph, anhand der 
     * Eigenschaften und Attribute der Knoten aus dem Datenmodell. <br><br>
     * 
     * Falls die Anzahl der Stellen vom Petrinetz zehn uebersteigt, wird eine andere
     * Darstellungsform fuer die Knoten gewaehlt.
     */
    public void buildNodes() {
        // Anfangsknoten zum Graph hinzufuegen, wenn noch kein Knoten im Graph enthalten ist.
        if(initialNode == null) {
            // ID von diesem Knoten = ID vom Knoten aus dem Datenmodell.
            Node newInitialNode = this.addNode(petrinet.getReachabilityGraphModel().getInitialNode().getID());
            // der Knoten bekommt eine Beschriftung
            newInitialNode.setAttribute("ui.label", 
                    createNodeLabelFromRGNodesMarking(petrinet.getReachabilityGraphModel().getInitialNode()));
            // der Knoten bekommt eine Position
            newInitialNode.setAttribute("xy", nodePositionX, nodePositionY);
            nodePositionX = -nodePositionX;
            nodePositionY -= 200;
            // Festlegen der Dimension des Knotens
            if(numberOfPlaces > 10) {
                newInitialNode.setAttribute("ui.class", stylesheetEntryForRGCurrentNode);
            } else {
                newInitialNode.setAttribute("ui.class", stylesheetEntryForRGCurrentNode + numberOfPlaces);
            }
            this.initialNode = newInitialNode;
            nodes.add(newInitialNode);
            return;
        }
        // Alle Knoten anhand der Eigenschaften und Attribute der Knoten im Datenmodell zeichnen
        for (IRGNode rgnode : petrinet.getReachabilityGraphModel().getNodes()) {
            // Knoten anhand der ID eines Knotens aus dem Datenmodell suchen
            Node node = this.getNode(rgnode.getID());
            // ein Knoten mit dieser ID existiert nicht
            if (node == null) {
                // ID von diesem Knoten = ID vom Knoten aus dem Datenmodell.
                node = this.addNode(rgnode.getID());
                // der Knoten bekommt eine Beschriftung
                node.setAttribute("ui.label", createNodeLabelFromRGNodesMarking(rgnode));
                // der Knoten bekommt eine Position
                node.setAttribute("xy", nodePositionX, nodePositionY);
                nodePositionX = -nodePositionX;
                nodePositionY -= 200;
                // Festlegen der Dimension des Knotens
                if(numberOfPlaces > 10) {
                    node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode);
                } else {
                    node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode + numberOfPlaces);
                }
                nodes.add(node);
            } 
            // Knoten ist der Knoten der zur aktuell angezeigten Markierung im Petrinetz-Graph gehoert
            else if (node.getId().equals(petrinet.getCurrentMarkingByCurrentPlaceMarkings().toString())) {
                if(numberOfPlaces > 10) {
                    node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode);
                } else {
                    node.setAttribute("ui.class", stylesheetEntryForRGCurrentNode + numberOfPlaces);
                }
            } 
            // Knoten ist der Anfangsknoten
            else if (node.getId().equals(petrinet.getInitialMarking().toString())) {
                if(numberOfPlaces > 10) {
                    node.setAttribute("ui.class", stylesheetEntryForRGInitialNode);
                } else {
                    node.setAttribute("ui.class", stylesheetEntryForRGInitialNode + numberOfPlaces);
                }
            } 
            // Knoten ist nicht der Anfangsknoten und nicht der aktuelle Knoten
            else {
                node.setAttribute("ui.label", createNodeLabelFromRGNodesMarking(rgnode));
                if(numberOfPlaces > 10) {
                    node.setAttribute("ui.class", stylesheetEntryForRGNode);
                } else {
                    node.setAttribute("ui.class", stylesheetEntryForRGNode + numberOfPlaces);
                }
            }
        }
    }
    
    /**
     * Erstellen und zeichnen der Kanten fuer diesen Graph, anhand der 
     * Eigenschaften und Attribute der Kanten aus dem Datenmodell.
     */
    public void buildEdges() {
        for (IRGArc rgarc : petrinet.getReachabilityGraphModel().getArcs()) {
            String iD = rgarc.getID();
            // Kante aus diesem Graph anhand seiner ID holen
            Edge edge = this.getEdge(iD);
            // die Kante existiert noch nicht in diesem Graph
            if (edge == null) {
                // Quelle und Ziel dieser Kante abfragen
                Node source = this.getNode(rgarc.getSource().getID());
                Node target = this.getNode(rgarc.getTarget().getID());
                // Kante erstellen, falls ihr Ziel existiert.
                if (target != null) {
                    edge = this.addEdge(iD, source, target, true);
                    edges.add(edge);
                    // Markierung für alle Kanten entfernen, die nicht zum aktuellen Knoten führen
                    // Sprite als Label fuer die Kante erstellen
                    Sprite s = spriteMan.addSprite(iD);
                    s.attachToEdge(iD);
                    s.setPosition(0.5);
                    s.setAttribute("ui.label", rgarc.getLabel());
                    s.setAttribute("ui.class", stylesheetEntryForEdgeLabel);
                    sprites.add(s);
                }
            } 
        }
    }
        
    /**
     * Formatiert das Label eines Knotens anhand der Vorgaben aus der Aufgabenstellung
     * 
     * @param rgnode
     *          der Knoten dessen Label formatiert wird
     * @return
     *          den formatierten Text, der auf dem Label angezeigt wird.
     */
    private String createNodeLabelFromRGNodesMarking(IRGNode rgnode) {
        int position = 1;
        int nodeSize = rgnode.getMarking().size();
        String nodeLabel = "( ";
        for(Integer integer : rgnode.getMarking()) {
            if(position != nodeSize) {
                nodeLabel = nodeLabel.concat(integer + " | ");
            } else {
                nodeLabel = nodeLabel.concat(integer + " )");
            }
            position++;
        }
        return nodeLabel;
    }
    
    /**
     * Loeschen aller Kanten, inklusive deren Sprites, die in diesem Graph vorhanden sind.
     */
    public void removeEdgesAndSprites() {
        List<String> edgeIDs = new ArrayList<String>();
        for(Edge edge : edges) {
            edgeIDs.add(edge.getId());
        }
        // alle Kanten aus der Liste mit Kanten loeschen.
        edges.clear();
        // alle Kanten aus diesem Graph loeschen
        for(String iD : edgeIDs) {
            this.removeEdge(iD);
        }
        List<String> spriteIDs = new ArrayList<String>();
        for(Sprite sprite : sprites) {
            spriteIDs.add(sprite.getId());
        }
        // alle Sprites aus der Liste mit Sprites loeschen
        sprites.clear();
        // alle Sprites aus diesem Graph loeschen
        for(String iD : spriteIDs) {
            spriteMan.removeSprite(iD);
        }
    }
    
    /**
     * Veraendert die Attribute der Kanten, welche durch die Beschraenktheitsanalyse 
     * als Bestandteil eines unbeschraenkten Pfades identifiziert wurden.
     * 
     * @param attribute
     *          Attribut dessen Wert veraendert wird
     * @param values
     *          der Wert, anhand dessen das Attribut geaendert wird.
     */
    public void setAttributesForUnlimitedPathsArcs(String attribute, Object values) {
        // die IDs der Kanten die zum unbeschraenkten Pfad im pEG gehoeren werden in einer Liste gesammelt
        List<String> iDs = new ArrayList<String>();
        for(IRGArc arc : ((Model_ReachabilityGraph) petrinet.getReachabilityGraphModel()).getUnlimitedPathsRGArcs()) {
            iDs.add(arc.getID());
        }
        // anhand der oben gesammelten IDs, werden die uebergebenen Attribute 
        // der Kanten zu denen die IDs gehoeren, anhand der uebergebenen Werte geaendert.
        for(String iD : iDs) {
            Edge edge = this.getEdge(iD);
            edge.setAttribute(attribute, values);
        }
    }
    
    /**
     * Veraendert das Attribut des Knotens, zu dem die ID id gehoert.
     * 
     * @param id
     *          ID eines Knotens aus diesem Graph
     * @param attribute
     *          das Attribut das geaendert wird
     * @param values
     *          der Wert, anhand dessen das Attribut geaendert wird.
     */
    public void setNodeAttributeByNodeID(String id, String attribute, Object values) {
        Node node = this.getNode(id);
        node.setAttribute(attribute, values);
    }
    
    /**
     * Liefert die Anzahl der Knoten in diesem Graph als String.
     * 
     * @return
     *          Anzahl der Knoten des Graphs als String.
     */
    public String getNumberOfPlacesAsString() {
        Integer numberOfPlacesAsInteger = numberOfPlaces;
        return numberOfPlacesAsInteger.toString();
    }
}