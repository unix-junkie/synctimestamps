/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class VtCursorLocation extends VtResponse {
	public static final Pattern PATTERN = Pattern.compile("\\e\\[(\\d+)\\;(\\d+)R");

	private final int x;

	private final int y;

	/**
	 * @param event
	 */
	VtCursorLocation(final InputEvent event) {
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
	 * @see VtResponse#appendDescription(StringBuilder)
	 */
	@Override
	protected StringBuilder appendDescription(final StringBuilder s) {
		return s.append("Cursor location: +").append(this.x).append('+').append(this.y);
	}
}
