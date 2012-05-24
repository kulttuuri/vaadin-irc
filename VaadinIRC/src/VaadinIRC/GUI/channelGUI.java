package VaadinIRC.GUI;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import VaadinIRC.settings;
import VaadinIRC.GUI.componentContainers.ChannelGUIComponentContainer;
import VaadinIRC.VaadinIRC.VaIRCInterface;
import VaadinIRC.VaadinIRC.VaadinIRC;

import com.sun.java.swing.plaf.windows.resources.windows;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * GUI for VaadinIRC channel.
 * @author Aleksi Postari
 *
 */
public class channelGUI extends ChannelGUIComponentContainer implements Button.ClickListener, Serializable, Handler, ItemClickListener, FocusListener, BlurListener
{
	/** Name of the channel. */
	private String channelName;
	/** Reference to IRCInterface. */
	private VaIRCInterface ircInterface;
	/** Reference to VaadinIRC */
	VaadinIRC vaIRC;
	
	private static final Action ACTION_WHOIS = new Action("Whois");
	private static final Action ACTION_PRIVMSG = new Action("Private chat");
	private static final Action ACTION_OP = new Action("Op");
	private static final Action ACTION_VOICE = new Action("Voice");
    private static final Action[] ACTIONS_NICKNAMES = new Action[] { ACTION_WHOIS, ACTION_PRIVMSG, ACTION_OP, ACTION_VOICE };
	
	public channelGUI(String channelName, VaadinIRC vaIRC, VaIRCInterface ircInterface)
	{
		super(null);
		this.channelName = channelName;
		this.vaIRC = vaIRC;
		this.ircInterface = ircInterface;
		//this.ircChannel = new IRCChannel(channelName, networkName);
		labelTitle = 				new Label("", Label.CONTENT_RAW);
		panelMessages =				new Panel();
		tableNicknames =			new Table();
		textfieldMessagefield = 	new TextField();
		buttonSendMessage = 		new Button("Send");
		buttonSettings = 			new Button("");
		buttonChangeNick = 			new Button("");
		buttonRefreshUsernames =	new Button("");
		styleComponents();
	}
	
	/**
	 * Removes tags from a given string and returns the parsed string.<br>
	 * Example tags: <b>, </b>, <p>, <br/> ...
	 * @param string Target String.
	 * @return Returns the String where all the tags have been parsed.
	 */
	private String removeTags(String string)
	{
	    if (string == null || string.length() == 0) return string;
	    
	    Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	    Matcher m = REMOVE_TAGS.matcher(string);
	    return m.replaceAll("");
	}
	
	/**
	 * Converts URLs to html links.<br>
	 * example: www.google.com => <a href="www.google.com">www.google.com</a>
	 * @param text Target text.
	 * @return Returns the text where all urls have been converted into HTML links.
	 */
	private String convertURLsToHTMLLinks(String text)
	{
	    if (text == null) return text;
	    
	    return text.replaceAll("(\\A|\\s)((http|https|ftp|mailto):\\S+)(\\s|\\z)", "$1<a target=\"_blank\" href=\"$2\">$2</a>$4");
	}
	
