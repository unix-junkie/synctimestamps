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
	 * @param term
	 * @param events
	 */
	void handle(final Terminal term, final List<InputEvent> events);

	/**
	 * @param term
	 */
	void printUsage(final Terminal term);

	/**
	 * @param next
	 */
	InputEventHandler append(final InputEventHandler next);
}
