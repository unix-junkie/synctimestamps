/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface Application {
	/**
	 * @param term
	 */
	Runnable getPostCreationTask(final Terminal term);

	InputEventHandler getInputEventHandler();

	String getWindowTitle();

	String getIconName();
}
