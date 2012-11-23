/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class RootWindow {
	private final Terminal term;

	private final int width;

	private final int height;

	private BorderStyle borderStyle;

	private Color foreground;

	private final List<TextAttribute> foregroundAttributes = new ArrayList<TextAttribute>();

	private Color borderForeground;

	private final List<TextAttribute> borderForegroundAttributes = new ArrayList<TextAttribute>();

	private Color background;

	/**
	 * @param term
	 */
	public RootWindow(final Terminal term) {
		this.term = term;

		final Dimension size = term.getSize();
		if (size.isUndefined()) {
			final Dimension defaultSize = term.getDefaultSize();
			assert !defaultSize.isUndefined();
			this.width = defaultSize.getWidth();
			this.height = defaultSize.getHeight();
		} else {
			this.width = size.getWidth();
			this.height = size.getHeight();
		}
	}

	public void setForeground(@Nonnull final Color foreground,
			@Nonnull final TextAttribute ... foregroundAttributes) {
		this.foreground = foreground;
		this.foregroundAttributes.clear();
		this.foregroundAttributes.addAll(asList(foregroundAttributes));
	}

	public void setBorderForeground(@Nonnull final Color borderForeground,
			@Nonnull final TextAttribute ... borderForegroundAttributes) {
		this.borderForeground = borderForeground;
		this.borderForegroundAttributes.clear();
		this.borderForegroundAttributes.addAll(asList(borderForegroundAttributes));
	}

	public void setBackground(@Nonnull final Color background) {
		this.background = background;
	}

	/**
	 * @todo cygwin and sun-color (at least) do auto-wraparound, so the last line is not usable (xterm, rxvt, linux, dtterm, vtnt and vt320 are fine)
	 */
	public void paint() {
		for (int i = 2; i <= this.width - 1; i++) {
			this.term.setCursorLocation(i, 1);
			this.term.print('-');
			this.term.setCursorLocation(i, this.height);
			this.term.print('-');
		}

		for (int i = 2; i <= this.height - 1; i++) {
			this.term.setCursorLocation(1, i);
			this.term.print('|');
			this.term.setCursorLocation(this.width, i);
			this.term.print('|');
		}

		this.term.setCursorLocation(1, 1);
		this.term.print('+');
		this.term.setCursorLocation(1, this.height);
		this.term.print('+');
		this.term.setCursorLocation(this.width, 1);
		this.term.print('+');
		this.term.setCursorLocation(this.width, this.height);
		this.term.print('+');

		this.term.setCursorLocation(1, 1);

		this.term.flush();
	}
}
