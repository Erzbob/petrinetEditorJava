package petrinet_view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

/**
 * Diese Klasse beschreibt ein Panel, welches dazu dient, ein ViewPanel aufzunehmen, 
 * welches wiederum einen Petrinetz-Graph oder einen Erreichbarkeits-Graph aufnimmt, 
 * der auf dem Bildschirm angezeigt wird.
 * 
 * @author Richard Herzog, 3208133
 *
 */
/**
 * @author Vogt (Benutzer)
 *
 */
public class View_GraphPanel extends JPanel{

    /**
     * der Graph der im ViewPanel angezeigt wird
     */
    private View_PetrinetGraph petrinetGraph;
    
    /**
     * der Graph der im ViewPanel angezeigt wird
     */
    private View_ReachabilityGraph reachabilityGraph;
    
    /**
     * im ViewPanel wird der Graph angezeigt. ViewPanel erweitert JPanel.
     */
    private ViewPanel viewPanel;
    
    /**
     * Bei der ViewerPipe koennen Listener fuer, in einem eigenen Thread laufende Viewer 
     * angemeldet werden, um auf Ereignisse die bei den Viewern auftreten reagieren zu koennen. 
     * Dies ist bei diesem SwingViewer der Fall, da er in einem eigenen Thread laeuft.
     */
    private ViewerPipe viewerPipe;
    /**
     * 
     */
    private SwingViewer viewer;
    
    /**
     * Ueberschrift fuer die Art des Graphen
     */
    private TitledBorder title;
    
    /**
     * Status den das auto-Layout des Graphen der in diesem Panenl eingebettet ist aktuell hat
     */
    private boolean autoLayoutStatus = false;
    
    // Falls das Layout von einem Graph der in einem Panel dieser Klasse eingebettet ist das auto-Layout von Graph-Stream
    // ist, so wird dieses Flag genau dann auf 1 gesetzt, wenn dieses auto-Layout über die in dieser Klasse
    // deklarierte Methode disableAutoLayout() deaktiviert wird. Das Flag wird erneut auf 0 gesetzt,
    // genau dann wenn die Methode setLayoutFlag() aus dieser Klasse aufgerufen wird.
    private static int layoutFlag = 0;
    
    /**
     * Erzeugen und initialisieren eines View_GraphPanel-Objekts,
     * welches keine Komponenten enthaelt.
     */
    public View_GraphPanel() { 
    }
    
