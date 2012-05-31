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

import VaadinIRC.GUI.componentContainers.SettingsComponentContainer;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Window for user to change hes nickname.
 * @author Aleksi Postari
 *
 */
public class GUIWindowChangeNickname extends SettingsComponentContainer
{
	/** IRC Session information. */
	private IRCSession session;
	/** IRC Interface. */
	IRCInterface irc;
	
	/**
	 * Constructor to initialize new window "Change Nickname".
	 * @param mainWindow Main application window.
	 * @param session IRCSession information.
	 * @param irc Reference to IRCInterface.
	 */
	public GUIWindowChangeNickname(Window mainWindow, IRCSession session, IRCInterface irc)
	{
		super(mainWindow);
		this.irc = irc;
		this.session = session;
		textfieldNickname.setValue(session == null ? "" : session.getNickname() == null ? "" : session.getNickname());
	}

	@Override
	public void createWindow()
	{
		setCaption("Change nickname");
		center();
		setWidth(300, Sizeable.UNITS_PIXELS);
		setHeight(300, Sizeable.UNITS_PIXELS);
		addtextfieldNickname();
		textfieldNickname.setWidth(200, Sizeable.UNITS_PIXELS);
		this.addComponent(new Label("<br>", Label.CONTENT_RAW));
		addButtonChangeNickname();
	}

	@Override
	public void buttonPressedChangeNickname(String newNick)
	{
		if (newNick.trim().equals(""))
		{
			textfieldNickname.setComponentError(new UserError("Username cannot be empty."));
			return;
		}
		
		irc.sendMessageToServer("/NICK " + newNick);
		this.setVisible(false);
		return;
	}
}