/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import java.util.List;

import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class ExitHandler extends AbstractInputEventHandler {
	public ExitHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public ExitHandler(final InputEventHandler next) {
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

		/*
		 * This handler should be the last one in a row.
		 */
		for (final InputEvent event : events) {
			if (event.isControlWith('Q') || event.isControlWith('C')) {
				term.close();
				System.exit(0);
			}
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^Q or ^C to quit.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}
}
