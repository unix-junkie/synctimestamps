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
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.BOLD;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$
 */
public final class TextAttributeHandler extends AbstractInputEventHandler {
	public TextAttributeHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public TextAttributeHandler(final InputEventHandler next) {
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
			if (event.isControlWith('R')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						term.clear();
						testColorPair(term, MAGENTA, WHITE);
						testColorPair(term, BRIGHT_MAGENTA, WHITE);
						testColorPair(term, MAGENTA, BRIGHT_WHITE);
						testColorPair(term, BRIGHT_MAGENTA, BRIGHT_WHITE);
						testColorPair(term, RED, BLACK);
						testColorPair(term, BRIGHT_RED, BLACK);
						testColorPair(term, RED, BRIGHT_BLACK);
						testColorPair(term, BRIGHT_RED, BRIGHT_BLACK);
						testColorPair(term, YELLOW, GREEN);
						testColorPair(term, BRIGHT_YELLOW, GREEN);
						testColorPair(term, YELLOW, BRIGHT_GREEN);
						testColorPair(term, BRIGHT_YELLOW, BRIGHT_GREEN);
						testColorPair(term, BLUE, CYAN);
						testColorPair(term, BRIGHT_BLUE, CYAN);
						testColorPair(term, BLUE, BRIGHT_CYAN);
						testColorPair(term, BRIGHT_BLUE, BRIGHT_CYAN);
						term.flush();
					}
				});
			}
		}
	}

	/**
	 * @param term
	 * @param foreground
	 * @param background
	 */
	static void testColorPair(@Nonnull final Terminal term,
			@Nonnull final Color foreground,
			@Nonnull final Color background) {
		term.setTextAttributes(foreground, background);
		term.println(foreground + " on " + background);

		if (foreground.isDark()) {
			term.setBrightForeground(foreground);
			term.println(foreground.brighter() + " on " + background + " (via AIXTerm sequences)");

			term.setTextAttributes(BOLD);
			term.println(foreground.brighter() + " on " + background + " (via AIXTerm sequences and bold attribute)");
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^R for text attribute demo.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}
}
