/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import static com.google.code.synctimestamps.ui.terminal.Color.BLUE;
import static com.google.code.synctimestamps.ui.terminal.Color.CYAN;
import static com.google.code.synctimestamps.ui.terminal.Color.GREEN;
import static com.google.code.synctimestamps.ui.terminal.Color.RED;
import static com.google.code.synctimestamps.ui.terminal.Color.WHITE;
import static com.google.code.synctimestamps.ui.terminal.Color.YELLOW;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.BOLD;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.NORMAL;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class LineDrawingHandler extends AbstractInputEventHandler {
	public LineDrawingHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public LineDrawingHandler(final InputEventHandler next) {
		super(next);
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		if (this.next != null) {
			this.next.handle(term, events);
		}

		for (final InputEvent event : events) {
			if (event.isControlWith('D')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						term.clear();
						lineDrawingUnicode(term);
					}
				});
			} else if (event.isControlWith('F')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						term.clear();
						lineDrawingVt100(term);
					}
				});
			} else if (event.isControlWith('G')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						term.clear();
						lineDrawingSunColor(term);
					}
				});
			} else if (event.isControlWith('H')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						term.clear();
						lineDrawingCp437(term);
					}
				});
			} else if (event.isControlWith('J')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						term.clear();
						lineDrawingCp866(term);
					}
				});
			} else if (event.isControlWith('K')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						term.clear();
						lineDrawingKoi8r(term);
					}
				});
			}
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^D for Unicode line-drawing characters demo.");
		term.println("Type ^F for VT100 line-drawing characters demo.");
		term.println("Type ^G for sun-color line-drawing characters demo.");
		term.println("Type ^H for CP437 line-drawing characters demo.");
		term.println("Type ^J for CP866 line-drawing characters demo.");
		term.println("Type ^K for KOI8-R line-drawing characters demo.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
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

	/**
	 * @param i
	 * @param padUpToLength
	 * @param addPrefix
	 */
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
	 * @param term
	 */
	static void lineDrawingUnicode(final Terminal term) {
		term.setTextAttributes(YELLOW, BLUE, BOLD);
		term.println("Unicode line-drawing characters:");

		term.setTextAttributes(CYAN, BLUE, NORMAL);
		for (char i = '\u2500'; i <= '\u2590'; ) {
			for (char j = 0x0; j <= 0xf; j++) {
				term.print((char) (i + j));
			}
			term.println();
			i += 0x10;
		}

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 * @see <a href = "http://www.in-ulm.de/~mascheck/various/alternate_charset/">http://www.in-ulm.de/~mascheck/various/alternate_charset/</a>
	 */
	static void lineDrawingVt100(final Terminal term) {
		term.setTextAttributes(YELLOW, BLUE, BOLD);
		term.println("VT100 alternate character set:");

		term.setTextAttributes(CYAN, BLUE, NORMAL);
		term.startAlternateCs();
		for (char i = 0x60; i <= 0x70; ) {
			for (char j = 0x0; j <= 0xf; j++) {
				term.print((char) (i + j));
			}
			term.println();
			i += 0x10;
		}
		term.stopAlternateCs();

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * <p>Solaris console (<tt>sun-color</tt>) doesn't support
	 * VT100 alternate character set, but has 11 single-line
	 * characters with codes 90..9A. The rest of the characters
	 * are distributed according to ISO8859-1</p>
	 *
	 * @param term
	 */
	static void lineDrawingSunColor(final Terminal term) {
		term.setTextAttributes(YELLOW, BLUE, BOLD);
		term.println("sun-color line-drawing characters:");

		term.setTextAttributes(CYAN, BLUE, NORMAL);
		try {
			final byte bytes[] = new byte[0x9A - 0x90 + 1];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) (0x90 + i);
			}
			final String s = new String(bytes, "ISO8859-1");

			term.println(s);
		} catch (final UnsupportedEncodingException uoe) {
			uoe.printStackTrace(term);
		}

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 */
	static void lineDrawingCp437(final Terminal term) {
		dumpCodepage(term, "CP437");

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 */
	static void lineDrawingCp866(final Terminal term) {
		dumpCodepage(term, "CP866");

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 */
	static void lineDrawingKoi8r(final Terminal term) {
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
}
