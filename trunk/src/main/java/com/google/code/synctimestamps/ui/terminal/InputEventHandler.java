/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import java.util.List;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface InputEventHandler {
	/**
	 * @param events
	 */
	void handle(final List<InputEvent> events);
}
