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

package VaadinIRC.GUI.componentContainers;

import VaadinIRC.settings;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

/**
 * Contains the components for the settings windows.
 * @author Aleksi Postari
 *
 */
public abstract class SettingsComponentContainer extends AbstractComponentContainer
{
	/** Server textfield */
	protected TextField textfieldServer;
	/** Port textfield */
	protected TextField textfieldPort;
	/** Connect to server button */
	protected Button buttonConnect;
	/** Disconnect from server button */
	protected Button buttonDisconnect;
	/** Nickname textfield */
	protected TextField textfieldNickname;
	/** Change nickname button */
	protected Button buttonChangeNick;
	
	/**
	 * Constructor to create the class.
	 * @param mainWindow Main application window.
	 */
	public SettingsComponentContainer(Window mainWindow)
	{
		super(mainWindow, true);
	}

	/**
	 * Adds change nickname button.<br>
	 * @see #buttonChangeNick
	 */
	public void addButtonChangeNickname()
	{
		buttonChangeNick = new Button("Change nickname");
		addComponent(buttonChangeNick);
		buttonChangeNick.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { buttonPressedChangeNickname(textfieldNickname.getValue().toString()); } });
	}
	
	/**
	 * Adds nickname textfield.<br>
	 * @see #textfieldNickname
	 */
	public void addtextfieldNickname()
	{
		textfieldNickname = new TextField("New nickname:");
		addComponent(textfieldNickname);
	}
	
	/**
	 * Adds server textfield.
	 * @see #textfieldServer
	 */
	public void addServerTextfield()
	{
		textfieldServer = new TextField("Server Address:");
		textfieldServer.setValue(settings.DEFAULT_SERVER_ADDRESS);
		addComponent(textfieldServer);
	}
	
	/**
	 * Adds server port textfield.
	 * @see #textfieldPort
	 */
	public void addServerPortTextfield()
	{
		textfieldPort = new TextField("Server Port:");
		textfieldPort.setValue(Integer.toString(settings.DEFAULT_SERVER_PORT));
		addComponent(textfieldPort);
	}
	
	/**
	 * Adds connect button & actionlistener for it.
	 * @see #buttonConnect
	 */
	public void addButtonConnect()
	{
		addComponent(new Label("<br>", Label.CONTENT_RAW));
		buttonConnect = new Button("Connect to Server");
		buttonConnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { buttonPressedConnectToServer(); } });
		addComponent(buttonConnect);
	}
	
	/**
	 * Adds disconnect button & actionlistener for it.
	 * @see #buttonDisconnect
	 */
	public void addButtonDisconnect()
	{
		buttonDisconnect = new Button("Disconnect from Server");
		buttonDisconnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { buttonPressedDisconnectFromServer(); } });
		addComponent(buttonDisconnect);
	}
	
	/**
	 * When button "disconnect from server" was pressed.
	 */
	public void buttonPressedDisconnectFromServer() { }
	
	/**
	 * When button "Connect to server" was pressed.
	 */
	public void buttonPressedConnectToServer() { }
	
	/**
	 * When button "Change nickname" was pressed.
	 * @param newNick value of the textfield {@link #textfieldNickname}
	 */
	public void buttonPressedChangeNickname(String newNick) { }
}