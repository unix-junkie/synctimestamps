/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import java.util.Iterator;
import java.util.List;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;
import com.google.code.synctimestamps.ui.terminal.wt.RootWindow;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class WtHandler extends AbstractInputEventHandler {
	public WtHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public WtHandler(final InputEventHandler next) {
		super(next);
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		final Iterator<InputEvent> it = events.iterator();
		while (it.hasNext()) {
			if (it.next().isControlWith('W')) {
				it.remove();

				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						try {
							final RootWindow rootWindow = new RootWindow(term, null, Color.GREEN, Color.BLUE, Color.CYAN);
							rootWindow.setBorderAttributes(TextAttribute.BOLD);
							rootWindow.paint();
						} catch (final Throwable t) {
							term.clear();
							t.printStackTrace(term);
							term.flush();
						}
					}
				});
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
		term.println("Type ^W for user interface demo.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}
}
