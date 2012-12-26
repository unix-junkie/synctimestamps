/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.jnlp;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface UnixTerminalProvider {
	/**
	 * @param title
	 * @param iconName
	 * @param program
	 */
	Process newTerminalProcess(final String title,
			final String iconName,
			final String program);
}
