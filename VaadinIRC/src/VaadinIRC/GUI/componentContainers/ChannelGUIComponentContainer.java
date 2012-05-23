package VaadinIRC.GUI.componentContainers;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * Contains all the components for channel GUI.
 * @author Aleksi Postari
 *
 */
public class ChannelGUIComponentContainer extends AbstractComponentContainer
{
	/** Channel title label. */
	protected Label labelTitle;
	/** Settings button */
	protected Button buttonSettings;
	/** Change nickname button */
	protected Button buttonChangeNick;
	/** Refresh usernames button */
	protected Button buttonRefreshUsernames;
	/** Panel containing the channel messages. */
	protected Panel panelMessages;
	/** Table containing the nicknames in the channel. */
	protected Table tableNicknames;
	/** Textfield which will be used to write the message. */
	protected TextField textfieldMessagefield;
	/** Button to send the message to channel or to IRC as an command. */
	protected Button buttonSendMessage;
	/** Panel containing this channel's GUI */
	protected Panel panel;
	/** Selected nickname in table of nicknames. */
	protected String selectedNickname = "";
	/** is the message textfield focused? */
	protected boolean isMsgTextfieldFocused = false;
	
	public ChannelGUIComponentContainer(Window mainWindow)
	{
		super(mainWindow, false);
	}

	@Override
	public void createWindow()
	{
		// Not used.
	}
}