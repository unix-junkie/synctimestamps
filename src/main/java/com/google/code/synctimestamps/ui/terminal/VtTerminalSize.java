/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Terminal size response looks like {@code ^[[8;24;80t}.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class VtTerminalSize extends VtResponse {
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
	 * @see VtResponse#appendDescription(StringBuilder)
	 */
	@Override
	protected StringBuilder appendDescription(final StringBuilder s) {
		return s.append("Terminal size: ").append(this.width).append('x').append(this.height);
	}
}
