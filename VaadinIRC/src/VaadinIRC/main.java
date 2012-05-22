package VaadinIRC;

import irc.IRCSession;
import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.ui.*;

/*
 * TODO: Disconnect heitt‰‰ errorin.
 * TODO: Nick ongelmia. Omaa nickin vaihtamista ei huomioida nyt channeleilla. /NICK komennolla tehty‰ nimenvaihtoa ei huomioida nyt.
 * TODO: Error replies: http://www.irchelp.org/irchelp/rfc/chapter6.html#c6_3
 * TODO: Siirr‰ icepush jutut pois interfacesta.
 * TODO: oma oppaus ja voice ei toiminut irkiss‰.
 * TODO: ChannelGUI component container.
 * TODO: Kun vaihtaa nickin min‰ tai joku muu: DEBUG: did read line: :ASDQWEASD!~null@a91-152-121-162.elisa-laajakaista.fi NICK :testaaja
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentorivilt‰ parametrit.
 * TODO: Settingsist‰ pituus ja korkeus (ja prossina vai pikselein‰)
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