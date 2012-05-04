package VaadinIRC.GUI;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Window;

/**
 * Settings window.
 * @author Aleksi Postari
 *
 */
public class GUIWindowSettings extends AbstractWindowGUI
{
	public GUIWindowSettings(Window mainWindow)
	{
		super(mainWindow);
	}

	@Override
	public void createWindow()
	{
		setCaption("Settings");
		setHeight(500, Sizeable.UNITS_PIXELS);
		setWidth(400, Sizeable.UNITS_PIXELS);
		center();
	}
}
