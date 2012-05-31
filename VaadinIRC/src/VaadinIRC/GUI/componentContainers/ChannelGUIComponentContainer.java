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

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * Contains all the components for channel GUI.
 * @author Aleksi Postari
 *
 */
public class ChannelGUIComponentContainer extends AbstractComponentContainer
{
	/** Channel title label. */
	protected Label labelTitle;
	/** Settings button */
	protected Button buttonSettings;
	/** Change nickname button */
	protected Button buttonChangeNick;
	/** Refresh usernames button */
	protected Button buttonRefreshUsernames;
	/** Panel containing the channel messages. */
	protected Panel panelMessages;
	/** Table containing the nicknames in the channel. */
	protected Table tableNicknames;
	/** Textfield which will be used to write the message. */
	protected TextField textfieldMessagefield;
	/** Button to send the message to channel or to IRC as an command. */
	protected Button buttonSendMessage;
	/** Panel containing this channel's GUI */
	protected Panel panel;
	/** Selected nickname in table of nicknames. */
	protected String selectedNickname = "";
	/** is the message textfield focused? */
	protected boolean isMsgTextfieldFocused = false;
	
	/**
	 * Constructor to create the class.
	 * @param mainWindow Main application window.
	 */
	public ChannelGUIComponentContainer(Window mainWindow)
	{
		super(mainWindow, false);
	}

	@Override
	public void createWindow()
	{
		// Not used.
	}
}