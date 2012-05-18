package irc.msghandlers;

import java.util.HashMap;
import java.util.Map;

import irc.IRCEnums;
import irc.IRCHelper;
import irc.IRCInterface;

/**
 * Handles IRC error replies (range 400-599)
 * @author Kulttuuri
 *
 */
public class HandleIrcErrorReplies extends MsgHandler
{
	/** Contains map of all error messages. */
	private HashMap<Integer, String> errorMap = new HashMap<Integer, String>();
	
	/**
	 * Fills all the required error replies to {@link #errorMap}.
	 */
	private void createErrors()
	{
		errorMap.put(401, "ERR_NOSUCHNICK");
		errorMap.put(402, "ERR_NOSUCHSERVER");
		errorMap.put(403, "ERR_NOSUCHCHANNEL");
		errorMap.put(404, "ERR_CANNOTSENDTOCHAN");
		errorMap.put(405, "ERR_TOOMANYCHANNELS");
		errorMap.put(406, "ERR_WASNOSUCHNICK");
		errorMap.put(407, "ERR_TOOMANYTARGETS");
		errorMap.put(409, "ERR_NOORIGIN");
		errorMap.put(411, "ERR_NORECIPIENT");
		errorMap.put(412, "ERR_NOTEXTTOSEND");
		errorMap.put(413, "ERR_NOTOPLEVEL");
		errorMap.put(414, "ERR_WILDTOPLEVEL");
		errorMap.put(421, "ERR_UNKNOWNCOMMAND");
		errorMap.put(422, "ERR_NOMOTD");
		errorMap.put(423, "ERR_NOADMININFO");
		errorMap.put(424, "ERR_FILEERROR");
		errorMap.put(431, "ERR_NONICKNAMEGIVEN");
		errorMap.put(432, "ERR_ERRONEUSNICKNAME");
		errorMap.put(433, "ERR_NICKNAMEINUSE");
		errorMap.put(436, "ERR_NICKCOLLISION");
		errorMap.put(441, "ERR_USERNOTINCHANNEL");
		errorMap.put(442, "ERR_NOTONCHANNEL");
		errorMap.put(443, "ERR_USERONCHANNEL");
		errorMap.put(444, "ERR_NOLOGIN");
		errorMap.put(445, "ERR_SUMMONDISABLED");
		errorMap.put(446, "ERR_USERSDISABLED");
		errorMap.put(451, "ERR_NOTREGISTERED");
		errorMap.put(461, "ERR_NEEDMOREPARAMS");
		errorMap.put(462, "ERR_ALREADYREGISTRED");
		errorMap.put(463, "ERR_NOPERMFORHOST");
		errorMap.put(464, "ERR_PASSWDMISMATCH");
		errorMap.put(465, "ERR_YOUREBANNEDCREEP");
		errorMap.put(467, "ERR_KEYSET");
		errorMap.put(471, "ERR_CHANNELISFULL");
		errorMap.put(472, "ERR_UNKNOWNMODE");
		errorMap.put(473, "ERR_INVITEONLYCHAN");
		errorMap.put(474, "ERR_BANNEDFROMCHAN");
		errorMap.put(475, "ERR_BADCHANNELKEY");
		errorMap.put(481, "ERR_NOPRIVILEGES");
		errorMap.put(482, "ERR_CHANOPRIVSNEEDED");
		errorMap.put(483, "ERR_CANTKILLSERVER");
		errorMap.put(491, "ERR_NOOPERHOST");
		errorMap.put(501, "ERR_UMODEUNKNOWNFLAG");
		errorMap.put(502, "ERR_USERSDONTMATCH");
	}
	
	public HandleIrcErrorReplies(IRCInterface irc)
	{
		super(irc);
		createErrors();
	}
	
	@Override
    public boolean handleLine(String row)
    {
		// Go through all errors and check if line matches error line.
		for (Map.Entry<Integer, String> entry : errorMap.entrySet())
		{
			if (row.indexOf(entry.getKey()) >= 0)
			{
				passError(row, entry.getValue());
				return true;
			}
		}
        return false;
    }
	
	/**
	 * Passes parsed error message to IRCInterface.
	 * @param row Full irc message.
	 * @param errorName Error name.
	 */
	private void passError(String row, String errorName)
	{
		irc.receivedErrorMessage(getMsg(errorName + ": " + row));
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