package VaadinIRC.GUI.componentContainers;

import com.vaadin.ui.Window;

/**
 * Abstract base class for all Component containers.
 * @author Aleksi Postari
 *
 */
public abstract class AbstractComponentContainer extends Window
{
	/**
	 * Base class for all windows.<br>
	 * Adds the window to passed main window and creates the window if user desired to.
	 * @param mainWindow Main application window.
	 * @param createWindow Add this container to main application window and call {@link #createWindow()} method?
	 */
	public AbstractComponentContainer(Window mainWindow, boolean createWindow)
	{
		if (createWindow)
		{
			mainWindow.addWindow(this);
			createWindow();
		}
	}
	
	/**
	 * Creates the window. Called in constructor: {@link AbstractComponentContainer#AbstractWindowGUI(Window)}.
	 */
	public abstract void createWindow();
}
