package VaadinIRC.VaadinIRC;

import irc.IRC;
import irc.IRCSession;
import java.util.HashMap;
import VaadinIRC.GUI.VaadinIrcGUI;
import VaadinIRC.GUI.channelGUI;
import com.vaadin.ui.Window;

/**
 * Main VaadinIRC Class.
 * Handles all the functionality for IRC View.
 * @author Aleksi Postari
 *
 */
public class VaadinIRC extends VaadinIrcGUI
{
	/** List of current user channels */
	protected HashMap<String, channelGUI> channelMap = new HashMap<String, channelGUI>();
	/** Thread where the IRC is running */
	private IRC irc;
	/** Reference to implemented IRCGuiInterface */
	VaIRCInterface ircInterface = new VaIRCInterface();
	
	public VaadinIRC(Window window, IRCSession session)
	{
		// Call super implementation (VaadinIrcGUI) to create the irc view and to store reference to main application window
		super(window);
		
		// Setup IRC class & IRC Interface
		ircInterface.setVaadinIRC(this);
		irc = new IRC(ircInterface, session);
		ircInterface.setIRC(irc);
		// Create status channel.
		createChannel("status", session.getServer());
		// Initiate connection to IRC server
		ircInterface.connectToServer(session);
	}
	
	/**
	 * Creates new IRC Channel with given name.
	 * @param channelName Name of the channel.
	 * @param networkName Name of the network.
	 */
	public void createChannel(String channelName, String networkName)
	{
		if (!channelName.startsWith("#") && !channelName.equals("status")) channelName = "#" + channelName;
		channelMap.put(channelName, new channelGUI(channelName, networkName, ircInterface));
		channelTabs.addTab(channelMap.get(channelName).getChannelGUI(), channelName);
	}
	
	/**
	 * Removes a channel from the list of channels and from the tabs.
	 * @param name Name of the channel to be removed.
	 */
	public void removeChannel(String name)
	{
		// TODO: Implement.
		//channelTabs.removeTab(channelMap.get(name).getChannelGUI());
	}
}