	/**
	 * Adds standard channel message to the channel textarea. Repaints the panel and scrolls to bottom.
	 * @param username Message sender's nickname.
	 * @param newMessage Message to be sent.
	 */
	public void addStandardChannelMessage(String username, String newMessage)
	{
		newMessage = removeTags(newMessage);
		newMessage = convertURLsToHTMLLinks(newMessage);
		
		Label label = new Label("<b>" + username + "</b> " + newMessage);
			label.setContentMode(Label.CONTENT_RAW);
			label.setWidth(550, Sizeable.UNITS_PIXELS);
		panelMessages.addComponent(label);
		
		// Scroll messages panel to bottom message
		panelMessages.setScrollTop(Short.MAX_VALUE);
		panelMessages.requestRepaint();
		
		ircInterface.setNewActivityToTab(channelName);
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Adds new message to the channel textarea, repaints the panel and scrolls to bottom.
	 * @param newMessage Message to be added.
	 */
	public void addMessageToChannelTextarea(String newMessage)
	{
		newMessage = removeTags(newMessage);
		newMessage = convertURLsToHTMLLinks(newMessage);
		
		Label label = new Label(newMessage);
			label.setContentMode(Label.CONTENT_RAW);
			label.setWidth(550, Sizeable.UNITS_PIXELS);
		panelMessages.addComponent(label);
		
		// Scroll messages panel to bottom message
		panelMessages.setScrollTop(Short.MAX_VALUE);
		panelMessages.requestRepaint();
		
		ircInterface.setNewActivityToTab(channelName);
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Returns the channel name.
	 * @return Channel name.
	 */
	public String getChannelName()
	{
		return channelName;
	}
	
	/**
	 * Clears nickname table for this channel and sets sent nicknames to the table.
	 * @param nicknames ArrayList of nicknames.
	 */
	public void addChannelUsersToTable(ArrayList<String> nicknames)
	{
		tableNicknames.removeAllItems();
		for (String nickname : nicknames) addUserToChannel(nickname);
		sortNicknameTable();
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Adds user to channel table.
	 * User will only be added if it did exist.
	 * @param nickname Nickname to be added.
	 */
	public void addUserToChannel(String nickname)
	{
		String userLevel = "";
		if (nickname.startsWith("@")) { userLevel = "@"; nickname = nickname.substring(1); }
		else if (nickname.startsWith("+")) { userLevel = "+"; nickname = nickname.substring(1); }
		
		tableNicknames.addItem(new Object[] { userLevel, nickname }, new Integer(tableNicknames.size()));
		sortNicknameTable();
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Changes given user nickname. Does not alter the user level (leaves it to the same it was before for the nickname)
	 * @param nickname Nickname of the user without user level tag.
	 * @param newNickname New nickname which you want the nickname to be changed to without user level tag.
	 * @return Returns true if user was found and nickname changed. Otherwise false.
	 */
	public boolean changeUserNickname(String nickname, String newNickname)
	{
		List<Property> foundUsers = new ArrayList<Property>();
		// Iterate through all table items in row Nicknames and get all found rows to list
        for (Object id : tableNicknames.getItemIds())
        {
            String row = (String)tableNicknames.getContainerProperty(id, "Nicknames").getValue();
            if (row.toString().trim().equals(nickname.toString().trim()))
            {
            	foundUsers.add(tableNicknames.getContainerProperty(id, "Nicknames"));
            }
        }
        
		// Change nickname on found users.
        for (Property id : foundUsers)
        {
        	id.setValue(newNickname);
        	addMessageToChannelTextarea(nickname + " is now known as " + newNickname);
        }
        sortNicknameTable();
		ircInterface.pushChangesToClient();
		return false;
	}
	
	/**
	 * Removes user from channel table if it did exist there.
	 * Deletes all found users, so if user was somehow added multiple times to table,
	 * all the user nicknames will be removed from channel table.
	 * @param nickname Nickname to be removed.
	 * @param reason Quit reason.
	 * @param announceReason Announce reason message?
	 */
	public void removeUserFromChannel(String nickname, String reason, boolean announceReason)
	{
		List<Object> toDelete = new ArrayList<Object>();
		
		// Iterate through all table items in row Nicknames and get all found rows to list
        for (Object id : tableNicknames.getItemIds())
        {
            String row = (String)tableNicknames.getContainerProperty(id, "Nicknames").getValue();
            if (row.toString().trim().equals(nickname.toString().trim())) toDelete.add(id);
        }
        
		// Delete all found items
        for (Object id : toDelete)
        {
        	tableNicknames.removeItem(id);
            if (announceReason) addMessageToChannelTextarea(nickname + " " + reason);
        }
        sortNicknameTable();
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Styles the channel GUI components.
	 */
	public void styleComponents()
	{
		// Style title label
			labelTitle.setStyleName("channelTitleLabel");
			labelTitle.setSizeFull();
		// Style messages panel
			panelMessages.setSizeFull();
			panelMessages.setScrollable(true);
		// Style nicknames panel
			tableNicknames.setSizeFull();
			tableNicknames.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
			tableNicknames.setColumnWidth("Rights", 1);
			tableNicknames.setColumnAlignment("Rights", Table.ALIGN_RIGHT);
			tableNicknames.setColumnAlignment("Nicknames", Table.ALIGN_LEFT);
		// Style messagefield
			textfieldMessagefield.setStyleName("channelMessageTextfield");
			textfieldMessagefield.setWidth(100, Sizeable.UNITS_PERCENTAGE);
			textfieldMessagefield.setHeight(100, Sizeable.UNITS_PERCENTAGE);
		// Style send message button
			buttonSendMessage.setStyleName("buttonSendMessage");
			buttonSendMessage.setHeight(100, Sizeable.UNITS_PERCENTAGE);
		// Style settings button
			buttonSettings.setIcon(new ThemeResource("images/cog.png"));
			buttonSettings.setWidth(33, Sizeable.UNITS_PIXELS);
			buttonSettings.setDescription("Settings");
			buttonSettings.addListener(new ClickListener() { public void buttonClick(ClickEvent event) { vaIRC.showSettingsWindow(); }});
		// Style change nickname button
			buttonChangeNick.setIcon(new ThemeResource("images/user_edit.png"));
			buttonChangeNick.setWidth(33, Sizeable.UNITS_PIXELS);
			buttonChangeNick.setDescription("Change nickname");
			buttonChangeNick.addListener(new ClickListener() { public void buttonClick(ClickEvent event) { vaIRC.showNicknameChangeWindow(); }});
		// Style refresh nicknames button
			buttonRefreshUsernames.setIcon(new ThemeResource("images/arrow_refresh.png"));
			buttonRefreshUsernames.setWidth(33, Sizeable.UNITS_PIXELS);
			buttonRefreshUsernames.setDescription("Refresh channel usernames");
			buttonRefreshUsernames.addListener(new ClickListener() { public void buttonClick(ClickEvent event) { refreshNicknameList(); }});
			
		// Add action listeners
			buttonSendMessage.addListener((Button.ClickListener)this);
	        getChannelGUI().addAction(new ShortcutListener(channelName, KeyCode.ENTER, null)
	        {
	            @Override
	            public void handleAction(Object sender, Object target)
	            {
	            	if (isMsgTextfieldFocused) sendMessage(textfieldMessagefield.getValue().toString());
	            }
	        });
			
	        // Right click menu for nicknames table
			tableNicknames.addListener((ItemClickListener)this);
	        tableNicknames.addActionHandler((Action.Handler)this);
	        textfieldMessagefield.addListener((FocusListener)this);
	        textfieldMessagefield.addListener((BlurListener)this);
	}
	
	/**
	 * Creates the GUI for a channel.
	 */
	public void createChannelGUI()
	{
		panel = new Panel();
		panel.setCaption(channelName);
		panel.setSizeFull();
		panel.getContent().setSizeFull();
		AbstractLayout panelLayout = (AbstractLayout)panel.getContent();
		panelLayout.setMargin(false);
		panel.setImmediate(true);
		labelTitle.setValue("<b>"+channelName+"</b>");
		
		VerticalLayout mainVerticalLayout = new VerticalLayout();
			mainVerticalLayout.setSizeFull();
			// Top bar containing channel title & topright buttons
			GridLayout topGrid = new GridLayout(2, 1);
				topGrid.setStyleName("topBar");
				topGrid.addComponent(labelTitle);
				topGrid.setSizeFull();
				labelTitle.setSizeFull();
					HorizontalLayout hori = new HorizontalLayout();
					hori.setStyleName("rightTopBar");
					hori.setWidth(100, Sizeable.UNITS_PIXELS);
					hori.addComponent(buttonSettings);
					hori.addComponent(buttonChangeNick);
					hori.addComponent(buttonRefreshUsernames);
					topGrid.addComponent(hori);
				topGrid.setComponentAlignment(hori, Alignment.TOP_RIGHT);
				mainVerticalLayout.addComponent(topGrid);
				mainVerticalLayout.setExpandRatio(topGrid, 0.05f);
			// Message area & table of nicknames
			HorizontalLayout horizontal = new HorizontalLayout();
				horizontal.setSpacing(false);
				horizontal.setMargin(false);
				horizontal.setSizeFull();
				horizontal.addComponent(panelMessages);
				mainVerticalLayout.addComponent(horizontal);
				mainVerticalLayout.setExpandRatio(horizontal, 0.90f);
				if (!channelName.equals("status"))
				{
					horizontal.addComponent(tableNicknames);
					horizontal.setExpandRatio(panelMessages, 0.8f);
					horizontal.setExpandRatio(tableNicknames, 0.2f);
				}
			// Send message textfield & send button
			HorizontalLayout bottomBar = new HorizontalLayout();
				//bottomBar.setWidth(100, Sizeable.UNITS_PERCENTAGE);
				bottomBar.setSizeFull();
				//bottomBar.setSpacing(true);
				//bottomBar.setMargin(true, false, false, false);
				bottomBar.addComponent(textfieldMessagefield);
				bottomBar.addComponent(buttonSendMessage);
				bottomBar.setExpandRatio(textfieldMessagefield, 1f);
				bottomBar.setExpandRatio(buttonSendMessage, 0f);
			mainVerticalLayout.addComponent(bottomBar);
			mainVerticalLayout.setExpandRatio(bottomBar, 0.05f);
		
		horizontal.setImmediate(true);
		panelMessages.setImmediate(true);
		tableNicknames.setImmediate(true);
		textfieldMessagefield.setImmediate(true);
		tableNicknames.addContainerProperty("Rights", String.class, null);
		tableNicknames.addContainerProperty("Nicknames", String.class, null);
		tableNicknames.setSelectable(true);
		
		//mainVerticalLayout.setSizeFull();
		panel.addComponent(mainVerticalLayout);
	}
	
	/**
	 * Returns the panel containing reference to this channel's graphical user interface.
	 * @return Reference to channel GUI member variable.
	 */
	public Panel getChannelGUI()
	{
		if (panel == null) createChannelGUI();
		return panel;
	}

	/**
	 * Sends message to server.
	 * If text does not start with slash: adds the current message to channel GUI and writes to IRC.
	 * If command starts with slash, directly sends the command without slash to server write buffer.
	 * @param message Message to be sent.
	 */
	private void sendMessage(String message)
	{
		if (message == null || message.trim().equalsIgnoreCase("")) return;
		
		if (!ircInterface.isConnectionRunning())
		{
			ircInterface.receivedNoConnectionInitializedMessage();
			textfieldMessagefield.setValue("");
			ircInterface.pushChangesToClient();
			return;
		}
		
		if (settings.debug)
		{
			ircInterface.debugSendMessage(message);
			return;
		}
		
		if (message.startsWith("/"))
		{
			ircInterface.sendMessageToServer(message);
		}
		else
		{
			// Channel messages cannot be sent if channel is "status".
			if (channelName.equals("status")) return;

			ircInterface.sendMessageToChannel(channelName, message);
		}
		textfieldMessagefield.setValue("");
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Queries new list of nicknames from server.
	 */
	private void refreshNicknameList()
	{
		sendMessage("/NAMES " + channelName);
	}
	
	/**
	 * Sorts the table containing the nicknames.
	 */
	private void sortNicknameTable()
	{
		tableNicknames.sort(new Object[] { "Rights", "Nicknames" }, new boolean[] { false, false });
	}
	
	/**
	 * Sets the channel topic.
	 * @param topic Topic.
	 */
	public void setChannelTopic(String topic)
	{
		labelTitle.setValue("<b>"+channelName + "</b> <small><small>" + topic + "</small></small>");
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Sets level for user (opped, voiced, normal user).
	 * @param nickname Who is going to have this change.
	 * @param newLevel New level. This can be +o, -o, +v, -v.
	 * @return Returns true if user was found and level changed. Otherwise false.
	 */
	public boolean setUserLevel(String nickname, String newLevel)
	{
		List<Property> foundUsers = new ArrayList<Property>();
		// Iterate through all table items in row Nicknames and get found nickname level row references to list
        for (Object id : tableNicknames.getItemIds())
        {
            String row = (String)tableNicknames.getContainerProperty(id, "Nicknames").getValue();
            if (row.toString().trim().equals(nickname.toString().trim())) foundUsers.add(tableNicknames.getContainerProperty(id, "Rights"));
        }

        // Change userlevel on found users
        for (Property id : foundUsers)
        {
        	String oldLevel = id.getValue().toString();
        	if (newLevel.startsWith("+"))
        	{
        		String lvl = newLevel.substring(1, 2);
        		//System.out.println("old value: " + oldLevel + " & new val: " + lvl);
        		if (oldLevel.equals("") && lvl.equals("o")) { id.setValue("@"); addMessageToChannelTextarea("User " + nickname + " was opped."); sortNicknameTable(); return true; }
        		else if (oldLevel.equals("") && lvl.equals("v")) { id.setValue("+"); addMessageToChannelTextarea("User " + nickname + " was voiced."); sortNicknameTable(); return true; }
        		else if (oldLevel.equals("+") && lvl.equals("o")) { id.setValue("@"); addMessageToChannelTextarea("User " + nickname + " was opped."); sortNicknameTable(); return true; }
        		return false;
        	}
        	else if (newLevel.startsWith("-"))
        	{
        		String lvl = newLevel.substring(1, 2);
        		if (oldLevel.equals("@") && lvl.equals("o")) { id.setValue(""); addMessageToChannelTextarea("User " + nickname + " was deopped."); sortNicknameTable(); return true; }
        		else if (oldLevel.equals("v") && lvl.equals("v")) { id.setValue(""); addMessageToChannelTextarea("User " + nickname + " was devoiced."); sortNicknameTable(); return true; }
        		else if (oldLevel.equals("+") && lvl.equals("v")) { id.setValue(""); addMessageToChannelTextarea("User " + nickname + " was devoiced."); sortNicknameTable(); return true; }
        		else return false;
        	}
        }
		ircInterface.pushChangesToClient();
		return false;
	}
	
	/**
	 * Returns the given user level in the channel (op, voiced, normal user).
	 * @param nickname Nickname of the user you want to get level for.
	 * @return Returns the user level. @ is op, + is voiced and otherwise if normal user or was not found, will return "".
	 */
	@Deprecated
	private String getUserLevel(String nickname)
	{
        for (Object id : tableNicknames.getItemIds())
        {
            String row = (String)tableNicknames.getContainerProperty(id, "Nicknames").getValue();
            if (row.equals(nickname)) return (String)tableNicknames.getContainerProperty(id, "Rights").getValue();
        }
        return "";
	}

	/**
	 * When button is clicked.
	 */
	public void buttonClick(ClickEvent event)
	{
		if (event.getComponent().equals(buttonSendMessage))
		{
			sendMessage(textfieldMessagefield.getValue().toString());
		}
		else if (event.getComponent().equals(buttonSettings))
		{
			vaIRC.showSettingsWindow();
		}
	}

	/**
	 * Returns the nicknames list right menu actions.
	 */
	public Action[] getActions(Object target, Object sender)
	{
		return ACTIONS_NICKNAMES;
	}

	/**
	 * Handles the right mouse button action for user.
	 */
    public void handleAction(Action action, Object sender, Object target)
    {
        if (ACTION_WHOIS == action)
        {
            ircInterface.sendMessageToServer("/WHOIS " + selectedNickname);
        }
        else if (ACTION_PRIVMSG == action)
        {
        	ircInterface.sendMessageToServer("/QUERY " + selectedNickname);
        }
        else if (ACTION_OP == action)
        {
        	ircInterface.sendMessageToServer("/MODE " + channelName + " +o " + selectedNickname);
        }
        else if (ACTION_VOICE == action)
        {
        	ircInterface.sendMessageToServer("/MODE " + channelName + " +v " + selectedNickname);
        }
        ircInterface.pushChangesToClient();
    }
	
	/**
	 * When nickname row in table of nicknames is clicked.
	 */
	public void itemClick(ItemClickEvent event)
	{
		String selectedItem = event.getItem().getItemProperty("Nicknames").toString();
		if (selectedItem != null) selectedNickname = selectedItem;
	}

	/**
	 * When textfield gets focus.
	 */
	public void focus(FocusEvent event)
	{
		isMsgTextfieldFocused = true;
	}

	/**
	 * When textfield loses focus.
	 */
	public void blur(BlurEvent event)
	{
		isMsgTextfieldFocused = false;
	}
}