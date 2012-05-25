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
	 * Is connection to IRC server running?
	 * @return Returns true if connection to IRC server is running. Otherwise false.
	 */
	public abstract boolean isConnectionRunning();
	
	/**
	 * Returns the user's current channel name which he is on.
	 * @return Current channel name.
	 */
	public abstract String getCurrentChannelName();
	
	/**
	 * Sets channel topic.
	 * @param channel Name of the channel.
	 * @param topic Topic for the channel.
	 * @param nickname Who did change the topic.
	 * @param notify Do we want to notify channel about the topic change? Generally we do not notify on join topic, but when user changes topic in realtime.
	 */
	public abstract void setChannelTopic(String channel, String topic, String nickname, boolean notify);
	
	/**
	 * Sends message to IRC server using the IRC connection's write buffer.
	 * These messages are mostly slash commands, like /JOIN #channel, remember to remove beginning slash if they are.
	 * @see IRC#writeMessageToBuffer(String)
	 * @param message Message to be sent to server.
	 */
	public abstract boolean sendMessageToServer(String message);
	
	/**
	 * Sends message to given channel.
	 * @param channel Name of the channel where the message will be sent (without #).
	 * @param message Message that will be sent to the channel.
	 * @return Returns true if message was sent to channel. Otherwise false.
	 */
	public abstract boolean sendMessageToChannel(String channel, String message);
	
	/**
	 * Sends message to given user.
	 * @param user Target user.
	 * @param message Message to be sent.
	 */
	public abstract void sendMessageToUser(String user, String message);
	
	/**
	 * When IRC Client receives a new message.
	 * @param nickname Message sender's nickname.
	 * @param message The message that was received.
	 * @param channel Channel where the message was received.
	 */
	public abstract void receivedNewMessage(String nickname, String message, String channel);
	
	/**
	 * When user receives new private message.
	 * @param nickname Sender nickname.
	 * @param message Message that was sent.
	 */
	public abstract void receivedNewPrivateMessage(String nickname, String message);
	
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
	 * When user parts a channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 */
	public abstract void leftChannel(String channelName, String network);
	
	/**
	 * When other user parts a channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 * @param nickname Nickname of the user who parted the channel.
	 */
	public abstract void otherLeftChannel(String channelName, String network, String nickname);
	
	/**
	 * When current user was kicked from a channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 * @param reason Reason.
	 */
	public abstract void kickedFromChannel(String channelName, String network, String reason);
	
	/**
	 * When other user was kicked from a channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 * @param nickname Nickname of the user who was kicked from the channel.
	 * @param reason Reason.
	 */
	public abstract void otherKickedFromChannel(String channelName, String network, String nickname, String reason);
	
	/**
	 * When current user was banned from a channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 * @param reason Reason.
	 */
	public abstract void bannedFromChannel(String channelName, String network, String reason);
	
	/**
	 * When other user was banned from a channel.
	 * @param channelName Name of the channel.
	 * @param network Name of the network.
	 * @param nickname Nickname of the user who was banned from the channel.
	 * @param reason Reason.
	 */
	public abstract void otherBannedFromChannel(String channelName, String network, String nickname, String reason);
	
	/**
	 * When user receives error message from server (codes 400-599)
	 * @param row {@link irc.JavadocLibrary#row}
	 * @param errorCode Error code number.
	 * @param error Error message (reason).
	 */
	public abstract void receivedErrorMessage(String row, String errorCode, String errorMessage);
	
	/**
	 * User did quit from network.
	 * @param network Name of the network.
	 * @param reason Reason of the quit.
	 */
	public abstract void quitNetwork(String network, String reason);
	
	/**
	 * When IRC connection state has been changed, this gets called.<br>
	 * Refer to the static CONNECTION_STATE variables in class irc for status types.
	 * @param network Name of the network.
	 * @param status New connection status.
	 */
	public abstract void connectionStatusChanged(String network, int status);
	
	/**
	 * Other user quit the network.
	 * @param nickname Nickname that did quit.
	 * @param network Name of the network.
	 * @param reason Quit reason.
	 */
	public abstract void otherQuitNetwork(String nickname, String network, String reason);
	
	/**
	 * When other user changes nickname.
	 * @param oldNickname Old nickname.
	 * @param newNickname New nickname
	 */
	public abstract void otherChangedNickname(String oldNickname, String newNickname);
	
	/**
	 * When current user changes nickname.
	 * @param oldNickname Old nickname.
	 * @param newNickname New nickname.
	 */
	public abstract void userChangedNickname(String oldNickname, String newNickname);
	
	/**
	 * When user list is changed in a channel.
	 * @param channel Channel where the userlist was changed.
	 * @param users ArrayList of current users in the channel.
	 */
	public abstract void userListChanged(String channel, ArrayList<String> users);
	
	/**
	 * When users were opped in a channel.
	 * @param channel Name of the channel.
	 * @param nickname List of nicknames.
	 */
	public abstract void usersOpped(String channel, ArrayList<String> nicknames);
	
	/**
	 * When usesr were deopped in a channel.
	 * @param channel Name of the channel.
	 * @param nickname Nickname.
	 */
	public abstract void usersDeOpped(String channel, ArrayList<String> nicknames);
	
	/**
	 * When users were given voice in a channel.
	 * @param channel Name of the channel.
	 * @param nickname Nickname.
	 */
	public abstract void usersVoiced(String channel, ArrayList<String> nicknames);
	
	/**
	 * When users were devoiced in a channel.
	 * @param channel Name of the channel.
	 * @param nickname Nickname.
	 */
	public abstract void usersDeVoiced(String channel, ArrayList<String> nicknames);
	
	/**
	 * Returns if debug mode is enabled.
	 * @return True if debug mode is enabled, otherwise false.
	 */
	public abstract boolean isDebugEnabled();
	
	/**
	 * Sends message to "server" in debug mode.
	 * @param message Message to be sent. This message is handled inside {@link ThreadIRCReader#handleCommand(String)}.
	 */
	public abstract void debugSendMessage(String message);
}