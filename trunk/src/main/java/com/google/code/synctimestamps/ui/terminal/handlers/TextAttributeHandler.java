/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import static com.google.code.synctimestamps.ui.terminal.Color.MAGENTA;
import static com.google.code.synctimestamps.ui.terminal.Color.WHITE;

import java.util.List;

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
						for(final TextAttribute attribute : TextAttribute.values()) {
							term.setTextAttributes(MAGENTA, WHITE, attribute);
							term.println("The quick brown fox jumps over a lazy dog");
						}
						term.flush();
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
		term.println("Type ^R for text attribute demo.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}
}
