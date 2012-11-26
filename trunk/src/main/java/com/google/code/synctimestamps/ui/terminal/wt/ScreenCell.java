/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
final class ScreenCell {
	@Nonnull
	private char text;

	private Color foreground;

	private Color background;

	private TextAttribute attributes[];

	ScreenCell() {
		/*
		 * Print a space by default, since not all terminals can
		 * use the background color in order to erase the scereen.
		 */
		this(' ');
	}

	ScreenCell(final char text) {
		this.text = text;
	}

	public char getText() {
		return this.text;
	}

	/**
	 * @param text
	 */
	public void setText(final char text) {
		this.text = text;
	}

	/**
	 * @param foreground
	 */
	public void setForeground(final Color foreground) {
		this.foreground = foreground;
	}

	/**
	 * @param background
	 */
	public void setBackground(final Color background) {
		this.background = background;
	}

	/**
	 * @param attributes
	 */
	public void setAttributes(final TextAttribute ... attributes) {
		this.attributes = attributes;
	}
}
