package irc;

import irc.exceptions.NoConnectionInitializedException;
import java.io.*;
import java.net.*;

/**
 * Base class for initialing a IRC connection.
 * Also handles the reading & writing to / from socket connection to IRC network.
 * @author Kulttuuri
 *
 */
public class IRC
{
	/** GUI Interface for the IRC Client to syncronize data between IRC & GUI. */
    protected IRCInterface GUIInterface;
    /** IRC Session information. */
    protected IRCSession session;

    /** To determine in reader and writer threads if the application is still running. */
    private boolean isRunning = true;

    /** Socket used to create an IRC connection to IRC server. */
    protected Socket socket;
    /** BufferedWriter used to write data to IRC server. */
    protected BufferedWriter writer;
    /** BufferedReader used to read data from IRC server. */
    protected BufferedReader reader;
    
    /** Thread for reading from IRC connection socket */
    ThreadIRCReader threadIRCReader;
    /** Thread for writing to IRC connection socket. */
    ThreadIRCWriter threadIRCWriter;

    /**
     * Constructor to initialize new IRC and connect to server.
     * Send your self implemented GUIInterface here that acts as an
     * interface to rely irc messages to / from IRC socket connection.
     * @param GUIInterface Your own implemented GUIInterface interface.
     * @param session Session containing the IRC user and server information.
     */
    public IRC(IRCInterface GUIInterface, IRCSession session)
        {
    	this.GUIInterface = GUIInterface;
    	this.session = session;
        }

    /**
     * Returns the IRC Session.
     * @return IRCSession.
     */
    public IRCSession getSession()
    {
    	return session;
    }
    
    /**
     * Sends message to given user.
     * @param user Nickname of the user.
     * @param message Message to be sent.
     */
    public void sendMessageToUser(String user, String message)
    {
    	// TODO: Implement.
    }
    
    /**
     * Sends message to given channel.
     * @param channel Channel where message will be sent.
     * @param message Message that will be sent.
     */
    public void sendMessageToChannel(String channel, String message)
    {
    	try {
    		writeMessageToBuffer("PRIVMSG " + GUIInterface.getCurrentChannelName() + " :" + message);
		} catch (Exception e) { System.out.println(e); e.printStackTrace(); }
        GUIInterface.receivedNewMessage(IRCHelper.generateIRCMessage(session.getNickname(), message), channel);
    }
    
    /**
     * Checks if connection to IRC server is still running.
     * @return Returns true if connection to IRC server is still on. Otherwise false.
     */
    public boolean isConnectionRunning()
    {
    	return isRunning;
    }
    
    /**
     * Sets connection state.
     * @param state
     * @return
     */
    public void setConnectionRunning(boolean state)
    {
    	this.isRunning = state;
    }
    
    /**
     * Writes given message to buffer (adding \r\n to the end) and flushes the writer buffer.
     * @param message Message to be sent.
     * @return Returns true if message was written & flushed to buffer. Otherwise false.
     * @throws NoConnectionInitializedException If connection to IRC server has not been established, this is thrown.
     */
    public boolean writeMessageToBuffer(String message) throws NoConnectionInitializedException
    {
    	if (!isConnectionRunning()) throw new NoConnectionInitializedException();
    	System.out.println("writing:" + message);
    	try
    	{
    		writer.write(message + "\r\n");
    		writer.flush();
    		return true;
    	}
    	catch (IOException e)
    	{
    		System.out.println("Error writing to buffer: " + e);
    		e.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * Returns the implemented IRCInterface.
     * @return IRCInterface.
     */
    public IRCInterface getGUIInterface()
    {
    	return GUIInterface;
    }

    /**
     * Closes the current connection to IRC server.
     * @throws NoConnectionInitializedException If connection was not initialized, this is thrown.
     */
    public void closeConnection() throws NoConnectionInitializedException
    {
    	if (!isConnectionRunning()) throw new NoConnectionInitializedException();
    	
    	isRunning = false;
    	writeMessageToBuffer("QUIT");
    	GUIInterface.receivedStatusMessage("Connection to network has been closed.");
    }
    
    /**
     * Responds to PING message from server.
     * @param message Whole server PING :<number> message.
     */
    public void handlePingResponse(String message)
    {
    	String[] split = message.split(":");
    	try
    	{
    		writeMessageToBuffer("PONG " + split[1].substring(1));
    	}
    	catch (NoConnectionInitializedException e)
    	{
    		System.out.println(e);
    		e.printStackTrace();
    	}
    }
    
    /**
     * Used to connect to IRC network using the member IRCSession information contained in this class. 
     * Spawns a separate thread to initialize the IRC connection so program does not hang up.
     */
    public void connect()
    {
    	ThreadIRCConnect threadIRCConnect = new ThreadIRCConnect(this);
    	threadIRCConnect.start();
    }
}