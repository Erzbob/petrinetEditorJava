package petrinet_gui;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.TreeSet;

import javax.swing.*;

import petrinet_controller.*;
import petrinet_model.Model_Petrinet;
import petrinet_view.*;

/**
 * Diese Klasse beschreibt das Haupt-Fenster. In ihm werden das Menue, die Toolbar, 
 * die Graphen und die Statusleiste angezeigt.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class PetrinetFrame extends JFrame {
    /**
     * Das Status-Label, das in diesem Fenster angezeigt wird
     */
    private JLabel statusLabel;
    /**
     * Die Menue-Bar, die in diesem Fenster angezeigt wird
     */
    private PetrinetMenuBar menuBar;
    /**
     * Die Tool-Bar, die in diesem Fenster angezeigt wird
     */
    private PetrinetToolBar toolBar;
    /**
     * Das Model auf das sich die angezeigten Elemente beziehen
     */
    private Model_Petrinet petrinet;
    /**
     * Der Petrinetz-Graph, der in diesem Fenster angezeigt wird
     */
    private View_PetrinetGraph petrinetGraph;
    /**
     * Der Erreichbarkeits-Graph, der in diesem Fenster angezeigt wird
     */
    private View_ReachabilityGraph reachabilityGraph;
    /**
     * Das Panel fuer den Petrinetz-Graph
     */
    private View_GraphPanel petrinetGraphPanel;
    /**
     * Das Panel fuer den Erreichbarkeits-Graph
     */
    private View_GraphPanel reachabilityGraphPanel;
    /**
     * Die Textausgabe, die in diesem Fenster angezeigt wird
     */
    private JTextArea textArea;
    /**
     * Das Panel fuer die Panel fuer die Graphen
     */
    private JSplitPane bothGraphPanel;
    /**
     * Das Panel fuer das bothGraphPanel und die textArea
     */
    private JSplitPane allViewsPanel;
    /**
     * Bildschirmgroesse in Pixel
     */
    private Dimension screenSize;
    /**
     * Die Datei von der das Petrinetz-Model seine Daten bezieht.
     */
    private File currentFile;
    /**
     * Das Verzeichnis in dem sich die Datei befindet.
     */
    private File currentDirectory;
    /**
     * Alle .pnml oder .PNML Dateien die sich im Verzeichnis befinden.
     */
    private List<File> fileList;
    
    /**
     * Erzeugen, initialisieren und anzeigen des Fensters auf dem Bildschirm.
     * 
     * @param titel
     *            Titel des Fensters
     */
    public PetrinetFrame(String titel) {
        super(titel);
        
        // Festlegen der Fenstergroesse in Abhaengigkeit der Bildschirmaufloesung
        double heightPerc = 0.8; // relative Hoehe des Fensters bzgl. der Bildschirmhoehe (1.0)
        double aspectRatio = 16.0 / 9.0; // Seitenverhaeltnis des Fensters
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int h = (int) (screenSize.height * heightPerc);
        int w = (int) (h * aspectRatio);
        this.setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);

        // Layout des Fensters setzen
        this.setLayout(new BorderLayout());

        // Menue-Leiste hinzufuegen
        menuBar = new PetrinetMenuBar();
        this.setJMenuBar(menuBar);

        // Werkzeug-Leiste hinzufuegen
        toolBar = new PetrinetToolBar();
        this.add(toolBar, BorderLayout.NORTH);

        // Das Panel mit den beiden Graphen und dem Textbereich initialisieren
        this.initializeAllViewPanel();

        // StatusLabel erzeugen
        this.statusLabel = new JLabel("java.version = " + System.getProperty("java.version") + "  |  user.dir = "
                + System.getProperty("user.dir"));
        
        // Statuslabel am unteren Bildschirmrand anzeigen
        this.add(statusLabel, BorderLayout.SOUTH);
        
        // ganze Anwendung beim Klicken des X in der rechten, oberen Ecke des Fensters schliessen.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Fenster anzeigen
        this.setVisible(true);

        // Den Controller fuer dieses Fenster erzeugen und initialisieren
        new Controller(this, menuBar, toolBar);

        // damit der oeffnen Button beim erzeugen des Fensters automatisch gedrueckt wird
        // und sich somit ein JFileChooser-Dialog oeffnet.
        menuBar.getMenu(0).getItem(0).doClick();
    }
    
    /**
     * Liefert einen String zurueck, der alle System Properties (und ihre aktuellen Werte) 
     * zeilenweise und in alphabetischer Reihenfolge enthaelt.
     *  
     * @return 
     *          String mit allen System Properties in alphabetischer Reihenfolge.
     *          
     * @author ProPra-Team FernUni Hagen
     */
    private static String getAllSystemProperties() {
        TreeSet<String> propSet = new TreeSet<String>();
        for (Object propName : System.getProperties().keySet())
        {
            propSet.add((String)propName + " = " + System.getProperty((String)propName));
        }
        String propertiesString = "";
        for (String prop : propSet)
        {
            propertiesString += prop + "\n";
        }
        return propertiesString;
    }
    
    /**
     * Erzeugen, initialisieren und hinzufuegen vom Panel, welches im Zentrum von diesem Fenster angezeigt wird. 
     */
    private void initializeAllViewPanel() {
        // Panel fuer den Petrinetz-Graph erzeugen
        View_GraphPanel pgPanel = new View_GraphPanel();
        // Panel fuer den EG erzeugen
        View_GraphPanel rgPanel = new View_GraphPanel();
        // Panel fuer die oberen zwei Panel erzeugen.
        bothGraphPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pgPanel, rgPanel);
        this.bothGraphPanel.setDividerLocation(screenSize.width / 2);
        // Bereich fuer die Textausgabe erzeugen
        this.textArea = new JTextArea();
        // Einstellen der Schriftart und Schriftgroesse fuer die TextArea.
        // Die Schriftgroesse wird aus der Bildschirmaufloesung berechnet
        double refactor = Toolkit.getDefaultToolkit().getScreenResolution() / 100.0;
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) (12.0 * refactor)));
//        System.out.println("font-logical-name = " + textArea.getFont().getName());
//        System.out.println("font-face-name    = " + textArea.getFont().getFontName());
        System.out.println("font-family-name  = " + textArea.getFont().getFamily());
        System.out.println("font-size         = " + textArea.getFont().getSize());
        System.out.println("Schriftgroesse der TextArea geaendert um den Faktor " +refactor+ ", an folgender Stelle: Paket: petrinet_gui; Klasse: PetrinetFrame; Methode: initializeAllViewPanel");
        // Systemeigenschaften ausgeben
        String linebreak = "\n";
        textArea.append("******SystemProperties******" + linebreak);
        textArea.append("----------------------------" + linebreak);
        textArea.append(PetrinetFrame.getAllSystemProperties() + linebreak);
        // Panel fuer das Panel welches zwei Panel enthaelt und fuer die Textausgabe.
        this.allViewsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, bothGraphPanel, new JScrollPane(this.textArea));
        allViewsPanel.setOneTouchExpandable(true);
        allViewsPanel.setDividerLocation(screenSize.height / 2);
        // alles Zum Fenster hinzufuegen
        this.add(allViewsPanel, BorderLayout.CENTER);
    }
    
    /**
     * Erzeugen und initialisieren eines Panels, welches die zwei Graphen enthaelt.
     * 
     * @return
     *          ein neu erzeugtes Panel fuer die Graphen.
     */
    public JSplitPane newAndEmptyBothGraphPanel() {
        View_GraphPanel newPetrinetGraphPanel = new View_GraphPanel();
        View_GraphPanel newReachabilityGraphPanel = new View_GraphPanel();
        // Die Graphen werden horizontal getrennt angezeigt
        JSplitPane newBothGraphPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, newPetrinetGraphPanel, newReachabilityGraphPanel);
        newBothGraphPanel.setDividerLocation(screenSize.width / 2);
        return newBothGraphPanel;
    }
    
    /**
     * Loescht das Panel das die beiden Graphen enthaelt.
     */
    public void removeBothGraphPanel() {
        allViewsPanel.remove(bothGraphPanel);
    }
    
    /**
     * Tauscht das Panel das die beiden Graphen enthaelt, mit einem neuen Panel aus.
     * 
     * @param newBothGraphPanel
     *          das Austausch-Panel.
     */
    public void changeBothGraphPanel(JSplitPane newBothGraphPanel) {
        this.bothGraphPanel = newBothGraphPanel;
        allViewsPanel.add(bothGraphPanel, 0);
        allViewsPanel.setDividerLocation(screenSize.height / 2);
    }
    
    /**
     * Tauscht das Panel das den Erreichbarkeitsgraph enthaelt, mit einem neuen Panel aus.
     * 
     * @param newReachabilityGraphPanel
     *          das Austausch-Panel.
     */
    public void changeReachabilityGraphPanel(View_GraphPanel newReachabilityGraphPanel) {
        this.reachabilityGraphPanel = newReachabilityGraphPanel;
        bothGraphPanel.add(reachabilityGraphPanel, 1);
        bothGraphPanel.setDividerLocation(screenSize.width / 2);
        if(this.reachabilityGraphPanel.usesAutoLayout() == true && View_GraphPanel.getLayoutFlag() == 1) {
            // Ausgabe, dass das auto-Layout vom (p)EG aktiviert ist
            appendTextOnTextArea("Erreichbarkeits-Graph - autoLayout = aktiviert\n");
            View_GraphPanel.setLayoutFlag();
        }
    }
    
    /**
     * Tauscht das Statuslabel am unteren Fensterrand, mit einem neuen Statuslabel aus
     * 
     * @param newStatusLabel
     *          das Austausch-Label.
     */
    public void changeStatusLabel(JLabel newStatusLabel) {
        this.remove(statusLabel);
        this.statusLabel = newStatusLabel;
        this.add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Loescht das Panel, welches den Erreichbarkeitsgraph enthaelt.
     */
    public void removeReachabilityGraphPanel() {
        bothGraphPanel.remove(reachabilityGraphPanel);
    }
    
    /**
     * Loescht den Text, der in der Textausgabe angezeigt wird.
     */
    public void removeTextFromTextArea() {
        this.textArea.setText(null);
    }
    
    /**
     * Liefert das Petrinetz-Datenmodell von diesem Fenster.
     * 
     * @return
     *          Petrinetz-Datenmodell von diesem Fenster.
     */
    public Model_Petrinet getPetrinetModel() {
        return petrinet;
    }

    /**
     * Das Petrinetz-Datenmodell von diesem Fenster wird mit 
     * dem, als Parameter uebergebenen Petrinetz-Datenmodell ersetzt.
     * 
     * @param petrinet
     *          das neue Petrinetz-Datenmodell von diesem Fenster.
     */
    public void setPetrinet(Model_Petrinet petrinet) {
        this.petrinet = petrinet;
    }

    /**
     * Liefert den Petrinetz-Graph von diesem Fenster.
     * 
     * @return
     *          Petrinetz-Graph von diesem Fenster.
     */
    public View_PetrinetGraph getPetrinetGraphView() {
        return petrinetGraph;
    }

    /**
     * Der Petrinetz-Graph von diesem Fenster wird zum Petrinetz-Graph der als Parameter uebergeben wird.
     * 
     * @param petrinetGraph
     *          der neue Petrinetz-Graph von diesem Fenster.
     */
    public void setPetrinetGraph(View_PetrinetGraph petrinetGraph) {
        this.petrinetGraph = petrinetGraph;
    }

    /**
     * Liefert den Erreichbarkeits-Graph von diesem Fenster.
     * 
     * @return 
     *          Erreichbarkeits-Graph von diesem Fenster.
     */
    public View_ReachabilityGraph getReachabilityGraphView() {
        return reachabilityGraph;
    }

    /**
     * Der Erreichbarkeits-Graph von diesem Fenster wird zum Erreichbarkeits-Graph der als Parameter uebergeben wird.
     * 
     * @param reachGraph
     *          der neue Erreichbarkeits-Graph von diesem Fenster.
     */
    public void setReachabilityGraph(View_ReachabilityGraph reachGraph) {
        this.reachabilityGraph = reachGraph;
    }
    
    /**
     * Liefert das Panel, welches den Petrinetz-Graph von diesem Fenster enthaelt.
     * 
     * @return
     *          Panel, welches den Petrinetz-Graph von diesem Fenster enthaelt.
     */
    public View_GraphPanel getPetrinetGraphPanel() {
        return petrinetGraphPanel;
    }
 
    

    /**
     * Tauscht das Panel, welches den Petrinetz-Graph enthaelt, mit dem als Parameter uebergebenen Panel aus.
     * 
     * @param petrinetGraphPanel
     *          das neue Panel, welches den Petrinetz-Graph enthaelt, fuer dieses Fenster.
     */
    
    public void setPetrinetGraphPanel(View_GraphPanel petrinetGraphPanel) {
        this.petrinetGraphPanel = petrinetGraphPanel;
    }

    /**
     * Liefert das Panel, welches den Erreichbarkeits-Graph von diesem Fenster enthaelt.
     * 
     * @return
     *           Panel, welches den Erreichbarkeits-Graph von diesem Fenster enthaelt.
     */
    public View_GraphPanel getReachabilityGraphPanel() {
        return reachabilityGraphPanel;
    }

    /**
     * Tauscht das Panel, welches den Erreichbarkeits-Graph enthaelt, mit dem als Parameter uebergebenen Panel aus.
     * 
     * @param reachabilityGraphPanel
     *          das neue Panel, welches den Erreichbarkeits-Graph enthaelt, fuer dieses Fenster.
     */
    public void setReachabilityGraphPanel(View_GraphPanel reachabilityGraphPanel) {
        this.reachabilityGraphPanel = reachabilityGraphPanel;
    }
    
    /**
     * Liefert die Datei, die aktuell als Petrinetz auf dem Bildschirm angezeigt wird.
     * 
     * @return
     *          Datei zum aktuell angezeigten Petrinetz.
     */
    public File getCurrentFile() {
        return currentFile;
    }

    /**
     * Tauscht die Datei des aktuell angezeigten Petrinetzes mit der als Parameter uebergebenen Datei.
     * 
     * @param currentFile
     *          Datei zum aktuell angezeigten Petrinetz.
     */
    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }
    
    /**
     * Liefert das Verzeichnis, in dem sich die zuletzt geoeffnete Datei befindet. 
     * 
     * @return
     *          Verzeichnis der zuletzt geoeffneten Datei.
     */
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * aendert das Verzeichnis aus dem zuletzt eine Datei geoeffnet wurde.
     * 
     * @param currentDirectory
     *          Verzeichnis der zuletzt geoeffneten Datei.
     */
    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
    
    /**
     * Liefert alle pnml-Dateien aus dem Verzeichnis, aus dem zuletzt eine pnml-Datei geoeffnet wurde.
     * 
     * @return
     *          eine Menge von pnml-Dateien.
     */
    public List<File> getFileList() {
        return fileList;
    }

    /**
     * Tauscht alle pnml-Dateien aus dem Verzeichnis, aus dem zuletzt eine pnml-Datei 
     * geoeffnet wurde, mit der als Parameter uebergebenen Menge von pnml-Dateien aus.
     * 
     * @param fileList
     *          eine Menge von pnml-Dateien.
     */
    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
    
    /**
     * Haengt den uebergebenen Text an das Ende des Textes der im Textbereich angezeigt wird an.
     * 
     * @param text
     *          Text der an das Ende des Textes im Textbereich angehaengt wird.
     */
    public void appendTextOnTextArea(String text) {
        textArea.append(text);
    }
    
    /**
     * Liefert die Bildschirmaufloesung.
     * 
     * @return
     *          Bildschirmaufloesung
     */
    public Dimension getScreenSize() {
        return screenSize;
    }
}