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

import irc.bot.IRCBotCommands;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.ui.Window;

/**
 * Class used to show commands in a table for the user.
 * @author Aleksi Postari
 */
public class GUICommands extends AbstractIRCTableGUI
{
	/**
	 * Constructor to create the commands view. Automatically creates the GUI, table and fills
	 * the table with columns and data.
	 * @param window Reference to Window.
	 */
	public GUICommands(Window window)
	{
		super(window);
		
		// Add table columns
		ArrayList<String> columns = new ArrayList<String>();
		columns.add("Command");
		columns.add("Description");
		
		// Add commands
		ArrayList<String[]> commands = new ArrayList<String[]>();
		for (java.util.Map.Entry<String, String> entry : IRCBotCommands.commands.entrySet())
		{
			commands.add(new String[] { entry.getKey(), entry.getValue() } );
		}
		
		generateTableWithData("BOT Commands", columns, commands);
	}

	@Override
	public void handleQueryStrings(HttpServletRequest request, HttpServletResponse response)
	{
		// Does nothing with query strings.
	}
}