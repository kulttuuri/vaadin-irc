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

package irc.msghandlers;

import irc.IRC;
import irc.IRCHelper;
import irc.IRCInterface;
import irc.JavadocLibrary;
import irc.exceptions.TerminateConnectionException;

/**
 * Base class for all IRC Message handlers.
 * @author Aleksi Postari
 *
 */
public abstract class MsgHandler
{
	/** Reference to IRC Interface. */
	protected IRCInterface irc;
	/** @see JavadocLibrary#row */
	protected String row = "";
	
	/**
	 * Constructor to take the reference to IRCInterface.
	 * @param irc IRCInterface.
	 */
	public MsgHandler(IRCInterface irc)
	{
		this.irc = irc;
	}
	
	/**
	 * To handle the line that server sent.<br>
     * @param ircRow {@link JavadocLibrary#row}
     * @param ircApp IRC application.
	 * @return Returns true if line was handled. Otherwise false.
	 * @throws TerminateConnectionException When connection was needed to be terminated.
	 */
	public abstract boolean handleLine(String ircRow, IRC ircApp) throws TerminateConnectionException;
	
    /**
     * Checks the command that server sent to client.<br>
     * Remember to store the IRC row to row variable.
     * @param message Command that should match the command sent from server. This is not case sensitive.
     * @return Returns true if command was same that was passed. Otherwise false.
     */
    protected boolean checkCommand(String command)
    {
    	String getCommand = IRCHelper.getStdCommand(row);
    	if (getCommand == null) return false;
    	
    	if (command.equalsIgnoreCase(getCommand)) return true;
    	else return false;
    }
}
