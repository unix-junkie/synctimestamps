/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import static com.google.code.synctimestamps.ui.terminal.Color.BLACK;
import static com.google.code.synctimestamps.ui.terminal.Color.BLUE;
import static com.google.code.synctimestamps.ui.terminal.Color.CYAN;
import static com.google.code.synctimestamps.ui.terminal.Color.GREEN;
import static com.google.code.synctimestamps.ui.terminal.Color.MAGENTA;
import static com.google.code.synctimestamps.ui.terminal.Color.RED;
import static com.google.code.synctimestamps.ui.terminal.Color.WHITE;
import static com.google.code.synctimestamps.ui.terminal.Color.YELLOW;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.BLINK;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.BLINK_RAPID;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.BOLD;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.NORMAL;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;

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
						testColorPair(term, RED, BLACK);
						testColorPair(term, YELLOW, GREEN);
						testColorPair(term, BLUE, CYAN);
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
		final EnumSet<TextAttribute> attributes = EnumSet.of(NORMAL, BOLD, BLINK, BLINK_RAPID);
		for(final TextAttribute attribute : attributes) {
			term.setTextAttributes(foreground, background, attribute);
			term.println(attribute + "\t: " + foreground + " on " + background);
		}
		for(final TextAttribute attribute : attributes) {
			term.setTextAttributes(attribute);
			term.setBrightForeground(foreground);
			term.setBrightBackground(background);
			term.println(attribute + "\t: " + foreground + " on " + background);
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
