package VaadinIRC.GUI;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import VaadinIRC.VaadinIRC.VaIRCInterface;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * GUI for VaadinIRC channel.
 * Contains the channel textfield, namelist and 
 * @author Aleksi Postari
 *
 */
public class channelGUI extends ShortcutListener implements Button.ClickListener, Serializable
{
	/** Name of the channel. TODO: Separate IRCChannel class? */
	private String channelName;
	/** Channel title text. TODO: Separate IRCChannel class? */
	private String channelTitle;
	/** Panel containing the channel messages. */
	private Panel panelMessages;
	/** Table containing the nicknames in the channel. */
	private Table tableNicknames;
	/** Textfield which will be used to write the message. */
	private TextField textfieldMessagefield;
	/** Button to send the message to channel or to IRC as an command. */
	private Button buttonSendMessage;
	/** Reference to IRCInterface. */
	private VaIRCInterface ircInterface;
	/** Panel containing this channel's GUI */
	private Panel panel;
	
	public channelGUI(String channelName, String networkName, VaIRCInterface ircInterface)
	{
		super(channelName, ShortcutAction.KeyCode.ENTER, null);
		this.channelName = channelName;
		this.ircInterface = ircInterface;
		//this.ircChannel = new IRCChannel(channelName, networkName);
		panelMessages =			new Panel();
		tableNicknames =		new Table();
		textfieldMessagefield = new TextField();
		buttonSendMessage = 	new Button("Send");
		styleComponents();
	}
	
	/**
	 * Adds new message to the channel textarea, repaints the panel and scrolls to bottom.
	 * @param newMessage Message to be added.
	 */
	public void addMessageToChannelTextarea(String newMessage)
	{
		Label label = new Label(newMessage);
			label.setContentMode(Label.CONTENT_RAW);
			label.setWidth(550, Sizeable.UNITS_PIXELS);
		panelMessages.addComponent(label);
		
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
		for (String nickname : nicknames)
			tableNicknames.addItem(new Object[] { nickname }, new Integer(tableNicknames.size()));
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Adds user to channel table.
	 * User will only be added if it did exist.
	 * @param nickname Nickname to be added.
	 */
	public void addUserToChannel(String nickname)
	{
		// Add user to end of table
		tableNicknames.addItem(new Object[] { nickname }, new Integer(tableNicknames.size()));
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Removes user from channel table if it did exist there.
	 * Deletes all found users, so if user was somehow added multiple times to table,
	 * all the user nicknames will be removed from channel table.
	 * @param nickname Nickname to be removed.
	 */
	public void removeUserFromChannel(String nickname)
	{
		List<Object> toDelete = new ArrayList<Object>();
		
		// Iterate through all table items in row Nicknames and get all found rows to list
        for (Object id : tableNicknames.getItemIds())
        {
            String row = (String)tableNicknames.getContainerProperty(id, "Nicknames").getValue();

            if (row.equals(nickname)) toDelete.add(id);
        }
        
		// Delete all found items
        for (Object id : toDelete) tableNicknames.removeItem(id);
		ircInterface.pushChangesToClient();
	}
	
	/**
	 * Styles the channel GUI components.
	 */
	private void styleComponents()
	{
		panelMessages.setWidth(550, Sizeable.UNITS_PIXELS);
		panelMessages.setHeight(500, Sizeable.UNITS_PIXELS);
		panelMessages.setScrollable(true);
		
		tableNicknames.setWidth(180, Sizeable.UNITS_PIXELS);
		tableNicknames.setHeight(500, Sizeable.UNITS_PIXELS);
		
		textfieldMessagefield.setWidth(672, Sizeable.UNITS_PIXELS);
		textfieldMessagefield.setStyleName("channelMessageTextfield");
		
		tableNicknames.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
		
		// Add action listeners
		buttonSendMessage.setStyleName("buttonSendMessage");
		buttonSendMessage.addListener(this);
		textfieldMessagefield.addShortcutListener(this);
	}
	
	/**
	 * Creates the channel GUI.
	 */
	private void createChannelGUI()
	{
		VerticalLayout mainVerticalLayout = new VerticalLayout();
		HorizontalLayout horizontal = new HorizontalLayout();
			horizontal.setSpacing(true);
			horizontal.addComponent(panelMessages);
			horizontal.addComponent(tableNicknames);
		mainVerticalLayout.addComponent(horizontal);
		HorizontalLayout bottomBar = new HorizontalLayout();
			bottomBar.setSpacing(true);
			bottomBar.setMargin(true, false, false, false);
			bottomBar.addComponent(textfieldMessagefield);
			bottomBar.addComponent(buttonSendMessage);
		mainVerticalLayout.addComponent(bottomBar);
		
		panelMessages.setImmediate(true);
		tableNicknames.setImmediate(true);
		textfieldMessagefield.setImmediate(true);
		tableNicknames.addContainerProperty("Nicknames", String.class, null);
		
		panel = new Panel();
			panel.setCaption(channelName);
			panel.setImmediate(true);
			panel.addComponent(mainVerticalLayout);
	}
	
	/**
	 * Returns the panel containing reference to this channel's graphical user interface.
	 * @return
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
	 */
	private void sendMessage()
	{
		String message = textfieldMessagefield.getValue().toString();
		if (message == null || message.trim().equalsIgnoreCase("")) return;
		
		if (!ircInterface.isConnectionRunning())
		{
			ircInterface.receivedStatusMessage("Connection to server has not yet been established. Could not send message to server.");
			textfieldMessagefield.setValue("");
			ircInterface.pushChangesToClient();
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
	 * When button "Send" is pressed.
	 */
	public void buttonClick(ClickEvent event)
	{
		sendMessage();
	}

	@Override
	public void handleAction(Object sender, Object target)
	{
		sendMessage();
		System.out.println("handling action in channel: " + channelName);
	}
}