package VaadinIRC.GUI.componentContainers;

import VaadinIRC.settings;
import VaadinIRC.GUI.AbstractWindowGUI;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

/**
 * Contains the components for the settings windows.
 * @author Aleksi Postari
 *
 */
public abstract class SettingsComponentContainer extends AbstractWindowGUI
{
	/** Server textfield */
	protected TextField textfieldServer;
	/** Port textfield */
	protected TextField textfieldPort;
	/** Connect to server button */
	protected Button buttonConnect;
	/** Disconnect from server button */
	protected Button buttonDisconnect;
	/** Nickname textfield */
	protected TextField textfieldNickname;
	/** Change nickname button */
	protected Button buttonChangeNick;
	
	public SettingsComponentContainer(Window mainWindow)
	{
		super(mainWindow);
	}

	/**
	 * Adds change nickname button.<br>
	 * @see #buttonChangeNick
	 */
	public void addButtonChangeNickname()
	{
		buttonChangeNick = new Button("Change nickname");
		addComponent(buttonChangeNick);
		buttonChangeNick.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { buttonPressedChangeNickname(textfieldNickname.getValue().toString()); } });
	}
	
	/**
	 * Adds nickname textfield.<br>
	 * @see #textfieldNickname
	 */
	public void addtextfieldNickname()
	{
		textfieldNickname = new TextField("New nickname:");
		addComponent(textfieldNickname);
	}
	
	/**
	 * Adds server textfield.
	 * @see #textfieldServer
	 */
	public void addServerTextfield()
	{
		textfieldServer = new TextField("Server Address:");
		textfieldServer.setValue(settings.DEFAULT_SERVER_ADDRESS);
		addComponent(textfieldServer);
	}
	
	/**
	 * Adds server port textfield.
	 * @see #textfieldPort
	 */
	public void addServerPortTextfield()
	{
		textfieldPort = new TextField("Server Port:");
		textfieldPort.setValue(Integer.toString(settings.DEFAULT_SERVER_PORT));
		addComponent(textfieldPort);
	}
	
	/**
	 * Adds connect button & actionlistener for it.
	 * @see #buttonConnect
	 */
	public void addButtonConnect()
	{
		buttonConnect = new Button("Connect to Server");
		buttonConnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { buttonPressedConnectToServer(); } });
		addComponent(buttonConnect);
	}
	
	/**
	 * Adds disconnect button & actionlistener for it.
	 * @see #buttonDisconnect
	 */
	public void addButtonDisconnect()
	{
		addComponent(new Label("---"));
		buttonDisconnect = new Button("Disconnect from Server");
		buttonDisconnect.addListener(new Button.ClickListener() { public void buttonClick(ClickEvent event) { buttonPressedDisconnectFromServer(); } });
		addComponent(buttonDisconnect);
	}
	
	/**
	 * When button "disconnect from server" was pressed.
	 */
	public void buttonPressedDisconnectFromServer() { }
	
	/**
	 * When button "Connect to server" was pressed.
	 */
	public void buttonPressedConnectToServer() { }
	
	/**
	 * When button "Change nickname" was pressed.
	 * @param newNick value of the textfield {@link #textfieldNickname}
	 */
	public void buttonPressedChangeNickname(String newNick) { }
}