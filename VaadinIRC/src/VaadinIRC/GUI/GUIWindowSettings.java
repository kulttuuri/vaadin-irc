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

import irc.IRCInterface;
import irc.IRCSession;

import VaadinIRC.settings;
import VaadinIRC.GUI.componentContainers.SettingsComponentContainer;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Settings window.
 * @author Aleksi Postari
 *
 */
public class GUIWindowSettings extends SettingsComponentContainer
{
	/** IRC Interface */
	private IRCInterface irc;
	/** IRC Session information */
	private IRCSession session;
	
	/**
	 * Constructor to create new settings window.
	 * @param mainWindow Main application window.
	 * @param session IRCSession.
	 * @param irc IRCInterface.
	 */
	public GUIWindowSettings(Window mainWindow, IRCSession session, IRCInterface irc)
	{
		super(mainWindow);
		this.irc = irc;
		this.session = session;
		if (session != null)
		{
			textfieldServer = new TextField();
			textfieldPort = new TextField();
			textfieldNickname = new TextField();
			if (session.getServer() != null) textfieldServer.setValue(session.getServer());
			String curPort = Integer.toString(session.getServerPort());
			if (curPort != null && !curPort.equals("0")) textfieldPort.setValue(curPort);
			if (session.getNickname() != null) textfieldNickname.setValue(session.getNickname());
		}
		createWindow();
	}

	@Override
	public void createWindow()
	{
		setCaption("Settings");
		setHeight(500, Sizeable.UNITS_PIXELS);
		setWidth(400, Sizeable.UNITS_PIXELS);
		center();
		if (irc == null) return;
		if (!irc.isConnectionRunning())
		{
			addtextfieldNickname();
			addServerTextfield();
			addServerPortTextfield();
			addButtonConnect();
			
			VerticalLayout mainL = (VerticalLayout)getContent();
			mainL.setComponentAlignment(textfieldNickname, Alignment.MIDDLE_CENTER);
			mainL.setComponentAlignment(textfieldServer, Alignment.MIDDLE_CENTER);
			mainL.setComponentAlignment(textfieldPort, Alignment.MIDDLE_CENTER);
			mainL.setComponentAlignment(buttonConnect, Alignment.MIDDLE_CENTER);
		}
		else
		{
			addButtonDisconnect();
			VerticalLayout mainL = (VerticalLayout)getContent();
			mainL.setComponentAlignment(buttonDisconnect, Alignment.MIDDLE_CENTER);
		}
	}
	
	@Override
	public void buttonPressedDisconnectFromServer()
	{
		irc.quitNetwork(session.getServer(), "");
		close();
	}
	
	@Override
	public void buttonPressedConnectToServer()
	{
		// Validate server
		if (textfieldServer.getValue() == null || textfieldServer.getValue().toString().trim().equals(""))
		{
			textfieldServer.setComponentError(new UserError("Server cannot be empty."));
			return;
		}
		textfieldServer.setComponentError(null);
		
		// Validate port
		try
		{
			session.setServerPort(Integer.parseInt(textfieldPort.getValue().toString()));
		}
		catch (NumberFormatException e)
		{
			textfieldPort.setComponentError(new UserError("Port must be "));
		}
		if (textfieldPort.getValue() == null || textfieldPort.getValue().toString().trim().equals(""))
		{
			textfieldPort.setComponentError(new UserError("Port cannot be empty."));
			return;
		}
		textfieldPort.setComponentError(null);
		
		// TODO: Store old nickname.
		// TODO: Validate nickname.
		session.setNickname(textfieldNickname.getValue().toString());
		session.setLogin("VaIRCUser");
		session.setRealName("Vaadin IRC User");
		
		// If no problems, store values to session and connect to server.
		session.setServer(textfieldServer.getValue().toString());
		irc.connectToServer(session);
		close();
	}
}