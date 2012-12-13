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
import static com.google.common.base.Functions.compose;
import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.LineDrawingMethod;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;
import com.google.common.base.Function;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class RootWindow {
	private final Terminal term;

	private final int width;

	private final int height;

	@Nullable
	private final String title;

	@Nonnull
	private final BorderStyle borderStyle;

	@Nonnull
	private Color borderForeground;

	private final Set<TextAttribute> borderAttributes = noneOf(TextAttribute.class);

	private final RootWindowBuffer buffer;

	/**
	 * @param term
	 */
	public RootWindow(@Nonnull final Terminal term) {
		this(term, null, term.getDefaultForeground(), term.getDefaultBackground());
	}

	/**
	 * @param term
	 * @param title
	 * @param foreground
	 * @param background
	 */
	public RootWindow(@Nonnull final Terminal term,
			@Nullable final String title,
			@Nonnull final Color foreground,
			@Nonnull final Color background) {
		this(term, title, foreground, background, foreground);
	}

	/**
	 * @param term
	 * @param title
	 * @param foreground
	 * @param background
	 * @param borderForeground
	 */
	public RootWindow(@Nonnull final Terminal term,
			@Nullable final String title,
			@Nonnull final Color foreground,
			@Nonnull final Color background,
			@Nonnull final Color borderForeground) {
		this(term, title, foreground, background, borderForeground, DOUBLE);
	}

	/**
	 * @param term
	 * @param title
	 * @param foreground
	 * @param background
	 * @param borderForeground
	 * @param borderStyle
	 * @param foregroundAttributes
	 */
	public RootWindow(@Nonnull final Terminal term,
			@Nullable final String title,
			@Nonnull final Color foreground,
			@Nonnull final Color background,
			@Nonnull final Color borderForeground,
			@Nonnull final BorderStyle borderStyle,
			@Nonnull final TextAttribute ... foregroundAttributes) {
		this.term = term;
		this.title = title;
		this.borderStyle = borderStyle;
		this.setBorderForeground(borderForeground);

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
		for (@SuppressWarnings("unused") final ScreenCell screenCell : transform(this.buffer,
				compose(setForeground(foreground, foregroundAttributes),
				setBackground(background)))) {
			// Empty: only required to repeatedly apply the function.
		}
	}

	/**
	 * @param foreground
	 * @param foregroundAttributes
	 */
	private static Function<ScreenCell, ScreenCell> setForeground(@Nonnull final Color foreground,
			@Nonnull final TextAttribute ... foregroundAttributes) {
		return new Function<ScreenCell, ScreenCell>() {
			/**
			 * @see Function#apply
			 */
			@Override
			public ScreenCell apply(final ScreenCell from) {
				from.setForeground(foreground);
				from.setAttributes(foregroundAttributes);
				return from;
			}
		};
	}

	/**
	 * @param borderForeground
	 * @param borderAttributes
	 */
	public void setBorderForeground(@Nonnull final Color borderForeground,
			@Nonnull final TextAttribute ... borderAttributes) {
		this.borderForeground = borderForeground;
		this.setBorderAttributes(borderAttributes);
	}

	/**
	 * @param borderAttributes
	 */
	public void setBorderAttributes(@Nonnull final TextAttribute ... borderAttributes) {
		this.borderAttributes.clear();
		this.borderAttributes.addAll(asList(borderAttributes));
	}

	/**
	 * @param background
	 */
	private static Function<ScreenCell, ScreenCell> setBackground(@Nonnull final Color background) {
		return new Function<ScreenCell, ScreenCell>() {
			/**
			 * @see Function#apply
			 */
			@Override
			public ScreenCell apply(final ScreenCell from) {
				from.setBackground(background);
				return from;
			}
		};
	}

	public void paint() {
		if (this.borderStyle != NONE) {
			final LineDrawingMethod lineDrawingMethod = this.term.getLineDrawingMethod();
			final boolean alternateCharset = lineDrawingMethod.isAlternateCharset();
			final char horizontal = lineDrawingMethod.getChar(HORIZONTAL, this.borderStyle);
			for (int i = 2; i <= this.width - 1; i++) {
				this.buffer.setTextAt(horizontal, i, 1, alternateCharset, this.borderForeground, this.borderAttributes);
				this.buffer.setTextAt(horizontal, i, this.height, alternateCharset, this.borderForeground, this.borderAttributes);
			}

			final char vertical = lineDrawingMethod.getChar(VERTICAL, this.borderStyle);
			for (int i = 2; i <= this.height - 1; i++) {
				this.buffer.setTextAt(vertical, 1, i, alternateCharset, this.borderForeground, this.borderAttributes);
				this.buffer.setTextAt(vertical, this.width, i, alternateCharset, this.borderForeground, this.borderAttributes);
			}

			this.buffer.setTextAt(lineDrawingMethod.getChar(DOWN_AND_RIGHT, this.borderStyle), 1, 1, alternateCharset, this.borderForeground, this.borderAttributes);
			this.buffer.setTextAt(lineDrawingMethod.getChar(UP_AND_RIGHT, this.borderStyle), 1, this.height, alternateCharset, this.borderForeground, this.borderAttributes);
			this.buffer.setTextAt(lineDrawingMethod.getChar(DOWN_AND_LEFT, this.borderStyle), this.width, 1, alternateCharset, this.borderForeground, this.borderAttributes);
			this.buffer.setTextAt(lineDrawingMethod.getChar(UP_AND_LEFT, this.borderStyle), this.width, this.height, alternateCharset, this.borderForeground, this.borderAttributes);
		}

		this.buffer.paint(this.term);
	}
}
