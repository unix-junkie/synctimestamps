/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_RED;
import static com.google.code.synctimestamps.ui.terminal.Color.BRIGHT_WHITE;
import static com.google.code.synctimestamps.ui.terminal.Color.CYAN;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.NORMAL;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public abstract class VtResponse implements VtKeyOrResponse {
	/**
	 * @param term
	 */
	protected abstract Terminal appendDescription(final Terminal term);

	/**
	 * The return value of this method can be returned by
	 * {@link InputEvent#toString(Terminal)}.
	 *
	 * @see InputEvent#toString(Terminal)
	 * @see VtKeyOrResponse#toString(Terminal)
	 */
	@Override
	public final void toString(final Terminal term) {
		term.setForeground(BRIGHT_RED).restoreDefaultBackground();
		term.print('[');

		/*
		 * White on cyan.
		 */
		term.setTextAttributes(BRIGHT_WHITE, CYAN);
		this.appendDescription(term);

		term.setForeground(BRIGHT_RED).restoreDefaultBackground();
		term.print(']');
		term.setTextAttributes(NORMAL);
	}
}
