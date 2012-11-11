/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import static com.google.code.synctimestamps.ui.terminal.handlers.Handlers.asTerminalSizeProvider;
import static java.lang.Boolean.getBoolean;

import java.util.List;

import com.google.code.synctimestamps.ui.terminal.CursorLocationProvider;
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
public final class TerminalSizeHandler extends AbstractInputEventHandler {
	private boolean nextIsFiltering;

	public TerminalSizeHandler() {
		this(new FilteringTerminalSizeHandler());
	}

	/**
	 * @param next
	 */
	public TerminalSizeHandler(final InputEventHandler next) {
		super(next);
	}

	/**
	 * @see AbstractInputEventHandler#setNext(InputEventHandler)
	 */
	@Override
	void setNext(final InputEventHandler next) {
		super.setNext(next);
		this.nextIsFiltering = next instanceof TerminalSizeProvider || next instanceof CursorLocationProvider;
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
					final TerminalSizeProvider handler = this.next instanceof TerminalSizeProvider
							? (TerminalSizeProvider) this.next
							: asTerminalSizeProvider((CursorLocationProvider) this.next);
					term.invokeLater(new Runnable() {
						/**
						 * @see Runnable#run()
						 */
						@Override
						public void run() {
							final Dimension terminalSize;
							if (isDebugMode()) {
								/*
								 * In debug mode, clear the screen *before*
								 * the debug output is printed.
								 */
								term.clear();
								terminalSize = handler.getTerminalSize(term);
							} else {
								terminalSize = handler.getTerminalSize(term);
								/*
								 * Clear the screen *after*
								 * it has potentially been messed with.
								 */
								term.clear();
							}

							term.println("Terminal size of " + terminalSize + " reported.");
							term.flush();
						}

					});
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

	/**
	 * @return whether debug mode is turned on.
	 */
	static boolean isDebugMode() {
		return getBoolean("terminal.debug");
	}
}
