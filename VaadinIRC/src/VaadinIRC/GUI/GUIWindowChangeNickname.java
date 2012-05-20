package VaadinIRC.GUI;

import irc.IRCSession;

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
public class GUIWindowChangeNickname extends AbstractWindowGUI
{
	private TextField textfieldNickname;
	private Button buttonChangeNick;
	private IRCSession session;
	
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
		this.addComponent(new Label("New nickname:"));
		textfieldNickname = new TextField();
		textfieldNickname.setWidth(200, Sizeable.UNITS_PIXELS);
		this.addComponent(textfieldNickname);
		this.addComponent(new Label("<br>", Label.CONTENT_RAW));
		buttonChangeNick = new Button("Change nickname");
		this.addComponent(buttonChangeNick);
		buttonChangeNick.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { changeNickname(textfieldNickname.getValue().toString()); } });
	}

	/**
	 * Used to change users nickname.<br>
	 * Verifies that IRC connection is running and that username contains only allowed characters.
	 * @param newNick New nickname for the user.
	 */
	private void changeNickname(String newNick)
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
