package irc.bot.plugins;

import java.util.HashMap;

/**
 * Contains the functionality for IRCBot voting plugin.
 * NOT CURRENTLY IMPLEMENTED FULLY.
 * @author Aleksi Postari
 *
 */
@Deprecated
public class PluginVoting
{
	/** Contains the voters and their responses (true=yes, false=no) */
	private HashMap<String, Boolean> voters = new HashMap<String, Boolean>();
	/** Contains the voters and their reasons (if any) */
	private HashMap<String, String> voteReasons = new HashMap<String, String>();
	
	/** Contains the vote title for the channel. */
	private String voteTitle;
	
	/**
	 * Constructor to initialize new voting for a channel.
	 * @param title Voting title message.
	 */
	public PluginVoting(String title)
	{
		this.voteTitle = title;
	}
	
	/**
	 * Adds user to voting session.
	 * @param nickname Nickname.
	 * @param response Response.
	 * @param reason If user passed additional reason message.
	 * @return Returns the completion / failure message for the operation.
	 */
	public String addUserToVoting(String nickname, boolean response, String reason)
	{
		String yesno = response == true ? "yes" : "no";
		voters.put(nickname, response);
		if (reason != null && !reason.trim().equals(""))
		{
			voteReasons.put(nickname, reason);
			return nickname + " voted " + yesno + " (" + reason + ").";
		}
		else
			return nickname + " voted " + yesno + ".";
	}
	
	/**
	 * Gets list of voters.
	 * @return Returns list of voters and their responses.
	 */
	public String[] getVoters()
	{
		if (voters.size() == 0) return new String[] { "There were no responses for voting '" + voteTitle + "' :(" };
		
		// Yes answers
		/*String msg = "";
		for (java.util.Map.Entry<String, Boolean> entry : voters.entrySet())
		{
			if (voteReasons.containsKey(entry.getKey()))
				returnMsg += entry.getKey() + "";
			else
				
		}*/
		// No answers
		
		return new String[] { "-" };
		//return returnMsg;
	}
	
	/**
	 * Checks if voting is currently running.
	 * @return Is voting session running
	 */
	public boolean isVotingRunning()
	{
		if (voters.size() == 0 && voteReasons.size() == 0 && voteTitle.equals(""))
			return false;
		else
			return true;
	}
	
	/**
	 * Stops the current voting session.
	 * @return Returns the completion / failure message for the operation.
	 */
	public String[] stopVoting()
	{
		if (isVotingRunning())
			return new String[] { "Voting is currently running. Use command votestop to stop the current voting session." };
		else
		{
			voters.clear();
			voteReasons.clear();
			voteTitle = "";
			//return "Stopped voting " + voteTitle + ". Results: " + getVoters();
			return new String[] { "not implemented." };
		}
	}
}