/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.InputEvent.ESC;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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

	/**
	 * <p>The threshold, in milliseconds, which, once exceeded,
	 * means that a new input event has occurred.</p>
	 *
	 * <p>The maximum delay observed between the read operations
	 * within a single escape sequence is 1 ms. The minimum delay
	 * observed between two separate escape sequences is 89 ms.</p>
	 *
	 * <p>Additionally, even a value of <em>1</em> doesn't prevent
	 * multiple keys pressed simultaneously
	 * from being interpreted as a single sequence.</p>
	 *
	 * <p>So the value of 45 ms is a fair trade.</p>
	 */
	private static final long INPUT_EVENT_THRESHOLD = 45;

	/**
	 * Despite a regular escape sequence length rarely exceeds 7,
	 * we should account for situations when multiple keys
	 * are pressed simultaneously.
	 */
	private static final int MAX_SEQUENCE_LENGTH = 1024;

	static int sequencePositionMarker;

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
			final Terminal term = new Terminal(ttyName, getenv("TERM"));

			term.println("Type ^Q or ^C to quit.");
			term.println("Type ^L for text area size reporting.");
			term.flush();

			final char sequence[] = new char[MAX_SEQUENCE_LENGTH];
			final Object sequenceLock = new Object();

			final Thread sequenceTokenizer = new Thread("SequenceTokenizer") {
				/**
				 * @see Thread#run()
				 */
				@Override
				public void run() {
					while (true) {
						try {
							/*
							 * Clear the interrupted status and sleep.
							 */
							interrupted();
							sleep(INPUT_EVENT_THRESHOLD);

							/*
							 * No new character has been read
							 * within the timeout -- process the sequence.
							 */
							final char sequenceClone[];
							synchronized (sequenceLock) {
								/*
								 * The sequence is empty.
								 */
								if (sequencePositionMarker == 0) {
									continue;
								}

								sequenceClone = new char[sequencePositionMarker];
								for (int j = 0; j < sequencePositionMarker; j++) {
									sequenceClone[j] = sequence[j];
								}
								sequencePositionMarker = 0;
							}

							for (final InputEvent event : term.split(sequenceClone)) {
								term.print(event);
								if (event.isControlWith('Q') || event.isControlWith('C')) {
									term.println();
									term.flush();

									term.close();
									System.exit(0);
								} else if (event.isControlWith('L')) {
									term.print(ESC + "[18t"); // "Correct" terminal size reporting
									term.print(ESC + "[999;999H" + ESC + "[6n"); // Workaround for buggy terminals
								}
							}
							term.println();
							term.flush();
						} catch (final InterruptedException ie) {
							/*
							 * New character has been read --
							 * continuing from the beginning.
							 */
							continue;
						}
					}
				}
			};
			sequenceTokenizer.start();

			int i;
			while (/* term.isOpen() && */ (i = term.read()) != -1) {
				/*
				 * 1. Interrupt the tokenizer,
				 * so that a new cycle is started.
				 */
				sequenceTokenizer.interrupt();

				/*
				 * 2. Append the character to the sequence.
				 */
				final char c = (char) i;
				synchronized (sequenceLock) {
					if (sequencePositionMarker < sequence.length) {
						sequence[sequencePositionMarker++] = c;
					}
				}
			}
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
