package VaadinIRC.GUI;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * GUI for VaadinIRC IRC View.
 * @author Aleksi Postari
 *
 */
public class VaadinIrcGUI
{
	/** Contains reference to Vaadin application main window. */
	protected Window window;
	/** Contains the channel / user conversation tabs. */
	protected TabSheet channelTabs;
	
	public VaadinIrcGUI(Window window)
	{
		this.window = window;
		channelTabs = new TabSheet();
		channelTabs.setImmediate(true);
		createIrcView();
	}
	
	/**
	 * Returns the selected channel tab name.
	 * @return Selected channel tab name.
	 */
	public String getSelectedChannelName()
	{
		return channelTabs.getSelectedTab().getCaption();
	}
	
	/**
	 * Creates the main IRC view.
	 */
	private void createIrcView()
	{
		VerticalLayout vert = new VerticalLayout();
		vert.setWidth(800, Sizeable.UNITS_PIXELS);
		vert.setHeight(700, Sizeable.UNITS_PIXELS);
			vert.addComponent(channelTabs);
		window.addComponent(vert);
	}
}
