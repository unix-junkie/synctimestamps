/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.jnlp;

import static java.lang.System.exit;
import static java.lang.System.getProperty;

import java.io.IOException;

import com.google.code.synctimestamps.ui.terminal.Application;
import com.google.code.synctimestamps.ui.terminal.InputDemo;

/**
 * This code has been designed for JNLP/Java Web Start use,
 * as it launches external terminal emulators.
 *
 * Launching the application from command line should be done
 * in a different way.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class JnlpMain {
	private JnlpMain() {
		assert false;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(final String args[]) throws IOException, InterruptedException {
		if (args.length > 1) {
			System.out.println("Usage: ");
			return;
		}

		final JnlpLauncher launcher;
		if (!getProperty("os.name").startsWith("Windows")) {
			/*
			 * Most probably, POSIX.
			 */
			launcher = new UnixJnlpLauncher();

			final Process terminalProcess = launcher.launchExternalTerminalEmulator(InputDemo.class, InputDemo.WINDOW_TITLE, InputDemo.WINDOW_TITLE);
			if (terminalProcess == null) {
				System.out.println("Failed to find a suitable terminal emulator in PATH.");
				return;
			}
			final int returnValue = terminalProcess.waitFor();
			if (returnValue != 0) {
				System.out.println("Child process exited with code " + returnValue);
			}
			exit(returnValue);
		} else {
			/*
			 * Windows
			 */
			final Application application = new InputDemo();
			final boolean keepCmdRunning = false;

			launcher = new Win32JnlpLauncher();

			final Process terminalProcess = launcher.launchTelnetSession(application, keepCmdRunning);
			if (terminalProcess == null) {
				System.out.println("Failed to find a suitable terminal emulator in PATH.");
				return;
			}

			/*-
			 * If cmd.exe is run as "cmd /C", we shouldn't exit here
			 * (telnet.exe will still be running).
			 * If cmd.exe is run as "cmd /K",
			 * this child process never returns (unless we terminate the JVM).
			 *
			 * On Windows, we shouldn't be actually waiting for the child process
			 * to return: this launcher doesn't spawn any separate child JVM
			 * (which is what we do on UNIX).
			 *
			 * Additionally, we don't yet detect it if a user
			 * just closes the telnet.exe window (JVM continues running).
			 */
			final int returnValue = terminalProcess.waitFor();
			if (returnValue != 0) {
				System.out.println("Child process exited with code " + returnValue);
			}

			if (keepCmdRunning) {
				/*-
				 * If cmd.exe has been kept running,
				 * we don't reach this point anyway
				 * (unless it is forcibly terminated -- in this case,
				 * it exits with code 1).
				 */
				exit(returnValue);
			}
		}
	}
}