    /**
     * Erzeugen initialisieren und sichtbar machen eines View_GraphPanel-Objekts,
     * welches ein {@link ViewPanel}-Objekt erhaelt, welches wiederum den
     * Petrinetz-Graph enthaelt, der diesem Konstruktor als Parameter uebergeben wird.
     * 
     * @param petrinetGraph
     *          der Petrinetz-Graph des neuen View_GraphPanel-Objekts.
     */
    public View_GraphPanel(View_PetrinetGraph petrinetGraph) {
        this.petrinetGraph = petrinetGraph;
        
        // Layout dieses JPanels setzen
        this.setLayout(new BorderLayout());

        // Erzeuge einen Viewer mit passendem Threading-Model fuer Zusammenspiel mit Swing
        viewer = new SwingViewer(this.petrinetGraph,
                Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        
        

        // bessere Darstellungsqualitaet und Antialiasing (Kantenglaettung) aktivieren
        // HINWEIS: Damit diese Attribute eine Auswirkung haben, muessen sie NACH 
        // Erzeugung des SwingViewer gesetzt werden
        this.petrinetGraph.setAttribute("ui.quality");
        this.petrinetGraph.setAttribute("ui.antialias");

        // Auto-Layout aktivieren: Die explizit hinzugefuegten Koordinaten werden nicht genutzt.
        // das Layout wird automatisch und moeglichst uerbersichtlich generiert.
        viewer.disableAutoLayout();

        // Das viewPanel wird nicht in einem eigenen Fenster angezeigt.
        // die zurueckgegebene Referenz ist ein JPanel.
        viewPanel = (ViewPanel) viewer.addDefaultView(false);

        // Neue ViewerPipe erzeugen, um ueber Ereignisse des Viewer informiert
        // werden zu koennen
        viewerPipe = viewer.newViewerPipe();

        // Umrandung mit Titel erzeugen
        title = BorderFactory.createTitledBorder("Petrinetz-Graph");
        this.setBorder(title);
        
        // Einbetten des ViewPanels in dieses JPanel
        this.add(BorderLayout.CENTER, viewPanel);
    }
    
    /**
     * Erzeugen initialisieren und sichtbar machen eines View_GraphPanel-Objekts,
     * welches ein {@link ViewPanel}-Objekt erhaelt, welches wiederum den
     * Erreichbarkeits-Graph enthaelt, der diesem Konstruktor als Parameter uebergeben wird.
     * 
     * @param reachabilityGraph
     *          der Erreichbarkeits-Graph des neuen View_GraphPanel-Objekts.
     */
    public View_GraphPanel(View_ReachabilityGraph reachabilityGraph) {
        this.reachabilityGraph = reachabilityGraph;
        
        // Layout dieses JPanels setzen
        this.setLayout(new BorderLayout());

        // Erzeuge einen Viewer mit passendem Threading-Model fuer Zusammenspiel mit Swing
        viewer = new SwingViewer(this.reachabilityGraph,
                Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        // bessere Darstellungsqualitaet und Antialiasing (Kantenglaettung) aktivieren
        // HINWEIS: Damit diese Attribute eine Auswirkung haben, muessen sie NACH 
        // Erzeugung des SwingViewer gesetzt werden.
        this.reachabilityGraph.setAttribute("ui.quality");
        this.reachabilityGraph.setAttribute("ui.antialias");
        
        // Auto-Layout deaktivieren: Die explizit hinzugefuegten Koordinaten werden genutzt
        // Achtung: Falls keine Koordinaten definiert wurden, liegen alle Knoten uebereinander.
        enableAutoLayout();
        
        // Das viewPanel wird nicht in einem eigenen Fenster angezeigt.
        // die zurueckgegebene Referenz ist ein JPanel.
        viewPanel = (ViewPanel) viewer.addDefaultView(false);
        
        // Neue ViewerPipe erzeugen, um ueber Ereignisse des Viewer informiert
        // werden zu koennen
        viewerPipe = viewer.newViewerPipe();

        // Umrandung mit Titel erzeugen
        title = BorderFactory.createTitledBorder("Erreichbarkeits-Graph");
        this.setBorder(title);
        
        // Einbetten des ViewPanels in dieses JPanel
        this.add(BorderLayout.CENTER, viewPanel);
    }
    
    /**
     * Liefert die ViewerPipe von diesem JPanel.
     * 
     * @return
     *          die ViewerPipe von diesem JPanel.
     */
    public ViewerPipe getViewerPipe() {
        return viewerPipe;
    }
    
    /**
     * Liefert das ViewPanel von diesem JPanel.
     * 
     * @return
     *          das ViewPanel von diesem JPanel.
     */
    public ViewPanel getViewPanel() {
        return viewPanel;
    }
    
    /**
     * Schaltet das AutoLayout von dem Graph aus, der in dieses Panel eingebettet ist.
     */
    public void disableAutoLayout() {
        viewer.disableAutoLayout();
        this.autoLayoutStatus = false;
        View_GraphPanel.layoutFlag = 1;
    }
    
    /**
     * Schaltet das AutoLayout von dem Graph ein, der in dieses Panel eingebettet ist.
     */
    public void enableAutoLayout() {
        viewer.enableAutoLayout();
        this.autoLayoutStatus = true;
    }
    
    /**
     * Zur Abfrage des Status des auto-Layouts des Graphen
     * @return
     *          true falls das auto-Layout fuer den Graph aktiviert ist, false sonst.
     */
    public boolean usesAutoLayout() {
        return this.autoLayoutStatus;
    }
    
    /**
     * Diese Methode liefert ein int, welches als Indikator fuer den auto-Layout-Status aller
     * Graphen, welche in diesem oder einem anderen Panel-Objekt der zugrunde liegenden Klasse 
     * eingebettet ist, interpretiert werden kann, sofern mindestens ein Graph jemals das auto-Layout
     * verwendet hat.
     * 
     * @return
     *          0 standardmaessig, oder falls das auto-Layout einese Graphen eines Objekts dieser Klasse
     *          ueber die dafuer vorgesehene Methode  aktiviert wurde. 1 falls das auto-Layout eines 
     *          Grapen eines Objekts dieser Klasse ueber die dafuer vorgesehene Methode deaktiviert wurde.
     */
    public static int getLayoutFlag() {
        return View_GraphPanel.layoutFlag;
    }
    
    /**
     * Diese Methode setzt das int, welches als Indikator fuer den Globalen auto-Layout-Status aller
     * Graphen, welche in diesem oder einem anderen Panel-Objekt dieser Klasse jemals eingebettet 
     * wurden oder werden, dient.
     */
    public static void setLayoutFlag() {
        View_GraphPanel.layoutFlag = 0;
    }

}