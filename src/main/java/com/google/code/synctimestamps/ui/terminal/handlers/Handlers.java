/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import com.google.code.synctimestamps.ui.terminal.CursorLocationProvider;
import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.Point;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TerminalSizeProvider;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public abstract class Handlers {
	private Handlers() {
		assert false;
	}

	public static TerminalSizeProvider asTerminalSizeProvider(final CursorLocationProvider cursorLocationProvider) {
		if (cursorLocationProvider == null) {
			throw new IllegalArgumentException();
		}

		return new TerminalSizeProvider() {
			/**
			 * @see TerminalSizeProvider#getTerminalSize(Terminal)
			 */
			@Override
			public Dimension getTerminalSize(final Terminal term) {
				term.setCursorLocation(999, 999);
				final Point cursorLocation = cursorLocationProvider.getCursorLocation(term);
				return new Dimension(cursorLocation.getX(), cursorLocation.getY());
			}
		};
	}
}
