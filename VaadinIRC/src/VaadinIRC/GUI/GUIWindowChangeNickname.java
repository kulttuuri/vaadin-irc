package VaadinIRC.GUI;

import irc.IRCInterface;
import irc.IRCSession;

import VaadinIRC.GUI.componentContainers.SettingsComponentContainer;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Window for user to change hes nickname.
 * @author Aleksi Postari
 *
 */
public class GUIWindowChangeNickname extends SettingsComponentContainer
{
	/** IRC Session information. */
	private IRCSession session;
	/** IRC Interface. */
	IRCInterface irc;
	
	/**
	 * Constructor to initialize new window "Change Nickname".
	 * @param mainWindow Main application window.
	 * @param session IRCSession information.
	 * @param irc Reference to IRCInterface.
	 */
	public GUIWindowChangeNickname(Window mainWindow, IRCSession session, IRCInterface irc)
	{
		super(mainWindow);
		this.irc = irc;
		this.session = session;
		textfieldNickname.setValue(session == null ? "" : session.getNickname() == null ? "" : session.getNickname());
	}

	@Override
	public void createWindow()
	{
		setCaption("Change nickname");
		center();
		setWidth(300, Sizeable.UNITS_PIXELS);
		setHeight(300, Sizeable.UNITS_PIXELS);
		addtextfieldNickname();
		textfieldNickname.setWidth(200, Sizeable.UNITS_PIXELS);
		this.addComponent(new Label("<br>", Label.CONTENT_RAW));
		addButtonChangeNickname();
	}

	@Override
	public void buttonPressedChangeNickname(String newNick)
	{
		if (newNick.trim().equals(""))
		{
			textfieldNickname.setComponentError(new UserError("Username cannot be empty."));
			return;
		}
		
		irc.sendMessageToServer("/NICK " + newNick);
		this.setVisible(false);
		return;
	}
}