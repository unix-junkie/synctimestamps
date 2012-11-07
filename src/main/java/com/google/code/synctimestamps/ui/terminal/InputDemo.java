/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.Color.BLACK;
import static com.google.code.synctimestamps.ui.terminal.Color.WHITE;
import static java.lang.Boolean.getBoolean;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.exit;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.lang.System.setProperty;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.code.synctimestamps.ui.terminal.handlers.Echo;
import com.google.code.synctimestamps.ui.terminal.handlers.ExitHandler;
import com.google.code.synctimestamps.ui.terminal.handlers.LineDrawingHandler;
import com.google.code.synctimestamps.ui.terminal.handlers.TerminalSizeHandler;

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
public abstract class InputDemo {
	private static final String XTERM_PATHS[][] = {
		{"xterm", "-T", null, "-n", null, "-e", null},
		{"/usr/X11/bin/xterm", "-T", null, "-n", null, "-e", null},
	};

	private static final String WINDOW_TITLE = "SyncTimeStamps";

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

		if (!getProperty("os.name").startsWith("Windows")) {
			/*
			 * Most probably, POSIX.
			 */
			if (args.length == 1) {
				/*
				 * TTY device specified.
				 */
				final String ttyName = args[0];
				final Terminal term = new Terminal(ttyName, getenv("TERM"), new ExitHandler().append(new TerminalSizeHandler()).append(new LineDrawingHandler()).append(new Echo()));

				term.setTitle(WINDOW_TITLE);
				term.setToolbarVisible(false);
				term.setCursorVisible(false);
				term.setScrollbarVisible(false);

				term.setDefaultForeground(WHITE);
				term.setDefaultBackground(BLACK);
				term.clear();

				term.start();
			} else {
				final String javaCommandLine = getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java -classpath \"" + getProperty("java.class.path") + "\" "
						+ (getProperty("os.name").equals("Mac OS X")
								? "-Dfile.encoding=\"`locale charmap`\" " // Apple's Java implementation default is MacRoman
								: "")
						+ "-Dterminal.debug=" + getBoolean("terminal.debug") + " "
						+ InputDemo.class.getName();

				if (getProperty("os.name").equals("Mac OS X")) {
					setProperty("java.io.tmpdir", "/tmp");  // By default, /var/folders/Fv/FvLjTL7NHa06CiaNGkyzpE+++TI/-Tmp-/ is used
				}
				final File shellScript = File.createTempFile("synctimestamps-" + getProperty("user.name") + '-', ".sh");
				final PrintWriter out = new PrintWriter(shellScript);
				final String shell = getenv("SHELL");
				out.println("#!" + (shell != null && shell.length() != 0 ? shell : "/bin/sh"));
				/*
				 * In Cygwin, tty returns /dev/cons0, /dev/cons1,..
				 * those values being invalid Windows filenames.
				 */
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

				final Process terminalProcess = newTerminalProcess(WINDOW_TITLE, WINDOW_TITLE, shellScript.getPath());
				if (terminalProcess == null) {
					System.out.println("Failed to find a suitable terminal emulator in PATH.");
					return;
				}
				final int returnValue = terminalProcess.waitFor();
				if (returnValue != 0) {
					System.out.println("Child process exited with code " + returnValue);
				}
				exit(returnValue);
			}
		} else {
			/*
			 * Windows
			 */
			final String winDir = getenv("WINDIR");
			final String cmdPath = (winDir == null || winDir.length() == 0 ? getenv("SYSTEMROOT") : winDir) + File.separatorChar + "system32" + File.separatorChar + "cmd.exe";
			final String telnetPath = (winDir == null || winDir.length() == 0 ? getenv("SYSTEMROOT") : winDir) + File.separatorChar + "system32" + File.separatorChar + "telnet.exe";
			final String telnetCommandLine[];

			/**
			 * @todo Instead of launching telnet.exe directly, create a batch file and modify window title.
			 */
			final boolean modifyDefaultColors = true;
			if (modifyDefaultColors) {
				telnetCommandLine = new String[]{cmdPath, "/C", "start", cmdPath, "/T:8A", "/K", telnetPath, "-t", "ansi"};
			} else {
				telnetCommandLine = new String[]{cmdPath, "/C", "start", telnetPath, "-t", "ansi"};
			}

			final Process terminalProcess = getRuntime().exec(telnetCommandLine);
			if (terminalProcess == null) {
				System.out.println("Failed to find a suitable terminal emulator in PATH.");
				return;
			}

			final int returnValue = terminalProcess.waitFor();
			if (returnValue != 0) {
				System.out.println("Child process exited with code " + returnValue);
			}
			exit(returnValue);
		}
	}
}
