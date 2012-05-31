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

package irc;

import java.util.ArrayList;
import net.sourceforge.htmlunit.corejs.javascript.tools.shell.QuitAction;

import VaadinIRC.VaadinIRC.VaIRCInterface;

import irc.exceptions.NoConnectionInitializedException;
import irc.exceptions.TerminateConnectionException;
import irc.msghandlers.HandleConnectMessages;
import irc.msghandlers.HandleErrorMessages;
import irc.msghandlers.HandleExtraMessages;
import irc.msghandlers.HandleStdMessages;

/**
 * Thread to handle all incoming IRC messages.
 * @author Aleksi Postari
 *
 */
public class ThreadIRCReader extends Thread
{
    /** Reference to IRC class. */
    IRC irc;
    /** Standard message handler. */
    HandleStdMessages stdMessageHandler;
    /** Error message handler. */
    HandleErrorMessages errorHandler;
    /** Extra message handler without numeric replies. */
    HandleExtraMessages extraMsgHandler;
    
    /**
     * Constructor to create new reader thread.
     * @param irc Reference to IRC class.
     */
    public ThreadIRCReader(IRC irc)
    {
		this.irc = irc;
		errorHandler = new HandleErrorMessages(irc.GUIInterface);
		extraMsgHandler = new HandleExtraMessages(irc.GUIInterface, irc.session);
		stdMessageHandler = new HandleStdMessages(irc.GUIInterface);
    }

    /**
     * Thread is running here.
     */
    @Override
    public void run()
    {
    	String row = "";
	    
	    while (irc.isConnectionRunning())
	    {
	        // Try to read line
	        try
            {
	        	if (irc.reader == null) break;
        		row = irc.reader.readLine();
            }
	        catch (Exception e)
            {
	        	System.out.println("Error reading line: " + e);
	        	e.printStackTrace();
            }
	
	        // If connection is closed, return from thread.
	        if (!irc.isConnectionRunning()) return;
	        
	        // Handle the passed command.
	        handleCommand(row);
	        
	        // TODO: Botin toiminnot.
	    }
    }
    
    /**
     * Gets the passed IRC line and executes any functionality for it.
     * @param row {@link JavadocLibrary#row}
     */
    public void handleCommand(String row)
    {
        System.out.println("DEBUG: did read line: " + row);
        // Handle extra messages without numeric codes
        try
		{
			if (extraMsgHandler.handleLine(row, irc)) return;
		}
		catch (TerminateConnectionException e)
		{
    		irc.GUIInterface.receivedErrorMessage("", "", e.getMessage());
			irc.setConnectionRunning(false);
			return;
		}
        // Handle standard messages
        if (stdMessageHandler.handleLine(row, irc)) return;
        // Handle error replies
        if (errorHandler.handleLine(row, irc)) return;
    }
    
    /**
     * Checks the command that server sent to client.
     * @param row {@link JavadocLibrary#row}
     * @param message Command that should match the command sent from server. This is not case sensitive.
     * @return Returns true if command was same that was passed. Otherwise false.
     */
    private boolean checkCommand(String row, String command)
    {
    	String getCommand = IRCHelper.getStdCommand(row);
    	if (getCommand == null) return false;
    	
    	if (command.equalsIgnoreCase(getCommand)) return true;
    	else return false;
    }
}