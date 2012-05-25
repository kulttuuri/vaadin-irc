package irc;

import irc.exceptions.*;
import irc.msghandlers.HandleConnectMessages;
import irc.msghandlers.HandleErrorMessages;
import irc.msghandlers.HandleExtraMessages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Thread for connecting to IRC server in separate thread so that actual program does not hang up while connecting.
 * @author Aleksi Postari
 *
 */
public class ThreadIRCConnect extends Thread
{
	IRC irc;
	
	/**
	 * Creates new IRC connection thread.
	 * @param irc Reference to IRC.
	 */
	public ThreadIRCConnect(IRC irc)
	{
		this.irc = irc;
	}
	
	/**
	 * Connects to IRC server in a separate thread.
	 */
	@Override
	public void run()
	{
		try
		{
			connect();
		}
		catch (IOException e)
		{
			irc.GUIInterface.quitNetwork(irc.getSession().getServer(), "Disconnected from server.");
		}
		catch (NullPointerException e)
		{
			irc.GUIInterface.quitNetwork(irc.getSession().getServer(), "Disconnected from server.");
		}
	}
	
	/**
	 * Connects to IRC server.
	 * @throws IOException If IOException did happen.
	 * @throws NullPointerException If NullPointerException did happen.
	 */
	private void connect() throws IOException, NullPointerException
	{
        irc.GUIInterface.receivedStatusMessage("Connecting to network: " + irc.session.getServer() + " using port " + irc.session.getServerPort() + "...");
        
        String row = null;

        // Create new socket connection to server
        try
        {
        	irc.socket = new Socket(irc.session.getServer(), irc.session.getServerPort());
        }
        catch (ConnectException e)
        {
        	irc.GUIInterface.receivedStatusMessage("Unable to initialize socket connection to server: " + irc.session.getServer() + " using port " + irc.session.getServerPort());
        	irc.setConnectionRunning(false);
        }

        // Create new buffered writer of the connection
        irc.writer = new BufferedWriter(new OutputStreamWriter(irc.socket.getOutputStream()));
        // Create new buffered reader of the connection
        irc.reader = new BufferedReader(new InputStreamReader(irc.socket.getInputStream()));

        // Connect to IRC Server
        try
        {
            irc.setConnectionRunning(true);
        	irc.GUIInterface.connectionStatusChanged(irc.getSession().getServer(), IRC.CONNECTION_STATUS_CONNECTING);
        	irc.writeMessageToBuffer("USER " + irc.session.getLogin() + " 8 * : " + irc.session.getLogin());
        	irc.writeMessageToBuffer("NICK " + irc.session.getNickname());
        }
        catch (NoConnectionInitializedException e)
        {
        	// This should not be happening here...
        	System.out.println(e);
        	irc.setConnectionRunning(false);
        	e.printStackTrace();
        }
        
        HandleConnectMessages connectHandler = new HandleConnectMessages(irc.GUIInterface);
        HandleErrorMessages errorHandler = new HandleErrorMessages(irc.GUIInterface);
        HandleExtraMessages extraHandler = new HandleExtraMessages(irc.GUIInterface, irc.session);
        
        // Connect to server
        boolean connected = false;
        while ((row = irc.reader.readLine()) != null)
        {
        	//irc.GUIInterface.receivedStatusMessage("DEBUG: " + row);
        	// Handle connect messages
        	try
        	{
        		if (connectHandler.handleConnectLines(row, irc)) connected = true;
        	}
        	catch (InvalidNicknameException e)
        	{
        		irc.GUIInterface.receivedErrorMessage("", "", e.getMessage());
        		irc.setConnectionRunning(false);
        		connected = false;
        		break;
        	}
        	catch (NicknameAlreadyInUseException e)
        	{
        		irc.GUIInterface.receivedErrorMessage("", "", e.getMessage());
        		irc.setConnectionRunning(false);
        		connected = false;
        		break;
        	}
        	
        	// Handle extra messages, like NOTICE
        	try
			{
				if (extraHandler.handleLine(row, irc)) continue;
			}
			catch (TerminateConnectionException e)
			{
        		irc.GUIInterface.receivedErrorMessage("", "", e.getMessage());
				irc.setConnectionRunning(false);
				break;
			}
        	// Handle error messages.
        	if (errorHandler.handleLine(row, irc)) continue;
        	
        	if (connected) break;
        	if (!irc.isConnectionRunning()) break;
        }
        
        if (!connected)
        {
        	irc.setConnectionRunning(false);
        	return;
        }

        irc.setConnectionRunning(true);
        
        // Start thread for reading IRC messages
        irc.threadIRCReader = new ThreadIRCReader(irc);
        irc.threadIRCReader.start();
	}
}