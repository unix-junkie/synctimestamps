/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface TerminalSizeProvider {
	/**
	 * @param term
	 * @throws IllegalStateException if called from the event dispatch
	 *         thread {@link SequenceConsumer#isDispatchThread()}
	 */
	Dimension getTerminalSize(final Terminal term);
}
