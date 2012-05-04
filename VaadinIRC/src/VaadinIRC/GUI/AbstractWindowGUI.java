package VaadinIRC.GUI;

import com.vaadin.ui.Window;

/**
 * Abstract Base gui for all windows.
 * @author Aleksi Postari
 *
 */
public abstract class AbstractWindowGUI extends Window
{
	/**
	 * Base class for all windows.<br>
	 * Adds the window to passed main window and creates the window.
	 * @param mainWindow Main application window.
	 */
	public AbstractWindowGUI(Window mainWindow)
	{
		mainWindow.addWindow(this);
		createWindow();
	}
	
	/**
	 * Creates the window. Called in constructor: {@link AbstractWindowGUI#AbstractWindowGUI(Window)}.
	 */
	public abstract void createWindow();
}
