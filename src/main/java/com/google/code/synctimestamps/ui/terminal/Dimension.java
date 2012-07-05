/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class Dimension {
	private final int width;

	private final int height;

	/**
	 * @param width
	 * @param height
	 */
	public Dimension(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return this.width + "x" + this.height;
	}
}
