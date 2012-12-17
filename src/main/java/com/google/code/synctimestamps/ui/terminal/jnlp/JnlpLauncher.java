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
	 * @param mainClass
	 * @param title
	 * @param iconName
	 * @throws IOException
	 */
	Process launchExternalTerminalEmulator(@Nonnull final Class<?> mainClass,
			final String title,
			final String iconName)
	throws IOException;

	/**
	 * @param application
	 * @param keepCmdRunning
	 * @throws IOException
	 */
	Process launchTelnetSession(@Nonnull final Application application,
			final boolean keepCmdRunning)
	throws IOException;
}
