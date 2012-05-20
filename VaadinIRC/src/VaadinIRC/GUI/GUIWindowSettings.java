package VaadinIRC.GUI;

import irc.IRCInterface;
import irc.IRCSession;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Settings window.
 * @author Aleksi Postari
 *
 */
public class GUIWindowSettings extends AbstractWindowGUI
{
	/** Server textfield */
	private TextField textfieldServer;
	/** Port textfield */
	private TextField textfieldPort;
	/** Connect to server button */
	private Button buttonConnect;
	/** Disconnect from server button */
	private Button buttonDisconnect;
	/** IRC Interface */
	private IRCInterface irc;
	/** IRC Session information */
	private IRCSession session;
	
	public GUIWindowSettings(Window mainWindow, IRCSession session, IRCInterface irc)
	{
		super(mainWindow);
		this.irc = irc;
		this.session = session;
		textfieldServer.setValue(session.getServer());
		textfieldPort.setValue(Integer.toString(session.getServerPort()));
	}

	@Override
	public void createWindow()
	{
		setCaption("Settings");
		setHeight(500, Sizeable.UNITS_PIXELS);
		setWidth(400, Sizeable.UNITS_PIXELS);
		center();
		addServerTextfield();
		addServerPortTextfield();
		addButtonConnect();
		addButtonDisconnect();
	}
	
	private void addServerTextfield()
	{
		addComponent(new Label("Server Address:"));
		textfieldServer = new TextField();
		addComponent(textfieldServer);
	}
	
	private void addServerPortTextfield()
	{
		addComponent(new Label("Server Port:"));
		textfieldPort = new TextField();
		addComponent(textfieldPort);
	}
	
	private void addButtonConnect()
	{
		buttonConnect = new Button("Connect to Server");
		buttonConnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { connectToServer(); } });
		addComponent(buttonConnect);
	}
	
	private void addButtonDisconnect()
	{
		addComponent(new Label("---"));
		buttonDisconnect = new Button("Disconnect from Server");
		buttonDisconnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { disconnectFromServer(); } });
		addComponent(buttonDisconnect);
	}
	
	private void disconnectFromServer()
	{
		irc.quitNetwork(session.getServer());
		close();
	}
	
	private void connectToServer()
	{
		irc.connectToServer(session);
		close();
	}
}