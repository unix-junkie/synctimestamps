/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.jnlp;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Application;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface JnlpLauncher {
	/**
	 * @param application
	 * @throws IOException
	 */
	Process launchTerminalEmulator(@Nonnull final Application application)
	throws IOException;

	boolean exitAfterChildTerminates();
}
