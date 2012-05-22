package VaadinIRC.GUI;

import irc.IRCSession;

import VaadinIRC.GUI.componentContainers.SettingsComponentContainer;

import com.vaadin.terminal.Sizeable;
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
	
	/**
	 * Constructor to initialize new window "Change Nickname".
	 * @param mainWindow Main application window.
	 * @param session IRCSession information.
	 */
	public GUIWindowChangeNickname(Window mainWindow, IRCSession session)
	{
		super(mainWindow);
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
		if (newNick.equals(session.getNickname()))
		{
			this.setVisible(false);
			return;
		}
		// TODO: Verify that nickname contains only allowed characters.
		// TODO: Check that connection to network has been first initialized.
		System.out.println("handling nick change...");
	}
}