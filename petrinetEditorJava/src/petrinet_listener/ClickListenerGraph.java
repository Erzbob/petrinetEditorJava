package petrinet_listener;

import org.graphstream.ui.view.ViewerListener;

/**
 * Diese abstrakte Klasse dient als Adapter fuer das Interface {@link ViewerListener}.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public abstract class ClickListenerGraph implements ViewerListener {

    /**
     * Die gleichnamige Methode aus dem Interface {@link ViewerListener}.
     * Die Methode wurde hier zwar implementiert, hat aber keine Funktion,
     * da ihr Rumpf leer ist.
     */
    @Override
    public void viewClosed(String viewName) {
    }

    /**
     * Die gleichnamige Methode aus dem Interface {@link ViewerListener}.
     * Die Methode wurde hier zwar implementiert, hat aber keine Funktion,
     * da ihr Rumpf leer ist.
     */
    @Override
    public void buttonPushed(String id) {
    }

    /** 
     * Die gleichnamige Methode aus dem Interface {@link ViewerListener}.
     * Die Methode wurde hier zwar implementiert, hat aber keine Funktion,
     * da ihr Rumpf leer ist.
     */
    @Override
    public void buttonReleased(String id) {
    }

    /**
     * Die gleichnamige Methode aus dem Interface {@link ViewerListener}.
     * Die Methode wurde hier zwar implementiert, hat aber keine Funktion,
     * da ihr Rumpf leer ist.
     */
    @Override
    public void mouseOver(String id) {
    }

    /**
     * Die gleichnamige Methode aus dem Interface {@link ViewerListener}.
     * Die Methode wurde hier zwar implementiert, hat aber keine Funktion,
     * da ihr Rumpf leer ist.
     */
    @Override
    public void mouseLeft(String id) {
    }
}
