package VaadinIRC.GUI;

import java.util.Iterator;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.TabSheet.Tab;

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
	/** Main application layout. */
	protected VerticalLayout mainLayout;
	
	public VaadinIrcGUI(Window window)
	{
		this.window = window;
		channelTabs = new TabSheet();
		channelTabs.setImmediate(true);
		createIrcView();
	}
	
	/**
	 * Creates the main IRC view.
	 */
	private void createIrcView()
	{
		//VerticalLayout vert = new VerticalLayout();
		//window.addComponent(vert);
		//vert.setWidth(100, Sizeable.UNITS_PERCENTAGE);
		//vert.setHeight(100, Sizeable.UNITS_PERCENTAGE);
		//channelTabs.setSizeFull();
		//window.addComponent(channelTabs);
		mainLayout = new VerticalLayout();
			mainLayout.addComponent(channelTabs);
			channelTabs.setSizeFull();
			mainLayout.setExpandRatio(channelTabs, 1.0f);
			mainLayout.setSizeFull();
		window.addComponent(mainLayout);
		//vert.setExpandRatio(channelTabs, 100);
	}
	
	/**
	 * Iterates through channel tabs and returns the tab with given name.
	 * If tab was not found, will return null.
	 * @param tabName Name of the tab what you wish to retrieve.
	 * @return Returns the tab with given name. If tab was not found, returns null.
	 */
	protected Tab getTab(String tabName)
	{
		Iterator<Component> i = channelTabs.getComponentIterator();
		Tab tab;
		while (i.hasNext())
		{
		    Component c = (Component)i.next();
		    tab = channelTabs.getTab(c);
		    // Store found tab
		    if (tabName.equals(tab.getCaption())) return channelTabs.getTab(c);
		}
		return null;
	}
}
