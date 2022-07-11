package petrinet_gui;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Diese ist die Klasse die eine Menue-Leiste mit den Menues und Menue-Punkten beschreibt, 
 * die fuer die Anwendung vorgesehen ist, die im Zuge des ProPra erstellt werden soll. <br><br>
 * 
 * Diese Klasse erweitert die Klasse {@link JMenuBar}. <br><br> 
 * 
 * Die Menues und Menue-Punkte werden beim erzeugen eines {@linkplain PetrinetMenuBar}-Objekts
 * gleich mit erstellt.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class PetrinetMenuBar extends JMenuBar {
    
    /**
     * Erzeugen und initialisieren eines {@linkplain PetrinetMenuBar}-Objekts, mit 
     * allen Menues und Menue-Eintraegen.
     */
    public PetrinetMenuBar() {
        // die verschiedenen Menues
        
        JMenu datei = new JMenu("Datei");
        JMenu hilfe = new JMenu("Hilfe");
        JMenu view = new JMenu("Ansicht");
        
        // oeffnen = Menue 0 Item 0
        JMenuItem oeffnen = new JMenuItem("Öffnen");
        oeffnen.setToolTipText("Dateiauswahl zum Öffnen einer .pnml-Datei");
        datei.add(oeffnen);
        // neuLaden = Menue 0 Item 1
        JMenuItem neuLaden = new JMenuItem("Neu laden");
        neuLaden.setToolTipText("Lädt die aktuelle .pnml-Datei neu");
        datei.add(neuLaden);
        // analyseMehrererDateien = Menue 0 Item 2
        JMenuItem analyseMehrererDateien = new JMenuItem("Analyse mehrerer Dateien");
        analyseMehrererDateien.setToolTipText("Dateiauswahl zur Analyse von mehrerern Petrinetzen");
        datei.add(analyseMehrererDateien);
        // beenden = Menue 0 Item 3
        JMenuItem beenden = new JMenuItem("Beenden");
        beenden.setToolTipText("Programm beenden");
        datei.add(beenden);
        // info = Menue 1 Item 0
        JMenuItem info = new JMenuItem("Info");
        info.setToolTipText("Info");
        hilfe.add(info);
        // Info ueber Quellen der Bilddateien die fuer die ToolBar verwendet werden
        // Menue 1 Item 1
        JMenuItem iconSource = new JMenuItem("Quellen der Symbole");
        iconSource.setToolTipText("Information über die Herkunft der verwendeten Symbole");
        hilfe.add(iconSource);
        //
        JMenuItem switchLayout = new JMenuItem("(p)EG-Auto-Layout umschalten");
        switchLayout.setToolTipText("Schaltet das Auto-Layout, für den aktuell angezeigten (partiellen)Erreichbarkeits-Graph um.");
        view.add(switchLayout);
        // Menues zu dieser Menue-Leiste hinzufuegen
        this.add(datei);
        this.add(hilfe);
        this.add(view);
    }
    
}
