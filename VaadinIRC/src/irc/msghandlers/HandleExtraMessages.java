package irc.msghandlers;

import static irc.IRCHelper.*;
import java.util.ArrayList;
import irc.IRCHelper;
import irc.IRCInterface;
import irc.IRCSession;

/**
 * To handle extra IRC messages without any numeric codes.<br>
 * For example: PRIVMSG, TOPIC.
 * @author Kulttuuri
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
	public boolean handleLine(String ircRow)
	{
		this.row = ircRow;
		
    	// Current message splitted with spaces
    	ArrayList<String> rowSpaces = new ArrayList<String>();
    	rowSpaces = IRCHelper.splitCommandsToList(row, " ");
		
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
        // Normal channel message (PRIVMSG)
        else if (!row.startsWith(":" + session.getNickname()) && checkCommand("PRIVMSG"))
        {
        	if (!getNicknameFromStdMessage(row).equals(session.getNickname()))
        		irc.receivedNewMessage(getNicknameFromStdMessage(row), getContentFromStdMessage(row), getChannelFromStdMessage(row));
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
        	if (getNicknameFromStdMessage(row).equals(session.getNickname()))
        		irc.kickedFromChannel(getChannelFromStdMessage(row), session.getServer(), getStdReason(row));
        	else
        		irc.otherKickedFromChannel(getChannelFromStdMessage(row), session.getServer(), getNicknameFromStdMessage(row), getStdReason(row));
        }
        // Nick change (:ASDQWEASD!~null@a91-152-121-162.elisa-laajakaista.fi NICK :testaaja)
        else if (checkCommand("NICK"))
        {
        	if (getNicknameFromStdMessage(row).equals(session.getNickname()))
        		irc.userChangedNickname(getNicknameFromStdMessage(row), getContentFromStdMessage(row));
        	else
        		irc.userChangedNickname(getNicknameFromStdMessage(row), getContentFromStdMessage(row));
        }
        // Mode (:Kulttuuri!u4267@irccloud.com MODE #testikannu12345 +bbb VaAle101!*@* reason!*@* viesti!*@*)
        else if (checkCommand("MODE"))
        {
        	String mode = rowSpaces.get(3);
        	if (mode.startsWith("+o"))
        		irc.usersOpped(getChannelFromStdMessage(row), getModeTargetUsers(row));
        	if (mode.startsWith("-o"))
        		irc.usersDeOpped(getChannelFromStdMessage(row), getModeTargetUsers(row));
        	if (mode.startsWith("+v"))
        		irc.usersVoiced(getChannelFromStdMessage(row), getModeTargetUsers(row));
        	if (mode.startsWith("-v"))
        		irc.usersDeVoiced(getChannelFromStdMessage(row), getModeTargetUsers(row));
        }
        // TOPIC (:Kulttuuri!u4267@irccloud.com TOPIC #testikannu12345 :asd)
        else if (checkCommand("TOPIC"))
        {
        	irc.setChannelTopic(getChannelFromStdMessage(row), getStdReason(row), getNicknameFromStdMessage(row), true);
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