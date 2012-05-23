package irc.msghandlers;

import irc.IRCHelper;
import irc.IRCInterface;

/**
 * Base class for all IRC Message handlers.
 * @author Aleksi Postari
 *
 */
public abstract class MsgHandler
{
	/** Reference to IRC Interface. */
	protected IRCInterface irc;
	/** Current row that will be handled. */
	protected String row = "";
	
	/**
	 * Constructor to take the reference to IRCInterface.
	 * @param irc IRCInterface.
	 */
	public MsgHandler(IRCInterface irc)
	{
		this.irc = irc;
	}
	
	/**
	 * To handle the line that server sent.<br>
	 * Remember to store the current row to member variable {@link #row}.
     * @param row Full IRC message row.
	 * @return Returns true if line was handled. Otherwise false.
	 */
	public abstract boolean handleLine(String ircRow);
	
    /**
     * Checks the command that server sent to client.<br>
     * Remember to store the IRC row to row variable.
     * @param message Command that should match the command sent from server. This is not case sensitive.
     * @return Returns true if command was same that was passed. Otherwise false.
     */
    protected boolean checkCommand(String command)
    {
    	String getCommand = IRCHelper.getStdCommand(row);
    	if (getCommand == null) return false;
    	
    	if (command.equalsIgnoreCase(getCommand)) return true;
    	else return false;
    }
}
