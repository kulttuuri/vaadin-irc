/**
 * Copyright (C) 2012 Aleksi Postari (@kulttuuri, aleksi@postari.net)
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * This code is part of project Vaadin Irkkia.
 * License in short: You can use this code as you wish, but please keep this license information intach or credit the original author in redistributions.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package irc.bot.plugins;

import irc.bot.IRCBotCommands;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Contains the functionality for IRCBot voting plugin.
 * @author Aleksi Postari (@kulttuuri)
 *
 */
public class PluginVoting extends AbstractBotPlugin
{
	/** Is voting currently running? */
	private boolean isVotingRunning = false;
	/** Everyone who has voted yes. {@link irc.JavadocLibrary#ircNickname nickname}, reason (reason can be "") */
	private HashMap<String, String> yesVoters = new HashMap<String, String>();
	/** Everyone who has voted no. format. {@link irc.JavadocLibrary#ircNickname nickname}, reason (reason can be "") */
	private HashMap<String, String> noVoters = new HashMap<String, String>();
	/** Contains the vote title for the channel. */
	private String voteTitle;
	/** Voting starting time. */
	private Date voteStartTime;
	/** Voting starting {@link irc.JavadocLibrary#ircNickname nickname}. */
	private String votingStarterNickname;
	
	/**
	 * Constructor to initialize the channel voting plugin.
	 */
	public PluginVoting(String botCallSign)
	{
		this.botCallSign = botCallSign;
	}
	
	/**
	 * Adds user to voting session.
	 * @param nickname {@link irc.JavadocLibrary#ircNickname nickname}.
	 * @param response Response.
	 * @param reason If user passed additional reason message.
	 * @return Returns the completion / failure message for the operation.
	 */
	public String addUserToVoting(String nickname, boolean response, String reason)
	{
		if (!isVotingRunning)
			return "Voting is not currently running. Use command "+botCallSign+"votestart title to start new voting session.";
		
		if (reason == null || reason.trim().equals("")) reason = "";
		String reasonMsg = "";
		if (!reason.equals("")) reasonMsg = " (" + reason + ")";
		
		if (response)
		{
			if (noVoters.containsKey(nickname)) noVoters.remove(nickname);
			yesVoters.put(nickname, reason);
			//return nickname + " voted yes" + reasonMsg + ".";
			return "";
		}
		else
		{
			if (yesVoters.containsKey(nickname)) yesVoters.remove(nickname);
			noVoters.put(nickname, reason);
			//return nickname + " voted no" + reasonMsg + ".";
			return "";
		}
	}
	
	/**
	 * Gets all voters that have answered yes and possible reason.
	 * @return All voters that have answered yes.
	 */
	private String getYesVoters()
	{
		String returnMsg = "Yes voters:  ";
		for (java.util.Map.Entry<String, String> entry : yesVoters.entrySet())
		{
			if (entry.getValue().equals("")) returnMsg += entry.getKey() + ", ";
			else returnMsg += entry.getKey() + " (" + entry.getValue() + "), ";
		}
		
		if (returnMsg.equals("Yes voters:  ")) return "";
		return returnMsg.substring(0, returnMsg.length()-2);
	}
	
	/**
	 * Gets all voters that have answered no and possible reason.
	 * @return All voters that have answered no.
	 */
	private String getNoVoters()
	{
		String returnMsg = "No voters:  ";
		for (java.util.Map.Entry<String, String> entry : noVoters.entrySet())
		{
			if (entry.getValue().equals("")) returnMsg += entry.getKey() + ", ";
			else returnMsg += entry.getKey() + " (" + entry.getValue() + "), ";
		}
		
		if (returnMsg.equals("No voters:  ")) return "";
		return returnMsg.substring(0, returnMsg.length()-2);
	}
	
	/**
	 * Checks if voting is currently running.
	 * @return Is voting session running.
	 */
	public String isVotingRunning()
	{
		if (isVotingRunning)
		{
			return "Voting is currently running.";
		}
		else
		{
			return "Voting is not running. Use command "+botCallSign+"votestart title to start new voting session.";
		}
	}
	
	/**
	 * Starts new voting session.
	 * @param nickname Voting starter {@link irc.JavadocLibrary#ircNickname nickname}.
	 * @param title Voting title.
	 * @return Returns the completion / failure message for the operation.
	 */
	public String startVoting(String nickname, String title)
	{
		if (isVotingRunning) return "Voting " + voteTitle + " is already running. " + yesVoters.size() + " yes voters and " + noVoters.size() + " no voters.";
		
		isVotingRunning = true;
		voteTitle = title == null ? "" : title;
		votingStarterNickname = nickname;
		voteStartTime = new Date();
		return nickname + " started new voting: " + voteTitle + ". Type "+botCallSign+"voteyes ?reason or "+botCallSign+"voteno ?reason.";
	}
	
	/**
	 * Returns information about the current voting session.
	 * @return Returns arraylist containing lines that needs to be announced in the IRC channel.
	 */
	public ArrayList<String> getVotingInformation()
	{
		ArrayList<String> returnList = new ArrayList<String>();
		if (!isVotingRunning)
		{
			returnList.add("Voting is not currently running. Use command "+botCallSign+"votestart title to start new voting session.");
			return returnList;
		}
		
		int[] dateDistance = IRCBotCommands.getDistanceBetweenDates(voteStartTime, new Date());
		
		returnList.add("Current voting: " + voteTitle + ". Voting was started by " + votingStarterNickname + 
			" " + dateDistance[0] + " hours " + dateDistance[1] + " minutes ago. " + yesVoters.size() + " yes voters and " + noVoters.size() + " no voters. " +
			"Type "+botCallSign+"voteyes ?reason or " + botCallSign + "voteno ?reason.");
		if (!getYesVoters().equals("")) returnList.add(getYesVoters());
		if (!getNoVoters().equals("")) returnList.add(getNoVoters());
		return returnList;
	}
	
	/**
	 * Stops the current voting session.
	 * @return Returns arraylist containing lines that needs to be announced in the IRC channel.
	 */
	public ArrayList<String> stopVoting()
	{
		ArrayList<String> returnList = new ArrayList<String>();
		
		if (!isVotingRunning)
		{
			returnList.add("Voting is not currently running. Use command '"+botCallSign+"votestart title' to start new voting session.");
			return returnList;
		}
		
		int[] dateDistance = IRCBotCommands.getDistanceBetweenDates(voteStartTime, new Date());
		
		returnList.add("Stopped voting " + voteTitle + ". Voting was started by " + votingStarterNickname + 
		" " + dateDistance[0] + " hours " + dateDistance[1] + " minutes ago. " + yesVoters.size() + " yes voters and " + noVoters.size() + " no voters.");
		if (!getYesVoters().equals("")) returnList.add(getYesVoters());
		if (!getNoVoters().equals("")) returnList.add(getNoVoters());
		yesVoters.clear();
		noVoters.clear();
		voteTitle = "";
		isVotingRunning = false;
		return returnList;
	}
}