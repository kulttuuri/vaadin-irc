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

package VaadinIRC.GUI.IRC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import VaadinIRC.settings;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * Abstract base class for all IRC table views containing read-only data.
 * @author Aleksi Postari
 */
public abstract class AbstractIRCTableGUI
{
	/** Reference to Window. */
	protected Window window;
	/** Table where data is being shown to user. */
	protected Table table;
	
	/**
	 * Constructor to pass window for the GUI and to create the GUI view.
	 * @param window Reference to Window.
	 */
	public AbstractIRCTableGUI(Window window)
	{
		this.window = window;
		createGUI();
	}
	
	/**
	 * Creates the the GUI.
	 */
	public void createGUI()
	{
		window.setSizeFull();
		((VerticalLayout)window.getLayout()).setSizeFull();
		HorizontalLayout horiPanel = new HorizontalLayout();
		horiPanel.setSizeFull();
			table = new Table("");
			table.setImmediate(true);
			table.setSelectable(true);
			table.setWidth(100, Sizeable.UNITS_PERCENTAGE);
			table.setHeight(100, Sizeable.UNITS_PERCENTAGE);
			horiPanel.addComponent(table);
		
		window.addComponent(horiPanel);
	}
	
	/**
	 * Gets SQLContainer object generated with given SQL query.
	 * @param sqlQuery SQL Query that will be used to generate the SQLContainer.
	 * @param columnToOrder Which column will be used to order the results.
	 * @return generated SQLContainer object. Can also be null on problems.
	 */
	public SQLContainer createSQLContainer(String sqlQuery, String columnToOrder)
	{
		try
		{
			SimpleJDBCConnectionPool connectionPool =
				new SimpleJDBCConnectionPool(settings.IRCBOT_DATABASE_DRIVER, settings.IRCBOT_DATABASE_ADDRESS + settings.IRCBOT_DATABASE_NAME,
				settings.IRCBOT_DATABASE_USERNAME, settings.IRCBOT_DATABASE_PASSWORD, 2, 2);
			return new SQLContainer(new FreeformQuery(sqlQuery, Arrays.asList(columnToOrder), connectionPool));
		}
		catch (Exception e)
		{
			Notification notification = new Notification("Problem creating SQL connection: " + e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
			notification.setPosition(Notification.POSITION_BOTTOM_RIGHT);
			window.showNotification(notification);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Generates table with given SQLContainer data.
	 * @param tableCaption Table title.
	 * @param sqlContainer Reference to SQLContainer that you want to fill the table with.
	 */
	public void generateTableWithData(String tableCaption, SQLContainer sqlContainer)
	{
		table.setVisible(true);
		table.setCaption(tableCaption);
		table.setContainerDataSource(sqlContainer);
		table.requestRepaint();
	}
	
	/**
	 * Generates the table with given programatic data.
	 * @param tableCaption Table title.
	 * @param columns Columns for the table.
	 * @param data Data for the table. It's an arraylist where one line is one arrayList index and inside
	 * there is String[] array where array indexes are the columns.
	 */
	public void generateTableWithData(String tableCaption, ArrayList<String> columns, ArrayList<String[]> data)
	{
		table.setVisible(true);
		table.setCaption(tableCaption);
		// Add table columns
		for (String column : columns) table.addContainerProperty(column, String.class, "");
		// Add table data
		for (int i = 0; i < data.size(); i++)
		{
			table.addItem(data.get(i), i+1);
		}
		table.requestRepaint();
	}
	
	/**
	 * Hides the table and shows error message for user.
	 * @param errorMessage Error message to be shown.
	 */
	public void showError(String errorMessage)
	{
		table.setVisible(false);
		Notification notification = new Notification(errorMessage, Notification.TYPE_HUMANIZED_MESSAGE);
		notification.setDelayMsec(6000);
		notification.setPosition(Notification.POSITION_CENTERED);
		window.showNotification(notification);
		table.requestRepaint();
	}
	
	/**
	 * You can write your own query string handling in here.
	 * @param request HttpServletRequest.
	 * @param response HttpServletResponse.
	 */
	public abstract void handleQueryStrings(HttpServletRequest request, HttpServletResponse response);
}