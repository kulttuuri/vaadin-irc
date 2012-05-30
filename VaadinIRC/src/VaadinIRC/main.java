/**
 * Copyright (C) 2012 Aleksi Postari
 * License type: MIT (http://en.wikipedia.org/wiki/MIT_License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package VaadinIRC;

import VaadinIRC.VaadinIRC.VaadinIRC;
import com.vaadin.Application;
import com.vaadin.ui.*;

/*
 * TODO: Settings from external file.
 * TODO: close() is missing from JDBC calls (it is automatically now closed after certain times, not best practice though)
 * TODO: definelle, addgroupille ja vastaavalle p‰‰luokka mik‰ yliajetaan ja toimii suoraan niiss‰ se toiminnallisuudet ja tulee komentoihin automaagisesti.
 * TODO: Timestampit asetukseen ett‰ n‰ytt‰‰kˆ chat viestien kanssa.
 * TODO: handlaamaan irkin boldaus ja n‰‰ formatoinnit.
 * TODO: Skandit.
 * TODO: Mode oppauksia ja muita VOI olla enemm‰n kuin 3. Nyt ei tajua kuin 3.
 * TODO: Status channelin viestit liian lyhyit‰ (Word wrappaa perus channelin tavoin), ei oo 100% width viestit.
 * TODO: Turn off serialization: http://dev-answers.blogspot.com/2007/03/how-to-turn-off-tomcat-session.html
 * TODO: External configurations file tai sitten passaa komentorivilt‰ parametrit.
 * TODO: Settingsist‰ pituus ja korkeus (ja prossina vai pikselein‰)
 * IRC Numerics: http://www.mirc.net/raws/#top
 * Threadeihin timeout checkkaus (jos yli 300s viime timeoutista, niin tapa threadit)
 * Tarkista clientilt‰ onko viel‰ connectannut tietyin v‰liajoin.
 * TODO: Tarkistamaan ett‰ k‰ytt‰j‰ on OP kun koittaa suorittaa komentoa.
 * TODO: Uusia ominaisuuksia:
 * 		- RSS readeri uutisille, automaaginen, voidaan enabloida / disabloida laittamalla !enablerss tai !disablerss
 * 		- !randomsentence ?#channel hakemaan random lause kanavalta.
 * 		- !randomlink ?#channel hakemaan random linkki kanavalta.
 * 		- votet kuntoon.
 * 		- message nickname viesti (vastaanottaja saa viestin kun connectaa tai kirjoittaa jotain kanavalle)
 * 		- definesearch (etsii defineist‰)
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