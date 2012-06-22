/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.InputEvent.ESC;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public enum VtKey implements VtKeyOrResponse {
	F1,
	F2,
	F3,
	F4,
	F5,
	F6,
	F7,
	F8,
	F9,
	F10,
	F11,
	F12,

	HOME,
	INSERT,
	DELETE,
	END,
	PAGE_UP,
	PAGE_DOWN,

	UP,
	DOWN,
	RIGHT,
	LEFT,
	;

	/**
	 * The return value of this method can be returned by
	 * {@link InputEvent#toString()}.
	 *
	 * @see Enum#toString()
	 * @see InputEvent#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();

		s.append(ESC).append("[0;1;31m");
		s.append('[');

		s.append(ESC).append("[1;37;44m");
		s.append(this.name());

		s.append(ESC).append("[0;1;31m");
		s.append(']');

		s.append(ESC).append("[0m");

		return s.toString();
	}
}
