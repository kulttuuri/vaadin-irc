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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import VaadinIRC.main;
import VaadinIRC.settings;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;

/**
 * Class used to show todo view for the user.
 * @author Aleksi Postari
 */
public class GUITodo extends AbstractIRCTableGUI
{
	/**
	 * Constructor. Stores reference to Window and creates the GUI.
	 * @param window Reference to Window.
	 */
	public GUITodo(Window window)
	{
		super(window);
	}
	
	@Override
	public void handleQueryStrings(HttpServletRequest request, HttpServletResponse response)
	{
		String channel = request.getParameter("channel");
		if (channel != null && !request.equals("")) channel = channel.replaceAll("[^a-zA-Z0-9/_/-]", "");
		if (channel != null)
		{
			if (channel.trim().equals(""))
			{
				showError("Channel cannot be empty.");
				return;
			}
			SQLContainer container = createSQLContainer("SELECT status, ID, content FROM todo WHERE CHANNEL = '#" + channel + "'", "id");
			if (container == null) showError("Channel cannot be empty.");
			else if (container.size() == 0) showError("No todo items found for channel.");
			else generateTableWithData("TODO items for channel #" + channel, container);
		}
	}
}