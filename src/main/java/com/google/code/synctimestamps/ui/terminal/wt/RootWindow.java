/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import static com.google.code.synctimestamps.ui.terminal.LineDrawingConstants.DOWN_AND_LEFT;
import static com.google.code.synctimestamps.ui.terminal.LineDrawingConstants.DOWN_AND_RIGHT;
import static com.google.code.synctimestamps.ui.terminal.LineDrawingConstants.HORIZONTAL;
import static com.google.code.synctimestamps.ui.terminal.LineDrawingConstants.UP_AND_LEFT;
import static com.google.code.synctimestamps.ui.terminal.LineDrawingConstants.UP_AND_RIGHT;
import static com.google.code.synctimestamps.ui.terminal.LineDrawingConstants.VERTICAL;
import static com.google.code.synctimestamps.ui.terminal.wt.BorderStyle.DOUBLE;
import static com.google.code.synctimestamps.ui.terminal.wt.BorderStyle.NONE;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.LineDrawingMethod;
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

	@Nonnull
	private final BorderStyle borderStyle = DOUBLE;

	private Color foreground;

	private final List<TextAttribute> foregroundAttributes = new ArrayList<TextAttribute>();

	private Color borderForeground;

	private final List<TextAttribute> borderForegroundAttributes = new ArrayList<TextAttribute>();

	private Color background;

	private final RootWindowBuffer buffer;

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

		this.buffer = new RootWindowBuffer(this.width, this.height);
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

	public void paint() {
		if (this.borderStyle != NONE) {
			final LineDrawingMethod lineDrawingMethod = this.term.getLineDrawingMethod();
			final boolean alternateCharset = lineDrawingMethod.isAlternateCharset();
			final char horizontal = lineDrawingMethod.getChar(HORIZONTAL, this.borderStyle);
			for (int i = 2; i <= this.width - 1; i++) {
				this.buffer.setTextAt(horizontal, i, 1, alternateCharset);
				this.buffer.setTextAt(horizontal, i, this.height, alternateCharset);
			}

			final char vertical = lineDrawingMethod.getChar(VERTICAL, this.borderStyle);
			for (int i = 2; i <= this.height - 1; i++) {
				this.buffer.setTextAt(vertical, 1, i, alternateCharset);
				this.buffer.setTextAt(vertical, this.width, i, alternateCharset);
			}

			this.buffer.setTextAt(lineDrawingMethod.getChar(DOWN_AND_RIGHT, this.borderStyle), 1, 1, alternateCharset);
			this.buffer.setTextAt(lineDrawingMethod.getChar(UP_AND_RIGHT, this.borderStyle), 1, this.height, alternateCharset);
			this.buffer.setTextAt(lineDrawingMethod.getChar(DOWN_AND_LEFT, this.borderStyle), this.width, 1, alternateCharset);
			this.buffer.setTextAt(lineDrawingMethod.getChar(UP_AND_LEFT, this.borderStyle), this.width, this.height, alternateCharset);
		}

		this.buffer.paint(this.term);
	}
}
