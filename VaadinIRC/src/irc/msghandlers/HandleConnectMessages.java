package irc.msghandlers;

import java.util.HashMap;
import irc.IRCHelper;
import irc.IRCInterface;

public class HandleConnectMessages extends MsgHandler
{
	/** Contains map of all connect replies. */
	private static HashMap<String, String> connectReplies = new HashMap<String, String>();
	
	/**
	 * Fills all the required connect replies to {@link #connectReplies}.
	 */
	private static void createConnectReplies()
	{
		connectReplies.put("001", "RPL_WELCOME");
		connectReplies.put("002", "RPL_YOURHOST");
		connectReplies.put("003", "RPL_CREATED");
		connectReplies.put("004", "RPL_MYINFO");
	}
	
	public HandleConnectMessages(IRCInterface irc)
	{
		super(irc);
		createConnectReplies();
	}

	@Override
	public boolean handleLine(String ircRow)
	{
		this.row = ircRow;
		
		if (connectReplies.containsKey(IRCHelper.getRowType(row)))
		{
			irc.receivedStatusMessage(IRCHelper.splitMessageAfterRow(row, " ", 3));
			return true;
		}
		return false;
	}
}