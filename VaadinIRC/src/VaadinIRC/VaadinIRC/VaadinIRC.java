package VaadinIRC.VaadinIRC;

import irc.IRC;
import irc.IRCSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.htmlunit.corejs.javascript.tools.debugger.GuiCallback;

import org.vaadin.artur.icepush.ICEPush;

import VaadinIRC.GUI.VaadinIrcGUI;
import VaadinIRC.GUI.channelGUI;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Sizeable;
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
	/** Reference to implemented IRCGuiInterface */
	VaIRCInterface ircInterface = new VaIRCInterface();
	/** Addon component for Vaadin that allows to force push changes from server to client. */
	private ICEPush pusher;
	
	public VaadinIRC(Window window, IRCSession session, Application application, ICEPush pusher)
	{
		// Call super implementation (VaadinIrcGUI) to create the irc view and to store reference to main application window
		super(window);
		// Store ICEPush reference
		this.pusher = pusher;
		// Initialize VaadinIRC
		init(window, session);
		// Connect to server
		if (debug) debug(session);
		else ircInterface.connectToServer(session);
	}
	
	private boolean debug = true;
	public void debug(IRCSession session)
	{
		createChannel("#tone", "");
		ArrayList<String> users = new ArrayList<String>();
		users.add("@Aleksi");
		users.add("+Testaaja");
		users.add("+vairc");
		users.add("normal");
		users.add("user");
		ircInterface.userListChanged("#tone", users);
	}
	
	/**
	 * Initializes VaadinIRC.
	 */
	private void init(Window window, IRCSession session)
	{
		// Setup IRC class & IRC Interface
		ircInterface.setVaadinIRC(this);
		irc = new IRC(ircInterface, session);
		ircInterface.setIRC(irc);
		// Create status channel.
		createChannel("status", session.getServer());
		// Add actionListener to channel tabs to listen for tab changes
		channelTabs.addListener(this);
	}
	
	/**
	 * Forces server to send the latest information to client using the
	 * ICEPush addon for Vaadin.
	 */
	public void pushChangesToClient()
	{
		if (ICEPush.getPushContext(pusher.getApplication().getContext()) != null) pusher.push();
	}
	
	/**
	 * Removes bell icon from given channel and sets the default icon to it.
	 * @param channelName Name of the channel.
	 */
	public void removeChannelActivity(String channelName)
	{
		if (channelName.equals("status"))
			getTab(channelName).setIcon(new ThemeResource("images/application.png"));
		else
			getTab(channelName).setIcon(new ThemeResource("images/application_side_boxes.png"));
	}
	
	/**
	 * Returns the selected channel tab name.
	 * @return Selected channel tab name.
	 */
	public String getSelectedChannelName()
	{
		return channelTabs.getSelectedTab().getCaption();
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
	 * Basically just adds the channel to list of channels and to TabSheet.
	 * @param channelName Name of the channel.
	 * @param networkName Name of the network.
	 */
	public void createChannel(String channelName, String networkName)
	{
		if (!channelName.startsWith("#") && !channelName.equals("status")) channelName = "#" + channelName;

		// Add channel to list of channel maps and create new Tab to TabSheet out of it & select the newly created tab.
		channelMap.put(channelName, new channelGUI(channelName, networkName, ircInterface));
		channelTabs.addTab(channelMap.get(channelName).getChannelGUI(), channelName);
		channelTabs.setSelectedTab(channelMap.get(channelName).getChannelGUI());
		// Set icon for the channel
		removeChannelActivity(channelName);
		
		if (!channelName.equals("status")) channelMap.get(channelName).addMessageToChannelTextarea("Joined channel " + channelName + ".");
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