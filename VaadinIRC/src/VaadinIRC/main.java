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

import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.ui.*;

/*
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
public class main extends Application
{
	/**
	 * Initializes vaadin application.
	 */
	@Override
	public void init()
	{
		// Load application settings from file
		settings.loadSettingsFromFile();

		// Create main window
		Window window = new Window(settings.APP_NAME);
		window.setTheme("VaIRCTheme");
		setMainWindow(window);

		// Set main window to full size.
		window.setSizeFull();
		window.getContent().setSizeFull();
		window.setStyleName("mainWindow");

		// Start VaadinIRC application.
		VaadinIRC vaadinIRC = new VaadinIRC(window, (Application) this);
	}
}