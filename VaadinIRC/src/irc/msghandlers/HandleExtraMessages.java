package irc.msghandlers;

import static irc.IRCHelper.getChannelFromStdMessage;
import static irc.IRCHelper.getContentFromStdMessage;
import static irc.IRCHelper.getModeTargetUsers;
import static irc.IRCHelper.getNicknameFromStdMessage;
import static irc.IRCHelper.getStdReason;
import static irc.IRCHelper.splitCommandsToList;
import static irc.IRCHelper.splitMessageAfterRow;
import irc.IRC;
import irc.IRCHelper;
import irc.IRCInterface;
import irc.IRCSession;
import irc.exceptions.TerminateConnectionException;
import java.util.ArrayList;

/**
 * To handle extra IRC messages without any numeric codes.<br>
 * For example: PRIVMSG, TOPIC, PING.
 * @author Aleksi Postari
 *
 */
public class HandleExtraMessages extends MsgHandler
{
	/** IRC Session information. */
	private IRCSession session;
	
	public HandleExtraMessages(IRCInterface irc, IRCSession session)
	{
		super(irc);
		this.session = session;
	}

	@Override
	public boolean handleLine(String ircRow, IRC ircApp) throws TerminateConnectionException
	{
		this.row = ircRow;
		
    	// Current message splitted with spaces
    	ArrayList<String> rowSpaces = new ArrayList<String>();
    	rowSpaces = IRCHelper.splitCommandsToList(row, " ");
    	
		// PING
    	if (row.startsWith("PING"))
    	{
    		ircApp.handlePingResponse(row);
    	}
		// NOTICE
		if (row.startsWith("NOTICE"))
		{
			irc.receivedStatusMessage(splitCommandsToList(row, " ").get(1) + " " + splitMessageAfterRow(row, " ", 3));
		}
        // Join
        else if (checkCommand("JOIN"))
        {
        	if (getNicknameFromStdMessage(row).equals(session.getNickname()))
        		irc.joinedChannel(getChannelFromStdMessage(row), session.getServer());
        	else
        		irc.otherJoinedChannel(getChannelFromStdMessage(row), session.getServer(), getNicknameFromStdMessage(row));
        	// Wait so that channel will have time to get generated
        	try { Thread.sleep(1000); } catch (InterruptedException e) { }
        }
        // PRIVMSG (Channel / private message)
        else if (!row.startsWith(":" + session.getNickname()) && checkCommand("PRIVMSG"))
        {
        	// Channel message
        	if (getChannelFromStdMessage(row).startsWith("#"))
        	{
        		if (!getNicknameFromStdMessage(row).equals(session.getNickname()))
        		{
        			irc.receivedNewMessage(getNicknameFromStdMessage(row), getContentFromStdMessage(row), getChannelFromStdMessage(row));
        			ircApp.sendMessageToBot(row);
        		}
        	}
        	// Private message
        	else
        	{
        		irc.receivedNewPrivateMessage(getNicknameFromStdMessage(row), getStdReason(row));
        	}
        }
        // Part (:VaAle101!~null@a91-152-121-162.elisa-laajakaista.fi PART #testikannu12345 :reason)
        else if (checkCommand("PART"))
        {
        	if (getNicknameFromStdMessage(row).equals(session.getNickname()))
        		irc.leftChannel(getChannelFromStdMessage(row), session.getServer());
        	else
        		irc.otherLeftChannel(getChannelFromStdMessage(row), session.getServer(), getNicknameFromStdMessage(row));
        }
        // Kick (:Kulttuuri!u4267@irccloud.com KICK #testikannu12345 VaAle101 :reason)
        else if (checkCommand("KICK"))
        {
        	if (rowSpaces.get(3).equals(session.getNickname()))
        		irc.kickedFromChannel(getChannelFromStdMessage(row), session.getServer(), getStdReason(row));
        	else
        		irc.otherKickedFromChannel(getChannelFromStdMessage(row), session.getServer(), rowSpaces.get(3), getStdReason(row));
        }
        // Nick change (:ASDQWEASD!~null@a91-152-121-162.elisa-laajakaista.fi NICK :testaaja)
        else if (checkCommand("NICK"))
        {
        	if (getNicknameFromStdMessage(row).equals(session.getNickname()))
        		irc.userChangedNickname(getNicknameFromStdMessage(row), getContentFromStdMessage(row));
        	else
        		irc.otherChangedNickname(getNicknameFromStdMessage(row), getContentFromStdMessage(row));
        }
        // Mode (:Kulttuuri!u4267@irccloud.com MODE #testikannu12345 +bbb VaAle101!*@* reason!*@* viesti!*@*)
        else if (checkCommand("MODE"))
        {
        	String mode = rowSpaces.get(3);
        	String channel = rowSpaces.get(2);
        	if (mode.startsWith("+o"))
        		irc.usersOpped(channel, getModeTargetUsers(row));
        	if (mode.startsWith("-o"))
        		irc.usersDeOpped(channel, getModeTargetUsers(row));
        	if (mode.startsWith("+v"))
        		irc.usersVoiced(channel, getModeTargetUsers(row));
        	if (mode.startsWith("-v"))
        		irc.usersDeVoiced(channel, getModeTargetUsers(row));
        }
        // TOPIC (:Kulttuuri!u4267@irccloud.com TOPIC #testikannu12345 :asd)
        else if (checkCommand("TOPIC"))
        {
        	irc.setChannelTopic(getChannelFromStdMessage(row), getStdReason(row), getNicknameFromStdMessage(row), true);
        }
		// QUIT  (:Kenkae!~kenkae@OsmosKosmos.users.quakenet.org QUIT :Read error: Operation timed out)
        else if (checkCommand("QUIT"))
        {
        	if (getNicknameFromStdMessage(row).equals(session.getNickname()))
        	{
        		irc.quitNetwork(session.getServer(), getStdReason(row));
        		throw new TerminateConnectionException(getStdReason(row));
        	}
        	else
        		irc.otherQuitNetwork(getNicknameFromStdMessage(row), session.getServer(), getStdReason(row));
        }
		// ERROR
        else if (row.startsWith("ERROR"))
        {
        	String reason = row.replace("ERROR :", "");
        	irc.quitNetwork(session.getServer(), reason);
        	throw new TerminateConnectionException(reason);
        }
		// AUTO-AWAY (DEBUG: did read line: :port80a.se.quakenet.org 301 joumatamou Kulttuuri :Auto-away)
        else if (checkCommand("301"))
        {
        	irc.receivedNewPrivateMessage(rowSpaces.get(3), getStdReason(row));
        }
		// If command was not handled, return false.
		else
		{
			return false;
		}
		// Otherwise return true.
		return true;
	}
}