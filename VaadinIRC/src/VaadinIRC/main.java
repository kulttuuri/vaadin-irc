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

package VaadinIRC;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import VaadinIRC.GUI.IRC.GUICommands;
import VaadinIRC.GUI.IRC.GUIDefine;
import VaadinIRC.GUI.IRC.GUITodo;
import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.terminal.ErrorMessage;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.*;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window.Notification;

/*
 * TODO: todo commands
 * addtodo <CONTENT>
 * removetodo <ID>
 * changetodo <ID> <NEWSTATE>
 * addtodoenum <NAME> <NEWENUM> (Tarkistaa onko jo olemassa enumia)
 * removetodoenum <ENUM>
 * 
 * TODO: Parse parameters.
 * TODO: You left the channel -message is coming twice in status channel.
 * TODO: Handle all rest of IRC messages.
 * TODO: Tab should autocomplete nickname.
 * TODO: Old messages should be removed from memory (right now if there are too many messages, it will not scroll to end on new messages).
 * TODO: Own nick change should be also announced in the status channel.
 * TODO: Topright icon should also be drawn even if you are missing / from end of the URL.
 * TODO: Top tabs should be drag & drop orderable.
 * TODO: Voting command should store results temporary to file (so if program crashes, they can be loaded from the file). Clear file when voting is stopped.
 * TODO: Search from logs and such should generate link to URL page for results (if there were any).
 * TODO: Split too long messages to multiple chat messages.
 * TODO: HIGH: Check that user is OP before executing a command.
 * TODO: Removetags on IRCHelper is too greedy, would need to only remove HTML tags.
 * TODO: close() is missing from JDBC calls (it is automatically now closed after certain times, not best practice though)
 * TODO: Users should be able to switch between 12 / 24 hour timestamps.
 * TODO: Skandit (character encoding for reading IRC messages, will need to implement so that it will recognise UTF, ISO etc.)
 * TODO: Channel texts are not 100% width.
 * TODO: Timeout check for threads (if over 300s from last ping, stop connection & threads).
 * TODO: New features:
 * 		- RSS reader for news
 * 		- !randomsentence ?#channel will get random sentence from channel.
 * 		- !randomlink ?#channel will fetch random link from channel.
 * 		- message nickname message (Will send message to user when he joins/changes nickname/sends message to channel).
 * 		- definesearch (searches from defines).
 * IRC Numerics: http://www.mirc.net/raws/#top
 */

/**
 * Entry point for VaadinIRC.
 * @author Aleksi Postari
 */
public class main extends Application implements HttpServletRequestListener
{
	/** Main VaadinIRC Application window. */
	private Window window;
	/** Login panel. */
	private Panel loginPanel;
	/** Reference to GUITodo. */
	private GUITodo todogui;
	/** Reference to GUIDefin. */
	private GUIDefine guidefine;
	/** Reference to GUICommands. */
	private GUICommands guicommands;
	
	/**
	 * Initializes vaadin application.
	 */
	@Override
	public void init()
	{
		// Load application settings from file
		settings.loadSettingsFromFile();

		createMainVaadinIRCWindow();
		createDefineView();
		createCommandsView();
		//createTodoWindow();
		
		// Check if we do need to show login panel
		if (settings.AUTHENTICATION_ENABLED) showLoginPanel();
		else startMainApplication();
	}
	
	/**
	 * Creates the main VaadinIRC application window.
	 */
	private void createMainVaadinIRCWindow()
	{
		// Create main window
		window = new Window(settings.APP_NAME);
		window.setTheme("VaIRCTheme");
		setMainWindow(window);

		// Set main window to full size.
		window.setSizeFull();
		window.getContent().setSizeFull();
		window.setStyleName("mainWindow");
	}
	
	/**
	 * Create window for todo view.
	 */
	private void createTodoWindow()
	{
		Window todoWindow = new Window();
		todoWindow.setName("todo");
		addWindow(todoWindow);
		todogui = new GUITodo(todoWindow);
	}
	
	/**
	 * Create window for defines view.
	 */
	private void createDefineView()
	{
		Window defineWindow = new Window();
		defineWindow.setName("defines");
		addWindow(defineWindow);
		guidefine = new GUIDefine(defineWindow);
	}
	
	/**
	 * Create window for command help.
	 */
	private void createCommandsView()
	{
		Window commandsWindow = new Window();
		commandsWindow.setName("commands");
		addWindow(commandsWindow);
		guicommands = new GUICommands(commandsWindow);
	}
	
	/**
	 * Shows login panel for the user.
	 */
	private void showLoginPanel()
	{
		loginPanel = new Panel("Login");
		loginPanel.setWidth(250, Sizeable.UNITS_PIXELS);
		loginPanel.setHeight(200, Sizeable.UNITS_PIXELS);
		LoginForm login = new LoginForm();
		loginPanel.addComponent(login);
		window.addComponent(loginPanel);
		VerticalLayout windowLayout = (VerticalLayout)window.getLayout();
		windowLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
		
		login.addListener(new LoginListener()
		{
			public void onLogin(LoginEvent event)
			{
				String username = event.getLoginParameter("username");
				String password = event.getLoginParameter("password");
				if (username.equals(settings.AUTHENTICATION_USERNAME) && password.equals(settings.AUTHENTICATION_PASSWORD))
				{
					window.removeComponent(loginPanel);
					startMainApplication();
				}
				else
				{
					Notification notification = new Notification("Wrong username or password.", Notification.TYPE_ERROR_MESSAGE);
					notification.setPosition(Notification.POSITION_BOTTOM_RIGHT);
					notification.setDelayMsec(250);
					window.showNotification(notification);
				}
			}
		});
	}
	
	/**
	 * Starts the main VaadinIRC application.
	 */
	private void startMainApplication()
	{
		VaadinIRC.getS().init(window);
	}
	
	public void onRequestStart(HttpServletRequest request, HttpServletResponse response)
	{
	}

	// Gets the query strings passed into the webpage and sends to windows.
	// We use this one instead of onRequestStart as these parameters are passed after the GUI interfaces and such are initialized first.
	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response)
	{
		if (todogui != null) todogui.handleQueryStrings(request, response);
		if (guidefine != null) guidefine.handleQueryStrings(request, response);
	}
}