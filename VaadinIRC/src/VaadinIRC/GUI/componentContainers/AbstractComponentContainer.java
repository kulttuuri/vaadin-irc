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

package VaadinIRC.GUI.componentContainers;

import com.vaadin.ui.Window;

/**
 * Abstract base class for all Component containers.
 * @author Aleksi Postari
 *
 */
public abstract class AbstractComponentContainer extends Window
{
	/**
	 * Base class for all windows.<br>
	 * Adds the window to passed main window and creates the window if user desired to.
	 * @param mainWindow Main application window.
	 * @param createWindow Add this container to main application window and call {@link #createWindow()} method?
	 */
	public AbstractComponentContainer(Window mainWindow, boolean createWindow)
	{
		if (createWindow)
		{
			mainWindow.addWindow(this);
			createWindow();
		}
	}
	
	/**
	 * Creates the window. Called in constructor: {@link AbstractComponentContainer#AbstractWindowGUI(Window)}.
	 */
	public abstract void createWindow();
}
