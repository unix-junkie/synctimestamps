/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.InputEvent.ESC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class VtCursorPosition implements VtResponse {
	public static final Pattern PATTERN = Pattern.compile("\\e\\[(\\d+)\\;(\\d+)R");

	private final int x;

	private final int y;

	/**
	 * @param event
	 */
	VtCursorPosition(final InputEvent event) {
		if (event == null) {
			throw new IllegalArgumentException("event is null");
		}

		final Matcher matcher = PATTERN.matcher(event);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(event.toString());
		}

		this.x = Integer.parseInt(matcher.group(2));
		this.y = Integer.parseInt(matcher.group(1));
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	/**
	 * The return value of this method can be returned by
	 * {@link InputEvent#toString()}.
	 *
	 * @see Object#toString()
	 * @see InputEvent#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();

		s.append(ESC).append("[0;1;31m");
		s.append('[');

		/*
		 * White on cyan.
		 */
		s.append(ESC).append("[1;37;45m");
		s.append("Cursor position: +").append(this.x).append('+').append(this.y);

		s.append(ESC).append("[0;1;31m");
		s.append(']');

		s.append(ESC).append("[0m");

		return s.toString();
	}
}
