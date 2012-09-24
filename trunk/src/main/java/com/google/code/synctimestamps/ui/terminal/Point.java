/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class Point {
	public static final Point UNDEFINED = new Point(-1, -1);

	private final int x;

	private final int y;

	/**
	 * @param x
	 * @param y
	 */
	public Point(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param cursorPosition
	 */
	public Point(final VtCursorLocation cursorPosition) {
		this.x = cursorPosition.getX();
		this.y = cursorPosition.getY();
	}

	public int getX() {
		return this.x;
	}

	public int getHeight() {
		return this.y;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return (this.x >= 0 ? "+" : "") + this.x
				+ (this.y >= 0 ? "+" : "") + this.y;
	}
}
