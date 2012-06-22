/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.InputEvent.ESC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Terminal size response looks like {@code ^[[8;24;80t}.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class VtTerminalSize implements VtResponse {
	public static final Pattern PATTERN = Pattern.compile("\\e\\[8\\;(\\d+)\\;(\\d+)t");

	private final int width;

	private final int height;

	/**
	 * @param event
	 */
	VtTerminalSize(final InputEvent event) {
		if (event == null) {
			throw new IllegalArgumentException("event is null");
		}

		final Matcher matcher = PATTERN.matcher(event);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(event.toString());
		}

		this.width = Integer.parseInt(matcher.group(2));
		this.height = Integer.parseInt(matcher.group(1));
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
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
		s.append("Terminal size: ").append(this.width).append('x').append(this.height);

		s.append(ESC).append("[0;1;31m");
		s.append(']');

		s.append(ESC).append("[0m");

		return s.toString();
	}
}
