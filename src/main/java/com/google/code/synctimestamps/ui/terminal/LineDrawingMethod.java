/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.TerminalType.SUN_COLOR;
import static java.util.Arrays.asList;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public enum LineDrawingMethod {
	UNICODE {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			return asList("UTF-8", "IBM437", "IBM866", "KOI8-R").contains(term.getEncoding());
		}
	},
	VT100_LINES {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			switch (term.getType()) {
			case DTTERM:
			case KTERM:
			case RXVT:
			case RXVT_UNICODE:
			case RXVT_UNICODE_256COLOR:
			case SCREEN:
			case SCREEN_LINUX:
			case VT320:
			case XTERM:
			case CYGWIN:
				return true;
			case ANSI:
			case LINUX:
			case SCOANSI:
			case SUN_CMD:
			case SUN_COLOR:
			case VT52:
			case VT100:
			case VTNT:
			default:
				return false;
			}
		}
	},
	SUN_COLOR_LINES {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			return term.getType() == SUN_COLOR;
		}
	},
	ASCII {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			/*
			 * Always supported regardless of terminal capabilities.
			 */
			return true;
		}
	},
	;

	public abstract boolean supportedFor(final Terminal term);
}
