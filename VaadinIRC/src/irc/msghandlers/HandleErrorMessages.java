package irc.msghandlers;

import java.util.HashMap;
import java.util.Map;

import irc.IRC;
import irc.IRCEnums;
import irc.IRCHelper;
import irc.IRCInterface;

/**
 * Handles IRC error replies (range 400-599)
 * @author Kulttuuri
 *
 */
public class HandleErrorMessages extends MsgHandler
{
	/** Contains map of all error replies. */
	private static HashMap<String, String> errorReplies = new HashMap<String, String>();
	
	/**
	 * Fills all the required error replies to {@link #errorReplies}.
	 */
	private static void createErrorReplies()
	{
		errorReplies.put("401", "ERR_NOSUCHNICK");
		errorReplies.put("402", "ERR_NOSUCHSERVER");
		errorReplies.put("403", "ERR_NOSUCHCHANNEL");
		errorReplies.put("404", "ERR_CANNOTSENDTOCHAN");
		errorReplies.put("405", "ERR_TOOMANYCHANNELS");
		errorReplies.put("406", "ERR_WASNOSUCHNICK");
		errorReplies.put("407", "ERR_TOOMANYTARGETS");
		errorReplies.put("409", "ERR_NOORIGIN");
		errorReplies.put("411", "ERR_NORECIPIENT");
		errorReplies.put("412", "ERR_NOTEXTTOSEND");
		errorReplies.put("413", "ERR_NOTOPLEVEL");
		errorReplies.put("414", "ERR_WILDTOPLEVEL");
		errorReplies.put("421", "ERR_UNKNOWNCOMMAND");
		errorReplies.put("422", "ERR_NOMOTD");
		errorReplies.put("423", "ERR_NOADMININFO");
		errorReplies.put("424", "ERR_FILEERROR");
		errorReplies.put("431", "ERR_NONICKNAMEGIVEN");
		errorReplies.put("432", "ERR_ERRONEUSNICKNAME");
		errorReplies.put("433", "ERR_NICKNAMEINUSE");
		errorReplies.put("436", "ERR_NICKCOLLISION");
		errorReplies.put("441", "ERR_USERNOTINCHANNEL");
		errorReplies.put("442", "ERR_NOTONCHANNEL");
		errorReplies.put("443", "ERR_USERONCHANNEL");
		errorReplies.put("444", "ERR_NOLOGIN");
		errorReplies.put("445", "ERR_SUMMONDISABLED");
		errorReplies.put("446", "ERR_USERSDISABLED");
		errorReplies.put("451", "ERR_NOTREGISTERED");
		errorReplies.put("461", "ERR_NEEDMOREPARAMS");
		errorReplies.put("462", "ERR_ALREADYREGISTRED");
		errorReplies.put("463", "ERR_NOPERMFORHOST");
		errorReplies.put("464", "ERR_PASSWDMISMATCH");
		errorReplies.put("465", "ERR_YOUREBANNEDCREEP");
		errorReplies.put("467", "ERR_KEYSET");
		errorReplies.put("471", "ERR_CHANNELISFULL");
		errorReplies.put("472", "ERR_UNKNOWNMODE");
		errorReplies.put("473", "ERR_INVITEONLYCHAN");
		errorReplies.put("474", "ERR_BANNEDFROMCHAN");
		errorReplies.put("475", "ERR_BADCHANNELKEY");
		errorReplies.put("481", "ERR_NOPRIVILEGES");
		errorReplies.put("482", "ERR_CHANOPRIVSNEEDED");
		errorReplies.put("483", "ERR_CANTKILLSERVER");
		errorReplies.put("491", "ERR_NOOPERHOST");
		errorReplies.put("501", "ERR_UMODEUNKNOWNFLAG");
		errorReplies.put("502", "ERR_USERSDONTMATCH");
	}
	
	public HandleErrorMessages(IRCInterface irc)
	{
		super(irc);
		createErrorReplies();
	}
	
	@Override
    public boolean handleLine(String ircRow, IRC ircApp)
    {
		this.row = ircRow;
		String command = IRCHelper.getStdCommand(row);
		
		// You're not channel operator
		if (command.equals("482"))
		{
			irc.receivedNewMessage(IRCHelper.splitCommandsToList(row, " ").get(2), IRCHelper.getStdReason(row), IRCHelper.splitCommandsToList(row, " ").get(3));
			return true;
		}
		// No text to send
		else if (command.equals("412"))
		{
			irc.receivedNewMessage(IRCHelper.splitCommandsToList(row, " ").get(2), IRCHelper.getStdReason(row), IRCHelper.splitCommandsToList(row, " ").get(3));
			return true;
		}
		else if (errorReplies.containsKey(command))
		{
			irc.receivedErrorMessage(row, IRCHelper.getStdCommand(row), IRCHelper.getContentFromStdMessage(row));
			return true;
		}
		return false;
    }
	
	/**
	 * Returns the error message for given IRC row.<br>
	 * Calls {@link IRCHelper#getContentFromStdMessage(String)}
	 * @param row Full IRC message.
	 * @return Returns the parsed message.
	 */
	public String getMsg(String row)
	{
		return IRCHelper.getContentFromStdMessage(row);
	}
}