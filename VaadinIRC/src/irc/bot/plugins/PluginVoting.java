/**
 * Copyright (C) 2012 Aleksi Postari
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package irc.bot.plugins;

import java.util.HashMap;

/**
 * Contains the functionality for IRCBot voting plugin.
 * NOT CURRENTLY IMPLEMENTED FULLY.
 * @author Aleksi Postari (@kulttuuri)
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