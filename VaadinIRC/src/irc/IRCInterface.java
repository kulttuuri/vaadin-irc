package irc;

import java.util.ArrayList;

/**
 * Interface which transmits the data between IRC client and GUI interface.
 * @author Aleksi Postari
 *
 */
public abstract interface IRCInterface
{
	/**
	 * Used to connect to IRC server with given IRC Session.
	 * @param session IRCSession containing the connection credentials.
	 */
	public abstract void connectToServer(IRCSession session);
	
	/**
	 * Returns the user's current channel name which he is on.
	 * @return Current channel name.
	 */
	public abstract String getCurrentChannelName();
	
	/**
	 * Sends message to IRC server using the IRC connection's write buffer.
	 * These messages are mostly slash commands, like /JOIN #channel, remember to remove beginning slash if they are.
	 * @see IRC#writeMessageToBuffer(String)
	 * @param message Message to be sent to server.
	 */
	public abstract void sendMessageToServer(String message);
	
	/**
	 * Sends message to given channel.
	 * @param channel Name of the channel where the message will be sent (without #).
	 * @param message Message that will be sent to the channel.
	 * @return Returns true if message was sent to channel. Otherwise false.
	 */
	public abstract boolean sendMessageToChannel(String channel, String message);
	
	/**
	 * Sends message to given user.
	 * @param user
	 * @param message
	 * @return Returns true if message was sent to user. Otherwise false.
	 */
	public abstract boolean sendMessageToUser(String user, String message);
	
	/**
	 * When IRC Client receives a new message.
	 * @param message The message that was received.
	 * @param channel Channel where the message was received.
	 */
	public abstract void receivedNewMessage(String message, String channel);
	
	/**
	 * When user receives message that will need to be added to status channels messages.
	 * @param message Message that will be added to status channel messages.
	 */
	public abstract void receivedStatusMessage(String message);
	
	/**
	 * When current user joins a channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 */
	public abstract void joinedChannel(String channelName, String network);
	
	/**
	 * When other user joins the channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 * @param nickname Nickname of the user who joined the channel.
	 */
	public abstract void otherJoinedChannel(String channelName, String network, String nickname);
	
	/**
	 * When user list is changed in a channel.
	 * @param channel Channel where the userlist was changed.
	 * @param users ArrayList of current users in the channel.
	 */
	public abstract void userListChanged(String channel, ArrayList<String> users);
}