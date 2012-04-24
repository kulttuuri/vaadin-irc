package VaadinIRC.VaadinIRC;

import irc.IRC;
import irc.IRCSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import VaadinIRC.GUI.VaadinIrcGUI;
import VaadinIRC.GUI.channelGUI;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window;

/**
 * Main VaadinIRC Class.
 * @author Aleksi Postari
 *
 */
public class VaadinIRC extends VaadinIrcGUI implements SelectedTabChangeListener
{
	/** List of current user channels */
	protected HashMap<String, channelGUI> channelMap = new HashMap<String, channelGUI>();
	/** Thread where the IRC is running */
	private IRC irc;
	/** Reference to application */
	private Application app;
	/** Reference to implemented IRCGuiInterface */
	VaIRCInterface ircInterface = new VaIRCInterface();
	
	public VaadinIRC(Window window, IRCSession session, Application application)
	{
		// Call super implementation (VaadinIrcGUI) to create the irc view and to store reference to main application window
		super(window);
		
		// Setup IRC class & IRC Interface
		ircInterface.setVaadinIRC(this);
		ircInterface.initICEPush(window);
		irc = new IRC(ircInterface, session);
		ircInterface.setIRC(irc);
		// Create status channel.
		createChannel("status", session.getServer());
		channelTabs.addListener(this);
		
		if (debug)
			debug(session);
		else
			ircInterface.connectToServer(session);
	}
	
	private boolean debug = true;
	public void debug(IRCSession session)
	{
		createChannel("#tone", "");
		ArrayList<String> users = new ArrayList<String>();
		users.add("@Kulttuuri");
		ircInterface.userListChanged("#tone", users );
	}
	
	/**
	 * Removes bell icon from given channel and sets the default icon to it.
	 * @param channelName Name of the channel.
	 */
	public void removeChannelActivity(String channelName)
	{
		Tab tab = getTab(channelName);
		if (channelName.equals("status"))
			getTab(channelName).setIcon(new ThemeResource("images/application.png"));
		else
			getTab(channelName).setIcon(new ThemeResource("images/application_side_boxes.png"));
	}
	
	/**
	 * Sets bell icon for given channel if that channel is not selected for indicating that
	 * there is new activity in that channel. 
	 * @param channelName Name of the channel.
	 */
	public void setChannelNewActivity(String channelName)
	{
		Tab tab = getTab(channelName);
		// If tab is selected, just return
		if (channelTabs.getSelectedTab().getCaption().equals(tab.getCaption())) return;
		// Otherwise set bell icon to channel
		if (tab != null) getTab(channelName).setIcon(new ThemeResource("images/bell.png"));
	}
	
	/**
	 * Creates new IRC Channel with given name.
	 * @param channelName Name of the channel.
	 * @param networkName Name of the network.
	 */
	public void createChannel(String channelName, String networkName)
	{
		if (!channelName.startsWith("#") && !channelName.equals("status")) channelName = "#" + channelName;
		
		// Add channel to list of channel maps and to tab & select the newly created tab.
		channelMap.put(channelName, new channelGUI(channelName, networkName, ircInterface));
		channelTabs.addTab(channelMap.get(channelName).getChannelGUI(), channelName);
		channelTabs.setSelectedTab(channelMap.get(channelName).getChannelGUI());
		// Set icon for the channel
		removeChannelActivity(channelName);
		
		if (!channelName.equals("status"))
		channelMap.get(channelName).addMessageToChannelTextarea("Joined channel " + channelName + ".");
	}
	
	/**
	 * Removes all tabs except status channel tab.
	 */
	public void removeAllServerTabs()
	{
		// Iterate through all channels
		ArrayList<String> channels = new ArrayList<String>();
		Collection c = channelMap.values(); Iterator itr = c.iterator();
		while (itr.hasNext())
		{
			channels.add(((channelGUI)itr.next()).getChannelName());
		}
		// Remove all channels except status channel
		for (String channel : channels)
		{
			if (!channel.equals("status"))
			{
				channelTabs.removeTab(getTab(channel));
				if (channels.remove(channel)) System.out.println("removed: " + channel);
			}
		}
	}
	
	/**
	 * Iterates through channel tabs and returns the tab with given name.
	 * If tab was not found, will return null.
	 * @param tabName Name of the tab what you wish to retrieve.
	 * @return Returns the tab with given name. If tab was not found, returns null.
	 */
	private Tab getTab(String tabName)
	{
		Component delTabC = null; Iterator<Component> i = channelTabs.getComponentIterator(); Tab tab;
		while (i.hasNext())
		{
		    Component c = (Component)i.next();
		    tab = channelTabs.getTab(c);
		    // Store found tab
		    if (tabName.equals(tab.getCaption())) return channelTabs.getTab(c);
		}
		return null;
	}
	
	/**
	 * Removes a channel from the list of channels and from the tabs.
	 * @param name Name of the channel to be removed.
	 */
	public void removeChannel(String name)
	{
		// Delete tab from list of channels
		channelMap.remove(name);
		// Delete found tab from list of tabs
		if (getTab(name) != null) channelTabs.removeTab(getTab(name));
	}

	/**
	 * When tab is selected, that tab icon is set back to default.
	 * @param event Not used.
	 */
	public void selectedTabChange(SelectedTabChangeEvent event)
	{
		removeChannelActivity(channelTabs.getSelectedTab().getCaption());
	}
}