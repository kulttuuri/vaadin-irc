package VaadinIRC;


import org.vaadin.artur.icepush.ICEPush;

import irc.IRCSession;
import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

/*
 * TODO: Error replies: http://www.irchelp.org/irchelp/rfc/chapter6.html#c6_3
 * TODO: Siirr‰ icepush jutut pois interfacesta.
 * TODO: Kannut ei nyt p‰ivity en‰‰ reaaliajassa.
 * TODO: oma oppaus ja voice ei toiminut irkiss‰.
 * TODO: Modal login / settings popuppi alkuun ja napista saa takaisin.
 * TODO: Kun vaihtaa nickin min‰ tai joku muu: DEBUG: did read line: :ASDQWEASD!~null@a91-152-121-162.elisa-laajakaista.fi NICK :testaaja
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentorivilt‰ parametrit.
 * TODO: Oma nimimerkki n‰kym‰‰n jonnekkin ja helposti vaihdettavaksi.
 * TODO: Settings valikko.
 * TODO: Settingsist‰ pituus ja korkeus (ja prossina vai pikselein‰)
 * TODO: Linkit ei toimi kanavan viesteiss‰.
 * TODO: poista html tagit kanavan viesteist‰.
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
		
		// TODO: Poista kun valmis.
		IRCSession session = new IRCSession("port80a.se.quakenet.org", 6667, "VaAle101", "VaIRC2", "VaUsr2");
		// Start VaadinIRC application.
		VaadinIRC vaadinIRC = new VaadinIRC(window, session, (Application)this);
	}
}