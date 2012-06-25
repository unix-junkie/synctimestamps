/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import static com.google.code.synctimestamps.ui.terminal.InputEvent.ESC;

import java.util.List;

import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class TerminalSizeHandler implements InputEventHandler {
	private final InputEventHandler next;

	/**
	 * @param next
	 */
	public TerminalSizeHandler(final InputEventHandler next) {
		this.next = next;
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
			if (event.isControlWith('L')) {
				term.print(ESC + "[18t"); // "Correct" terminal size reporting
				term.print(ESC + "[999;999H" + ESC + "[6n"); // Workaround for buggy terminals
				term.println();
				term.flush();
			}
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^L for text area size reporting.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}
}
