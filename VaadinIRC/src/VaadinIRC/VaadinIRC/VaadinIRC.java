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

package VaadinIRC.VaadinIRC;

import irc.IRC;
import irc.IRCSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.vaadin.artur.icepush.ICEPush;
import VaadinIRC.settings;
import VaadinIRC.GUI.GUIWindowChangeNickname;
import VaadinIRC.GUI.GUIWindowSettings;
import VaadinIRC.GUI.VaadinIrcGUI;
import VaadinIRC.GUI.channelGUI;
import VaadinIRC.exceptions.ChannelNotFoundException;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
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
	private HashMap<String, channelGUI> channelMap = new HashMap<String, channelGUI>();
	/** Thread where the IRC is running */
	private IRC irc;
	/** Reference to implemented IRCGuiInterface */
	VaIRCInterface ircInterface = new VaIRCInterface();
	/** Reference to Vaadin main application window */
	private Window window;
	/** Settings window */
	private GUIWindowSettings windowSettings;
	/** Change nickname window */
	private GUIWindowChangeNickname windowNickname;
	/** IRC Session information. */
	private IRCSession session = new IRCSession();
	/** Addon component for Vaadin that allows to force push changes from server to client. */
	private ICEPush pusher;
	
	/**
	 * Constructor to start the VaadinIRC application.
	 * @param window Reference to main application window.
	 * @param application Reference to Application.
	 */
	public VaadinIRC(Window window, Application application)
	{
		// Call super implementation (VaadinIrcGUI) to create the irc view and to store reference to main application window
		super(window);
		this.window = window;
		// Initialize VaadinIRC
		init(window);
		if (settings.debug) debug(session);
		else showSettingsWindow();
	}
	
	/** Used for debug purposes. */
	public void debug(IRCSession session)
	{
		session.setNickname("oma");
		ircInterface.debugSendMessage(":oma!~VaIRCUser@a91-152-121-162.elisa-laajakaista.fi JOIN #testikannu12345");
		ircInterface.debugSendMessage(":port80a.se.quakenet.org 332 VaAle #testikannu12345 :TIKOLAN4 8.6 - 10.6. Tiedepuistolla! Taavi, Pessu, Poppis, Z0a, EImo, VuNe");
		ArrayList<String> users = new ArrayList<String>();
		users.add("@Aleksi");
		users.add("+Testaaja");
		users.add("+vairc");
		for (int i = 0; i < 5; i++) users.add("oma" + i);
		users.add("user");
		ircInterface.userListChanged("#testikannu12345", users);
		ircInterface.debugSendMessage(":Kulttuuri!u4267@irccloud.com MODE #testikannu12345 +ooooo oma1 oma2 oma3 oma4 oma5");
		ircInterface.debugSendMessage(":Kulttuuri!u4267@irccloud.com PRIVMSG #testikannu12345 :moikka kaikki!");
	}
	
	/**
	 * Refreshes the topbar text.
	 */
	public void refreshTopbarText()
	{
		if (ircInterface == null || !ircInterface.isConnectionRunning())
			topLabel.setValue("<div style='float: right; background-color: #BDE3F6; width: 40%; border: 1px solid black;'><img src='./VAADIN/themes/VaIRCTheme/images/server_delete.png'/> Not connected to any network.</div>");
		else
		{
			String server = session.getServer() + ":" + session.getServerPort();
			String nick = session.getNickname();
			topLabel.setValue("<div style='float: right; background-color: #BDE3F6; width: 40%; border: 1px solid black;'><img src='./VAADIN/themes/VaIRCTheme/images/server_go.png'/> Connected to: " + server + " with nickname: " + nick + "</div>");
		}
	}
	
	/**
	 * Sets connecting message to topbar text.
	 */
	public void setTopbarTextStatusConnecting()
	{
		topLabel.setValue("<div style='float: right; background-color: #BDE3F6; width: 40%; border: 1px solid black;'><img src='./VAADIN/themes/VaIRCTheme/images/server_go.png'/> Connecting to network...</div>");
	}
	
	/**
	 * Gets channel from channel map.
	 * @param channel Name of the channel (With starting #)
	 * @return Returns the channelGUI object of the given channel.
	 * @throws ChannelNotFoundException If channel was not found, this gets thrown.
	 */
	public channelGUI getChannel(String channel) throws ChannelNotFoundException
	{
		if (!channelMap.containsKey(channel)) throw new ChannelNotFoundException(channel);
		else return channelMap.get(channel);
	}
	
	/**
	 * Returns the status channel.
	 * @return Returns the channelGUI object of the given channel.
	 * @throws ChannelNotFoundException If status channel was not found, this gets thrown.
	 */
	public channelGUI getStatusChannel() throws ChannelNotFoundException
	{
		return getChannel("status");
	}
	
	/**
	 * To check if user is already on this map.
	 * @param channel Name of the channel.
	 * @return Returns true if user is already on the given channel, otherwise false.
	 */
	public boolean containsChannel(String channel)
	{
		if (channelMap.containsKey(channel)) return true;
		else return false;
	}
	
	/**
	 * Changes given user's nickname.<br>
	 * If old nickname was same as current user's nickname, current user's
	 * nickname will also be changed to the new one.
	 * @param oldNickname Old nickname.
	 * @param newNickname New nickname.
	 */
	public void changeUserNickname(String oldNickname, String newNickname)
	{
		if (oldNickname.equals(session.getNickname())) session.setNickname(newNickname);
		
		for (Entry<String, channelGUI> channel : channelMap.entrySet())
			channelMap.get(channel.getKey()).changeUserNickname(oldNickname, newNickname);
	}
	
	/**
	 * Removes user from channels.<br>
	 * Can be used in conjuction with for ex. when other user quit from network.
	 * @param nickname User's nickname.
	 * @param reason Quit reason.
	 * @param announceReason Announce reason?
	 */
	public void removeUserFromChannels(String nickname, String reason, boolean announceReason)
	{
		for (Entry<String, channelGUI> channel : channelMap.entrySet())
			channelMap.get(channel.getKey()).removeUserFromChannel(nickname, reason, announceReason);
	}
	
	/**
	 * Shows the nickname change window.
	 */
	public void showNicknameChangeWindow()
	{
		new GUIWindowChangeNickname(window, session, ircInterface);
	}
	
	/**
	 * Shows the settings window.
	 */
	public void showSettingsWindow()
	{
		new GUIWindowSettings(window, session, ircInterface);
	}
	
	/**
	 * Initializes VaadinIRC.
	 * @param window Main application window.
	 */
	private void init(Window window)
	{
		// Initialize icePush addon & add it to main layout.
		pusher = new ICEPush();
			mainLayout.addComponent(pusher);
			mainLayout.setExpandRatio(pusher, 0.0f);
		// Setup IRC class & IRC Interface
		ircInterface.setVaadinIRC(this);
		irc = new IRC(ircInterface, session);
		ircInterface.setIRC(irc);
		// Create status channel.
		createChannel("status");
		// Add actionListener to channel tabs to listen for tab changes
		channelTabs.addListener(this);
	}
	
	/**
	 * Forces server to send the latest information to client using the
	 * ICEPush addon for Vaadin.
	 */
	public void pushChangesToClient()
	{
		if (ICEPush.getPushContext(pusher.getApplication().getContext()) != null)
			pusher.push();
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
	 * Starts new private conversation window with given nickname.<br>
	 * Converts nickname always to lower case format (IRC network is not case sensitive with nicknames).
	 * @param nickname Target nickname.
	 */
	public void createPrivateConversation(String nickname)
	{
		nickname = nickname.toLowerCase().trim();
		if (channelMap.containsKey(nickname)) return;
		
		// Add channel to list of channel maps and create new Tab to TabSheet out of it & select the newly created tab.
		channelMap.put(nickname, new channelGUI(nickname, this, ircInterface));
		channelTabs.addTab(channelMap.get(nickname).getChannelGUI(), nickname);
		channelTabs.setSelectedTab(channelMap.get(nickname).getChannelGUI());
		getTab(nickname).setClosable(true);
		// Set icon for the channel
		removeChannelActivity(nickname);
		channelMap.get(nickname).addMessageToChannelTextarea("Started private conversation with " + nickname + ".");
	}
	
	/**
	 * Creates new IRC Channel with given name.
	 * Basically just adds the channel to list of channels and to TabSheet.
	 * @param channelName Name of the channel.
	 */
	public void createChannel(String channelName)
	{
		channelName = channelName.trim();
		if (!channelName.startsWith("#") && !channelName.equals("status")) channelName = "#" + channelName;
		if (channelMap.containsKey(channelName)) return;

		// Add channel to list of channel maps and create new Tab to TabSheet out of it & select the newly created tab.
		channelMap.put(channelName, new channelGUI(channelName, this, ircInterface));
		channelTabs.addTab(channelMap.get(channelName).getChannelGUI(), channelName);
		channelTabs.setSelectedTab(channelMap.get(channelName).getChannelGUI());
		// Set icon for the channel
		removeChannelActivity(channelName);
		
		if (!channelName.equals("status"))
		{
			channelMap.get(channelName).addMessageToChannelTextarea("Joined channel " + channelName + ".");
			getTab(channelName).setClosable(true);
		}
	}
	
	/**
	 * Removes all tabs except status channel tab.
	 */
	public void removeAllServerTabs()
	{
		// Iterate through all channels
		ArrayList<String> channels = new ArrayList<String>();
		Collection c = channelMap.values(); Iterator itr = c.iterator();
		while (itr.hasNext()) channels.add(((channelGUI)itr.next()).getChannelName());
		// Remove all channels except status channel
		for (String channel : channels)
		{
			if (!channel.equals("status"))
			{
				if (getTab(channel) == null) continue;
				channelTabs.removeTab(getTab(channel));
				channelMap.remove(channel);
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
	 * @param event This parameter is not used.
	 */
	public void selectedTabChange(SelectedTabChangeEvent event)
	{
		removeChannelActivity(channelTabs.getSelectedTab().getCaption());
	}

	@Override
	public void onTabClose(TabSheet tabsheet, Component tabContent)
	{
		String name = tabContent.getCaption();
		if (name.startsWith("#"))
		{
			ircInterface.sendMessageToServer("/PART " + name);
			ircInterface.leftChannel(name, session.getServer());
			ircInterface.pushChangesToClient();
		}
		else
			removeChannel(name);
	}
}