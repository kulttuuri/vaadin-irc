/**
 * Copyright (C) 2012 Aleksi Postari (@kulttuuri, aleksi@postari.net)
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * This code is part of project Vaadin Irkkia.
 * License in short: You can use this code as you wish, but please keep this license information intach or credit the original author in redistributions.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package VaadinIRC.GUI;

import java.util.Iterator;

import VaadinIRC.main;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.TabSheet.Tab;

/**
 * GUI for VaadinIRC IRC View.
 * @author Aleksi Postari
 *
 */
public abstract class VaadinIrcGUI implements CloseHandler
{
	private static final long serialVersionUID = 1L;
	/** Contains reference to Vaadin application main window. */
	protected Window window;
	/** Contains the channel / user conversation tabs. */
	protected TabSheet channelTabs;
	/** Top information label located in topright corner of the application. */
	protected Label topLabel;
	/** Main application layout. */
	protected VerticalLayout mainLayout;
	
	/**
	 * Initializes the application with given window
	 * (primary just sets up the GUI, tabs and such)
	 * @param window Reference to window.
	 */
	public void init(Window window)
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
			topLabel = new Label("<div style='float: right; background-color: #BDE3F6; width: 40%; border: 1px solid black;'><img src='./VAADIN/themes/VaIRCTheme/images/server_delete.png'/> Not connected to any network.</div>", Label.CONTENT_RAW);
			mainLayout.addComponent(topLabel);
			mainLayout.addComponent(channelTabs);
			channelTabs.setSizeFull();
			channelTabs.setCloseHandler(this);
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

	public abstract void onTabClose(TabSheet tabsheet, Component tabContent);
}