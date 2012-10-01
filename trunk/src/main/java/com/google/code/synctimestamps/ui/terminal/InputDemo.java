/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.Color.BLUE;
import static com.google.code.synctimestamps.ui.terminal.Color.CYAN;
import static com.google.code.synctimestamps.ui.terminal.Color.GREEN;
import static com.google.code.synctimestamps.ui.terminal.Color.RED;
import static com.google.code.synctimestamps.ui.terminal.Color.WHITE;
import static com.google.code.synctimestamps.ui.terminal.Color.YELLOW;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.BOLD;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.NORMAL;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.code.synctimestamps.ui.terminal.handlers.Echo;
import com.google.code.synctimestamps.ui.terminal.handlers.ExitHandler;
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
	 * @param charsetName
	 * @return the full contents of an 8-bit codepage.
	 */
	private static String getEightBitContents(final String charsetName) {
		final byte bytes[] = new byte[256];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) i;
		}

		try {
			return new String(bytes, charsetName);
		} catch (final UnsupportedEncodingException uee) {
			return "";
		}
	}

	private static CharSequence toHexString(final int i, final int padUpToLength, final boolean addPrefix) {
		final String s0 = Integer.toHexString(i);
		final StringBuilder s1 = new StringBuilder();
		if (addPrefix) {
			s1.append("0x");
		}
		for (int j = 0; j < padUpToLength - s0.length(); j++) {
			s1.append('0');
		}
		return s1.append(s0);
	}

	/**
	 * <p>
	 * In order to see line-drawing characters when logging in to a UNIX
	 * from Windows using Microsoft Telnet, one need to issue
	 * <pre>
	 * $ <b>export LANG=ru_RU.CP866</b>
	 * $ <b>export LC_ALL=${LANG}</b>
	 * </pre>
	 * </p>
	 *
	 * <p>Solaris console (<tt>sun-color</tt>) doesn't support
	 * VT100 alternate character set, but has 11 single-line
	 * characters with codes 90..9A. The rest of the characters
	 * are distributed according to ISO8859-1</p>
	 *
	 * @param term
	 * @see <a href = "http://www.in-ulm.de/~mascheck/various/alternate_charset/">http://www.in-ulm.de/~mascheck/various/alternate_charset/</a>
	 */
	private static void lineDrawingCharsDemo(final Terminal term) {
		term.setTextAttributes(YELLOW, BLUE, BOLD);
		term.println("Unicode line-drawing characters:");

		term.setTextAttributes(CYAN, BLUE, NORMAL);
		for (char i = '\u2500'; i <= '\u2590'; ) {
			for (char j = 0; j <= 0xf; j++) {
				term.print((char) (i + j));
			}
			term.println();
			i += 0x10;
		}

		term.setTextAttributes(YELLOW, BLUE, BOLD);
		term.println("VT100 alternate character set:");

		term.setTextAttributes(CYAN, BLUE, NORMAL);
		term.startAlternateCs();
		for (char i = 0x60; i <= 0x70; ) {
			for (char j = 0; j <= 0xf; j++) {
				term.print((char) (i + j));
			}
			term.println();
			i += 0x10;
		}
		term.stopAlternateCs();

		dumpCodepage(term, "CP437");
		dumpCodepage(term, "CP866");
		dumpCodepage(term, "KOI8-R");

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 * @param charsetName
	 */
	private static void dumpCodepage(final Terminal term, final String charsetName) {
		term.setTextAttributes(YELLOW, BLUE, BOLD);
		term.println(charsetName + " line-drawing characters:");

		final String cp437 = getEightBitContents(charsetName);
		for (int i = 0x00; i <= 0xf0; ) {
			term.setTextAttributes(GREEN, BLUE, BOLD);
			term.print(toHexString(i, 2, true));
			term.setTextAttributes(CYAN, BLUE, NORMAL);
			for (int j = 0x0; j <= 0xf; j++) {
				if (j != 0) {
					term.print("    ");
				}
				term.print(cp437.charAt(i + j));
			}
			term.println();

			for (int j = 0x0; j <= 0xf; j++) {
				final boolean emphasize = cp437.charAt(i + j) > 0xff;
				term.setTextAttributes(emphasize ? RED : WHITE, BLUE, emphasize ? BOLD : NORMAL);
				term.print(' ');
				term.print(toHexString(cp437.charAt(i + j), 4, false));
			}
			term.println();
			i += 0x10;
		}
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
				final Terminal term = new Terminal(ttyName, getenv("TERM"), new ExitHandler().append(new TerminalSizeHandler()).append(new Echo()));
				term.start();

				lineDrawingCharsDemo(term);
			} else {
				final String javaCommandLine = System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java -classpath \"" + System.getProperty("java.class.path") + "\" "
						+ (System.getProperty("os.name").equals("Mac OS X")
								? "-Dfile.encoding=\"`locale charmap`\" " // Apple's Java implementation default is MacRoman
								: "")
//						+ "-Dterminal.debug=true "
						+ InputDemo.class.getName();

				if (System.getProperty("os.name").equals("Mac OS X")) {
					System.setProperty("java.io.tmpdir", "/tmp");  // By default, /var/folders/Fv/FvLjTL7NHa06CiaNGkyzpE+++TI/-Tmp-/ is used
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
			System.exit(returnValue);
		}
	}
}
