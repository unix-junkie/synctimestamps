/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import java.util.Iterator;
import java.util.List;

import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TerminalType;
import com.google.code.synctimestamps.ui.terminal.VtKeyOrResponse;
import com.google.code.synctimestamps.ui.terminal.VtTerminalSize;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class FilteringTerminalSizeHandler implements InputEventHandler {
	private final InputEventHandler next;

	private final Object expectingTerminalSizeLock = new Object();

	/**
	 * The moment (in milliseconds) when {@link
	 * #setExpectingTerminalSize(boolean) expectingTerminalSize}
	 * was set to {@code true}. If this value is {@code 0L}, then
	 * {@link #isExpectingTerminalSize() expectingTerminalSize} flag
	 * is {@code false}.
	 */
	private long t0;

	/**
	 * @param next
	 */
	public FilteringTerminalSizeHandler(final InputEventHandler next) {
		this.next = next;
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		synchronized (this.expectingTerminalSizeLock) {
			if (this.isExpectingTerminalSize()) {
				final Iterator<InputEvent> it = events.iterator();
				while (it.hasNext()) {
					final InputEvent event = it.next();
					final TerminalType type = term.getType();
					if (type.isKnownEscapeSequence(event)) {
						 final VtKeyOrResponse vtKeyOrResponse = type.getVtKeyOrResponse(event);
						 if (vtKeyOrResponse instanceof VtTerminalSize) {
							 final VtTerminalSize terminalSize = (VtTerminalSize) vtKeyOrResponse;
							 it.remove();

							 final int width = terminalSize.getWidth();
							 final int height = terminalSize.getHeight();
							 final long t1 = System.currentTimeMillis();
							 term.println("Terminal size of " + width + 'x' + height + " reported " + (t1 - this.t0) + " ms after the request.");

							 /*
							  * Reset the "expectingTerminalSize" status.
							  */
							 this.setExpectingTerminalSize(false);

							 /*
							  * We're expecting only a single terminal size event.
							  */
							 break;
						 }
					}
				}
			}
		}

		if (this.next != null) {
			this.next.handle(term, events);
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		if (this.next != null) {
			this.next.printUsage(term);
		}
	}

	/**
	 * @param expectingTerminalSize
	 */
	public void setExpectingTerminalSize(final boolean expectingTerminalSize) {
		synchronized (this.expectingTerminalSizeLock) {
			this.t0 = expectingTerminalSize ? System.currentTimeMillis() : 0L;
		}
	}

	public boolean isExpectingTerminalSize() {
		synchronized (this.expectingTerminalSizeLock) {
			return this.t0 != 0L;
		}
	}
}
