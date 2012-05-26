package VaadinIRC;

import irc.IRCSession;
import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.ui.*;

/*
 * TODO: Status channelin viestit liian lyhyitä (Word wrappaa perus channelin tavoin)
 * TODO: Error replies: http://www.irchelp.org/irchelp/rfc/chapter6.html#c6_3
 * TODO: Siirrä icepush jutut pois interfacesta.
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentoriviltä parametrit.
 * TODO: Settingsistä pituus ja korkeus (ja prossina vai pikseleinä)
 * Muuta: http://blog.initprogram.com/2010/10/14/a-quick-basic-primer-on-the-irc-protocol/
 * IRC Numerics: http://www.mirc.net/raws/#top
 */

/**
 * Entry point for VaadinIRC.
 * @author Aleksi Postari
 *
 */
public class main extends Application
{
	/**
	 * Initializes vaadin application.
	 */
	@Override
	public void init()
	{
		// Create main window
		Window window = new Window(settings.APP_NAME);
		window.setTheme("VaIRCTheme");
		setMainWindow(window);
		
		// Set main window to full size.
		window.setSizeFull();
		window.getContent().setSizeFull();
		window.setStyleName("mainWindow");
		
		// Start VaadinIRC application.
		VaadinIRC vaadinIRC = new VaadinIRC(window, (Application)this);
	}
}