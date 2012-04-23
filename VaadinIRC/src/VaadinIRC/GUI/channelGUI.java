package VaadinIRC.GUI;

import irc.IRCHelper;

import java.io.Serializable;
import java.util.ArrayList;

import VaadinIRC.VaadinIRC.VaIRCInterface;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * GUI for VaadinIRC channel.
 * Contains the channel textfield, namelist and 
 * @author Aleksi Postari
 *
 */
public class channelGUI implements Button.ClickListener, Serializable
{
	private String channelName;
	private Panel panelMessages;
	private Table tableNicknames;
	private TextField textfieldMessagefield;
	private Button buttonSendMessage;
	//private IRCChannel ircChannel;
	/** Reference to IRCInterface. */
	private VaIRCInterface ircInterface;
	/** Panel containing this channel's GUI */
	private Panel panel;
	
	public channelGUI(String channelName, String networkName, VaIRCInterface ircInterface)
	{
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
	 * Adds new message to the channel textarea.
	 * @param newMessage
	 */
	public void addMessageToChannelTextarea(String newMessage)
	{
		Label label = new Label(newMessage);
		label.setContentMode(Label.CONTENT_RAW);
			label.setWidth(550, Sizeable.UNITS_PIXELS);
		panelMessages.addComponent(label);
	}
	
	/**
	 * Clears nickname table for this channel and sets sent nicknames to the table.
	 * @param nicknames ArrayList of nicknames.
	 */
	public void addChannelUsersToTable(ArrayList<String> nicknames)
	{
		tableNicknames.removeAllItems();
		for (String nickname : nicknames) tableNicknames.addItem(nickname);
	}
	
	public void addUserToChannel(String nickname)
	{
		tableNicknames.addItem(nickname);
		
	}
	
	public void removeUserFromChannel(String nickname)
	{
		tableNicknames.removeItem(nickname);
	}
	
	public String 	  getChannelName()		{ return channelName; }
	/*
	public TextArea   getGUIMessageArea()	{ return textareaMessages; }
	public Table      getGUINicknames()		{ return tableNicknames; }
	public TextField  getGUIMessageField()	{ return textfieldMessagefield; }
	*/
	//public IRCChannel getChannel()			{ return ircChannel; }
	
	private void styleComponents()
	{
		panelMessages.setWidth(550, Sizeable.UNITS_PIXELS);
		panelMessages.setHeight(500, Sizeable.UNITS_PIXELS);
		
		tableNicknames.setWidth(180, Sizeable.UNITS_PIXELS);
		tableNicknames.setHeight(500, Sizeable.UNITS_PIXELS);
		
		textfieldMessagefield.setWidth(672, Sizeable.UNITS_PIXELS);
		textfieldMessagefield.setStyleName("channelMessageTextfield");
		
		tableNicknames.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
		
		// Add action listeners
		buttonSendMessage.setStyleName("buttonSendMessage");
		buttonSendMessage.addListener(this);
	}
	
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
	 * When button "Send" is pressed.
	 * If text does not start with slash: adds the current message to channel GUI and writes to IRC.
	 * If command starts with slash, directly sends the command without slash to server write buffer.
	 */
	public void buttonClick(ClickEvent event)
	{
		String message = textfieldMessagefield.getValue().toString();
		if (message == null || message.trim().equalsIgnoreCase("")) return;
		
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
	}
}
