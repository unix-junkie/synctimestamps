/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import java.util.List;

import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TerminalSizeProvider;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class TerminalSizeHandler implements InputEventHandler {
	private final InputEventHandler next;

	private final boolean nextIsFiltering;

	/**
	 * @param next
	 */
	public TerminalSizeHandler(final InputEventHandler next) {
		this.next = next;
		this.nextIsFiltering = next instanceof TerminalSizeProvider;
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
				if (this.nextIsFiltering) {
					final TerminalSizeProvider handler = (TerminalSizeProvider) this.next;
					final Dimension terminalSize = handler.getTerminalSize(term);
					term.println("Terminal size of " + terminalSize + " reported.");
				} else {
					term.requestTerminalSize();
					term.setCursorLocation(999, 999).requestCursorLocation();
					term.println();
					term.flush();
				}
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
