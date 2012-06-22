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
public abstract class VtResponse implements VtKeyOrResponse {
	/**
	 * @param s
	 */
	protected abstract StringBuilder appendDescription(final StringBuilder s);

	/**
	 * The return value of this method can be returned by
	 * {@link InputEvent#toString()}.
	 *
	 * @see Object#toString()
	 * @see InputEvent#toString()
	 */
	@Override
	public final String toString() {
		final StringBuilder s = new StringBuilder();

		s.append(ESC).append("[0;1;31m");
		s.append('[');

		/*
		 * White on cyan.
		 */
		s.append(ESC).append("[1;37;45m");
		this.appendDescription(s);

		s.append(ESC).append("[0;1;31m");
		s.append(']');

		s.append(ESC).append("[0m");

		return s.toString();
	}
}
