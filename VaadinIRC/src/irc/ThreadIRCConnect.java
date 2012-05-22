package irc;

import irc.exceptions.*;

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
		catch (InvalidNicknameException e)
		{
			irc.GUIInterface.receivedStatusMessage(e.getMessage());
		}
		catch (NicknameAlreadyInUseException e)
		{
			irc.GUIInterface.receivedStatusMessage(e.getMessage());
		}
		catch (IOException e)
		{
			irc.GUIInterface.receivedStatusMessage(e.getMessage());
		}
	}
	
	/**
	 * Connects to IRC server.
	 * @throws InvalidNicknameException If nickname was invalid.
	 * @throws NicknameAlreadyInUseException If nickname is already in use.
	 * @throws IOException If IOException did happen.
	 */
	private void connect() throws InvalidNicknameException, NicknameAlreadyInUseException, IOException
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
        	try { irc.closeConnection(); } catch (NoConnectionInitializedException noConE) { }
        }

        // Create new buffered writer of the connection
        irc.writer = new BufferedWriter(new OutputStreamWriter(irc.socket.getOutputStream()));
        // Create new buffered reader of the connection
        irc.reader = new BufferedReader(new InputStreamReader(irc.socket.getInputStream()));

        // Connect to IRC Server
        try
        {
            irc.setConnectionRunning(true);
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
        
        // Connect to server
        while ((row = irc.reader.readLine()) != null)
        {
        	irc.GUIInterface.receivedStatusMessage(row);
        	
        	if (row.startsWith("PING "))
        	{
        		irc.handlePingResponse(row);
        	}
        	// Additional PONG response
        	if (row.indexOf(IRCEnums.CONNECT_ADDITIONAL_PING_RESPONSE) >= 0)
        	{
        		try {
            		String[] split = row.split(" ");
        			irc.writeMessageToBuffer("PONG " + split[split.length - 1]);
        		} catch (Exception e) { }
        	}
            // If row was 004, we have succesfully connected to server.
        	else if (row.indexOf(IRCEnums.CONNECT_ONNECTION_SUCCESFUL) >= 0)
            {
                break;
            }
            // Invalid nickname
            else if(row.indexOf(IRCEnums.CONNECT_INVALID_NICKNAME) >= 0)
            {
                throw new InvalidNicknameException();
            }
            // Nickname already in use
            else if (row.indexOf(IRCEnums.CONNECT_NICKNAME_IN_USE) >= 0)
            {
                throw new NicknameAlreadyInUseException();
            }
        }

        irc.GUIInterface.receivedStatusMessage("Connected to network: " + irc.session.getServer() + " through port " + irc.session.getServerPort());
        irc.setConnectionRunning(true);
        
        // Start thread for reading IRC messages
        irc.threadIRCReader = new ThreadIRCReader(irc);
        irc.threadIRCReader.start();
	}
}
