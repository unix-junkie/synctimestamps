/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.code.synctimestamps.ui.terminal.handlers.Echo;
import com.google.code.synctimestamps.ui.terminal.handlers.ExitHandler;
import com.google.code.synctimestamps.ui.terminal.handlers.TerminalSizeHandler;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public abstract class InputDemo {
	private static final String XTERM_PATHS[][] = {
		{"xterm", "-T", null, "-n", null, "-e", null},
		{"/usr/X11/bin/xterm", "-T", null, "-n", null, "-e", null},
	};

	private InputDemo() {
		assert false;
	}

	private static Process newTerminalProcess(final String title, final String iconName, final String program) {
		for (final String xtermPath[] : XTERM_PATHS) {
			try {
				assert xtermPath.length == 7 : xtermPath.length;
				xtermPath[2] = title;
				xtermPath[4] = iconName;
				xtermPath[6] = program;
				final Process terminalProcess = getRuntime().exec(xtermPath);
				return terminalProcess;
			} catch (final IOException ioe) {
				continue;
			}
		}
		return null;
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

		if (args.length == 1) {
			/*
			 * TTY device specified.
			 */
			final String ttyName = args[0];
			final Terminal term = new Terminal(ttyName, getenv("TERM"), new ExitHandler(new TerminalSizeHandler(new Echo())));
			term.start();
		} else {
			final String javaCommandLine = System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java -classpath \"" + System.getProperty("java.class.path") + "\" " + InputDemo.class.getName();

			final File shellScript = File.createTempFile("synctimestamps-" + getProperty("user.name") + '-', ".sh");
			final PrintWriter out = new PrintWriter(shellScript);
			out.println("#!" + getenv("SHELL"));
			out.println("tty=`tty 2>/dev/null`");
			out.println("returnValue=$?");
			out.println("if [ ${returnValue} -ne 0 ]");
			out.println("then");
			out.println("\texit ${returnValue}");
			out.println("else");
			/*
			 * man stty
			 */
			final StringBuilder sttyCommand = new StringBuilder();
			sttyCommand.append("\t");
			sttyCommand.append("stty");
			sttyCommand.append(" -icanon min 1 time 0"); // Allow individual characters to be read
			sttyCommand.append(" -echo"); // Don't echo what is being read
			sttyCommand.append(" -icrnl"); // ^M and <Enter> generate ^M, not ^J
			sttyCommand.append(" intr undef"); // Allow ^C to be received
			sttyCommand.append(" flush undef"); // Allow ^O to be received
			sttyCommand.append(" start undef"); // Allow ^Q to be received
			sttyCommand.append(" stop undef"); // Allow ^S to be received
			sttyCommand.append(" lnext undef"); // Allow ^V to be received
			sttyCommand.append(" dsusp undef"); // Allow ^Y to be received
			sttyCommand.append(" susp undef"); // Allow ^Z to be received
			sttyCommand.append(" quit undef"); // Allow ^\ to be received (otherwise JVM prints the full stack trace)
			out.println(sttyCommand);
			out.println("\t#stty -a");
			out.println('\t' + javaCommandLine + " ${tty}");
			out.println("\treturnValue=$?");
			out.println("\tstty sane 2>/dev/null");
			out.println("\t#echo \"Exiting with code ${returnValue}...\"");
			out.println("\t#read dummy");
			out.println("\texit ${returnValue}");
			out.println("fi");
			out.flush();
			out.close();
			shellScript.setExecutable(true);
			shellScript.deleteOnExit();

			final Process terminalProcess = newTerminalProcess("SyncTimeStamps", "SyncTimeStamps", shellScript.getPath());
			if (terminalProcess == null) {
				System.out.println("Failed to find a suitable terminal emulator in PATH.");
				return;
			}
			final int returnValue = terminalProcess.waitFor();
			if (returnValue != 0) {
				System.out.println("Child process exited with code " + returnValue);
			}
			System.exit(returnValue);
		}
	}
}
