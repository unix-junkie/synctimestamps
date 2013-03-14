/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import static com.google.code.synctimestamps.ui.terminal.Color.BLACK;
import static com.google.code.synctimestamps.ui.terminal.Color.BLUE;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_BLACK;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_BLUE;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_CYAN;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_GREEN;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_MAGENTA;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_RED;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_WHITE;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_YELLOW;
import static com.google.code.synctimestamps.ui.terminal.Color.CYAN;
import static com.google.code.synctimestamps.ui.terminal.Color.GREEN;
import static com.google.code.synctimestamps.ui.terminal.Color.MAGENTA;
import static com.google.code.synctimestamps.ui.terminal.Color.RED;
import static com.google.code.synctimestamps.ui.terminal.Color.WHITE;
import static com.google.code.synctimestamps.ui.terminal.Color.YELLOW;
import static com.google.code.synctimestamps.ui.terminal.Dimension._80X24;

import java.util.Iterator;
import java.util.List;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.wt.ComponentBuffer;
import com.google.code.synctimestamps.ui.terminal.wt.Panel;
import com.google.code.synctimestamps.ui.terminal.wt.RootWindow;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class WtHandler extends AbstractInputEventHandler {
	private final String windowTitle;

	private RootWindow rootWindow;

	/**
	 * @param windowTitle
	 */
	public WtHandler(final String windowTitle) {
		this(windowTitle, null);
	}

	/**
	 * @param windowTitle
	 * @param next
	 */
	public WtHandler(final String windowTitle, final InputEventHandler next) {
		super(next);
		this.windowTitle = windowTitle;
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
							final RootWindow rootWindow0 = WtHandler.this.getRootWindow(term);
							rootWindow0.resizeToTerm();
							rootWindow0.paint();
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

	/**
	 * @param term
	 */
	RootWindow getRootWindow(final Terminal term) {
		final RootWindow rootWindow0 = this.rootWindow == null
				? this.rootWindow = new RootWindow(term, this.windowTitle, BRIGHT_WHITE, BLUE, BRIGHT_CYAN)
				: this.rootWindow;
		rootWindow0.setMinimumSize(_80X24);

		@SuppressWarnings("unused")
		final Panel contentPane = new Panel(rootWindow0) {
			/**
			 * @see Panel#paint(ComponentBuffer)
			 */
			@Override
			protected void paint(final ComponentBuffer buffer) {
				final Color colorPairs[][] = {
					{MAGENTA,		WHITE},
					{BRIGHT_MAGENTA,		WHITE},
					{MAGENTA,		BRIGHT_WHITE},
					{BRIGHT_MAGENTA,		BRIGHT_WHITE},
					{RED,			BLACK},
					{BRIGHT_RED,		BLACK},
					{RED,			BRIGHT_BLACK},
					{BRIGHT_RED,		BRIGHT_BLACK},
					{YELLOW,			GREEN},
					{BRIGHT_YELLOW,		GREEN},
					{YELLOW,			BRIGHT_GREEN},
					{BRIGHT_YELLOW,		BRIGHT_GREEN},
					{BLUE,			CYAN},
					{BRIGHT_BLUE,		CYAN},
					{BLUE,			BRIGHT_CYAN},
					{BRIGHT_BLUE,		BRIGHT_CYAN},
				};
				int y = 1;
				for (final Color colorPair[] : colorPairs) {
					final Color foreground = colorPair[0];
					final Color background = colorPair[1];
					if (background.isBright() && !term.getType().isBrightBackgroundSupported()) {
						continue;
					}
					buffer.setTextAt(foreground + " on " + background, 1, y++, false, foreground, background);
				}
			}
		};

		return rootWindow0;
	}
}
