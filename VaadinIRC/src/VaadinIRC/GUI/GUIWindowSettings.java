package VaadinIRC.GUI;

import irc.IRCInterface;
import irc.IRCSession;

import VaadinIRC.settings;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.UserError;
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
	
	/**
	 * Constructor to create new settings window.
	 * @param mainWindow Main application window.
	 * @param session IRCSession.
	 * @param irc IRCInterface.
	 */
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
	
	/**
	 * Adds server textfield.
	 */
	private void addServerTextfield()
	{
		textfieldServer = new TextField("Server Address:");
		textfieldServer.setValue(settings.DEFAULT_SERVER_ADDRESS);
		addComponent(textfieldServer);
	}
	
	/**
	 * Adds server port textfield.
	 */
	private void addServerPortTextfield()
	{
		textfieldPort = new TextField("Server Port:");
		textfieldPort.setValue(Integer.toString(settings.DEFAULT_SERVER_PORT));
		addComponent(textfieldPort);
	}
	
	/**
	 * Adds connect button.
	 */
	private void addButtonConnect()
	{
		buttonConnect = new Button("Connect to Server");
		buttonConnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { connectToServer(); } });
		addComponent(buttonConnect);
	}
	
	/**
	 * Adds disconnect button.
	 */
	private void addButtonDisconnect()
	{
		addComponent(new Label("---"));
		buttonDisconnect = new Button("Disconnect from Server");
		buttonDisconnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { disconnectFromServer(); } });
		addComponent(buttonDisconnect);
	}
	
	/**
	 * To disconnect from IRC network.
	 */
	private void disconnectFromServer()
	{
		irc.quitNetwork(session.getServer());
		close();
	}
	
	/**
	 * To connect to IRC server.
	 */
	private void connectToServer()
	{
		// Validate server
		if (textfieldServer.getValue() == null || textfieldServer.getValue().toString().trim().equals(""))
		{
			textfieldServer.setComponentError(new UserError("Server cannot be empty."));
			return;
		}
		textfieldServer.setComponentError(null);
		
		// Validate port
		try
		{
			session.setServerPort(Integer.parseInt(textfieldPort.getValue().toString()));
		}
		catch (NumberFormatException e)
		{
			textfieldPort.setComponentError(new UserError("Port must be "));
		}
		if (textfieldPort.getValue() == null || textfieldPort.getValue().toString().trim().equals(""))
		{
			textfieldPort.setComponentError(new UserError("Port cannot be empty."));
			return;
		}
		textfieldPort.setComponentError(null);
		
		// If no problems, store values to session and connect to server.
		session.setServer(textfieldServer.getValue().toString());
		irc.connectToServer(session);
		close();
	}
}