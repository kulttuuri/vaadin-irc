package irc;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * IRC Channel template.
 * @author Aleksi Postari
 *
 */
public class IRCChannel implements Serializable
{
	private String channelName;
	private String networkName;
	//private ArrayList<String> channelMessages;
	
	public IRCChannel(String channelName, String networkName)
	{
		this.channelName = channelName;
		this.networkName = networkName;
	}
	
	public String getChannelName() { return channelName; }
	public String getNetworkName() { return networkName; }
	//public ArrayList<String> getChannelMessages() { return channelMessages; }
}
