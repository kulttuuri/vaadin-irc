package irc;

import irc.bot.IRCBot;
import irc.exceptions.NoConnectionInitializedException;
import java.io.*;
import java.net.*;
import VaadinIRC.settings;

/**
 * Base class for initialing a IRC connection.
 * Also handles the reading & writing to / from socket connection to IRC network.
 * @author Kulttuuri
 *
 */
public class IRC
{
	/** When IRC connection has not been established. */
	public static final int CONNECTION_STATUS_DISCONNECTED = 0;
	/** When IRC connection is currently connecting to the server. */
	public static final int CONNECTION_STATUS_CONNECTING = 1;
	/** When IRC connection has been established. */
	public static final int CONNECTION_STATUS_CONNECTED = 2;
	
	/** GUI Interface for the IRC Client to syncronize data between IRC & GUI. */
    protected IRCInterface GUIInterface;
    /** IRC Session information. */
    protected IRCSession session;
    /** To determine is the connection to IRC server established and active. */
    private boolean isRunning = false;
    /** Socket used to create an IRC connection to IRC server. */
    protected Socket socket;
    /** BufferedWriter used to write data to IRC server. */
    protected BufferedWriter writer;
    /** BufferedReader used to read data from IRC server. */
    protected BufferedReader reader;
    /** Thread for reading from IRC connection socket. */
    ThreadIRCReader threadIRCReader;
    /** @see IRCBot */
    private IRCBot ircbot;

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
    	this.ircbot = new IRCBot(settings.IRCBOT_ENABLED, settings.IRCBOT_DATABASE_ADDRESS,
    		settings.IRCBOT_DATABASE_USERNAME, settings.IRCBOT_DATABASE_PASSWORD,
    		settings.IRCBOT_DATABASE_DRIVER, settings.IRCBOT_DATABASE_NAME, settings.IRCBOT_BOT_CALL_SIGN, settings.VERSION);
    }

    /**
     * Sends new channel message to irc bot.
     * @param row {@link irc.JavadocLibrary#row}
     */
    public void sendChannelMessageToBot(String row)
    {
    	try
    	{
    		ircbot.receivedChannelMessage(this, GUIInterface, row);
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error handling bot command: " + e);
    	}
    }
    
    /**
     * When user joins, will send join message to bot for handling.
     * @param row {@link irc.JavadocLibrary#row}
     */
    public void sendJoinMessageToBot(String row)
    {
    	try
    	{
    		ircbot.joinedChannel(this, GUIInterface, row);
    	}
    	catch (Exception e)
    	{
    		System.out.println("Error handling bot command: " + e);
    	}
    }
    
    /**
     * Debug function. Used to send messages for irc reader to be handled.
     * @param message Message to be sent.
     */
    public void debugSendMessageToReader(String message)
    {
    	if (threadIRCReader == null)
    	{
            threadIRCReader = new ThreadIRCReader(this);
            threadIRCReader.start();
    	}
    	threadIRCReader.handleCommand(message);
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
    	// TODO: Siirrä privmsg raw viestit tänne kutsumaan.
    }
    
    /**
     * Sends message to given channel.
     * @param channel Channel where message will be sent.
     * @param message Message that will be sent.
     * @throws NoConnectionInitializedException If connection to IRC server is not established, this gets thrown.
     */
    public void sendMessageToChannel(String channel, String message) throws NoConnectionInitializedException
    {
		writeMessageToBuffer("PRIVMSG " + GUIInterface.getCurrentChannelName() + " :" + message);
        GUIInterface.receivedNewMessage(session.getNickname(), message, channel);
    }
    
    /**
     * Checks if connection to IRC server is still running.<br>
     * If debug is set to true, will always return true.
     * @return Returns true if connection to IRC server is still on. Otherwise false.
     */
    public boolean isConnectionRunning()
    {
    	if (GUIInterface.isDebugEnabled()) return true;
    	if (reader == null || writer == null) return false;
    	return isRunning;
    }
    
    /**
     * Sets connection state (running or not running).
     * @param state Connection state.
     */
    public void setConnectionRunning(boolean state)
    {
    	if (state)
    		GUIInterface.connectionStatusChanged(session.getServer(), CONNECTION_STATUS_CONNECTED);
    	else
    	{
    		try
			{
				closeConnection();
			}
			catch (NoConnectionInitializedException e) { }
    		GUIInterface.connectionStatusChanged(session.getServer(), CONNECTION_STATUS_DISCONNECTED);
    	}
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
    	catch (Exception e)
    	{
    		System.out.println("Error writing to buffer: " + e);
    		//e.printStackTrace();
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
    private void closeConnection() throws NoConnectionInitializedException
    {
    	try
    	{
    		writeMessageToBuffer("QUIT");
    	}
    	catch (NoConnectionInitializedException e)
    	{
        	reader = null;
        	writer = null;
        	throw new NoConnectionInitializedException();
    	}
    	reader = null;
    	writer = null;
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