package VaadinIRC;


import org.vaadin.artur.icepush.ICEPush;

import irc.IRCSession;
import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

/*
 * TODO: Modal login / settings popuppi alkuun ja napista saa takaisin.
 * TODO: Kun vaihtaa nickin min‰ tai joku muu: DEBUG: did read line: :ASDQWEASD!~null@a91-152-121-162.elisa-laajakaista.fi NICK :testaaja
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentorivilt‰ parametrit.
 * TODO: Oma nimimerkki n‰kym‰‰n jonnekkin ja helposti vaihdettavaksi.
 * TODO: Settings valikko.
 * TODO: Settingsist‰ pituus ja korkeus (ja prossina vai pikselein‰)
 * TODO: Katso voiko tehd‰ oman actionlistenerin VaIRCInterfaceen
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
		
		// Set main window as full. We do not set getContent as that would also take in account the disabled ICEPush component.
		window.setSizeFull();
		window.setStyleName("mainWindow");
		
		// Initialize icePush addon & add it to main window.
		ICEPush pusher = new ICEPush();
			pusher.setWidth(0, Sizeable.UNITS_PERCENTAGE);
			pusher.setHeight(0, Sizeable.UNITS_PERCENTAGE);
			pusher.setEnabled(false);
			pusher.setVisible(false);
			window.addComponent(pusher);
		/*
		Label hiddenLabel = new Label("this label is visible.");
			hiddenLabel.setWidth(0, Sizeable.UNITS_PERCENTAGE);
			hiddenLabel.setHeight(0, Sizeable.UNITS_PERCENTAGE);
			hiddenLabel.setVisible(false);
			window.addComponent(hiddenLabel);
		
		// Creating a new vertical layout that holds the label. This should be whole page length.
		Label label = new Label("Label that should be the whole page height.4");
			label.setSizeFull();
			label.setStyleName("labelTest");
			window.addComponent(label);*/
		
		// TODO: Poista kun valmis.
		IRCSession session = new IRCSession("port80a.se.quakenet.org", 6667, "VaAle101", "VaIRC2", "VaUsr2");
		// Start VaadinIRC application.
		VaadinIRC vaadinIRC = new VaadinIRC(window, session, (Application)this, pusher);
	}
}