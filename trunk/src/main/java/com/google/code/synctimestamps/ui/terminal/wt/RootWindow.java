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
import static com.google.code.synctimestamps.ui.terminal.wt.BorderStyle.DOUBLE_RAISED;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.LineDrawingMethod;
import com.google.code.synctimestamps.ui.terminal.Point;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class RootWindow implements Component, Container {
	private static final int MINIMUM_WIDTH = 2;

	private static final int MINIMUM_HEIGHT = 2;

	private final Terminal term;

	private Dimension size;

	private Dimension minimumSize = new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT);

	@Nullable
	private final String title;

	@Nonnull
	private final Color foreground;

	private final Set<TextAttribute> foregroundAttributes = noneOf(TextAttribute.class);

	@Nonnull
	private final BorderStyle borderStyle;

	@Nonnull
	private Color borderForeground;

	private final Set<TextAttribute> borderAttributes = noneOf(TextAttribute.class);

	@Nullable
	private ChildComponent contentPane;

	final RootWindowBuffer buffer;

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
		this(term, title, foreground, background, borderForeground, DOUBLE_RAISED);
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
		this.foreground = foreground;
		this.foregroundAttributes.addAll(asList(foregroundAttributes));
		this.borderStyle = borderStyle;
		this.setBorderForeground(borderForeground);

		this.buffer = new RootWindowBuffer(foreground, background, foregroundAttributes);
		this.resizeToTerm();
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
	 * @param minimumSize
	 */
	public void setMinimumSize(@Nonnull final Dimension minimumSize) {
		if (minimumSize.getWidth() < MINIMUM_WIDTH
				|| minimumSize.getHeight() < MINIMUM_HEIGHT) {
			throw new IllegalArgumentException();
		}

		this.minimumSize = minimumSize;
	}

	/**
	 * @param contentPane
	 */
	void setContentPane(@Nullable final ChildComponent contentPane) {
		this.contentPane = contentPane;
	}

	public void resizeToTerm() {
		if (this.term == null || this.buffer == null) {
			throw new IllegalStateException();
		}

		final Dimension reportedTermSize = this.term.getSize();
		final Dimension effectiveTermSize;
		if (reportedTermSize.isUndefined()) {
			final Dimension defaultTermSize = this.term.getDefaultSize();
			assert !defaultTermSize.isUndefined();
			effectiveTermSize = defaultTermSize;
		} else {
			effectiveTermSize = reportedTermSize;
		}

		this.size = new Dimension(max(effectiveTermSize.getWidth(), this.minimumSize.getWidth()),
				max(effectiveTermSize.getHeight(), this.minimumSize.getHeight()));
		this.buffer.setSize(effectiveTermSize);

		if (this.contentPane != null) {
			final boolean titleAbsent = this.title == null || this.title.length() == 0;
			this.contentPane.setLocation(this.borderStyle.isEmpty()
					? new Point(1, titleAbsent ? 1 : 2)
					: new Point(2, 2));
			this.contentPane.setSize(this.borderStyle.isEmpty()
					? titleAbsent
							? this.size
							: new Dimension(this.size.getWidth(), this.size.getHeight() - 1)
					: new Dimension(this.size.getWidth() - 2, this.size.getHeight() - 2));
		}
	}

	private void paintBorder() {
		if (this.borderStyle.isEmpty()) {
			return;
		}

		final Color topLeftForeground = this.borderStyle.isRaised()
				? this.borderForeground.brighter()
				: this.borderStyle.isLowered()
						? this.borderForeground.darker()
						: this.borderForeground;
		final Color bottomRightForeground = this.borderStyle.isRaised()
				? this.borderForeground.darker()
				: this.borderStyle.isLowered()
						? this.borderForeground.brighter()
						: this.borderForeground;

		final int width = this.size.getWidth();
		final int height = this.size.getHeight();
		final LineDrawingMethod lineDrawingMethod = this.term.getLineDrawingMethod();
		final boolean alternateCharset = lineDrawingMethod.isAlternateCharset();
		final char horizontal = lineDrawingMethod.getChar(HORIZONTAL, this.borderStyle);
		for (int i = 2; i <= width - 1; i++) {
			this.buffer.setTextAt(horizontal, i, 1, alternateCharset, topLeftForeground, null, this.borderAttributes);
			this.buffer.setTextAt(horizontal, i, height, alternateCharset, bottomRightForeground, null, this.borderAttributes);
		}

		final char vertical = lineDrawingMethod.getChar(VERTICAL, this.borderStyle);
		for (int i = 2; i <= height - 1; i++) {
			this.buffer.setTextAt(vertical, 1, i, alternateCharset, topLeftForeground, null, this.borderAttributes);
			this.buffer.setTextAt(vertical, width, i, alternateCharset, bottomRightForeground, null, this.borderAttributes);
		}

		this.buffer.setTextAt(lineDrawingMethod.getChar(DOWN_AND_RIGHT, this.borderStyle), 1, 1, alternateCharset, topLeftForeground, null, this.borderAttributes);
		this.buffer.setTextAt(lineDrawingMethod.getChar(UP_AND_RIGHT, this.borderStyle), 1, height, alternateCharset, bottomRightForeground, null, this.borderAttributes);
		this.buffer.setTextAt(lineDrawingMethod.getChar(DOWN_AND_LEFT, this.borderStyle), width, 1, alternateCharset, topLeftForeground, null, this.borderAttributes);
		this.buffer.setTextAt(lineDrawingMethod.getChar(UP_AND_LEFT, this.borderStyle), width, height, alternateCharset, bottomRightForeground, null, this.borderAttributes);
	}

	private void paintTitle() {
		if (this.title == null) {
			return;
		}
		final int titleLength = this.title.length();
		if (titleLength == 0) {
			return;
		}

		/*
		 * Window width minus 2 empty cells minus border corners.
		 */
		final int maximumTitleLength = max(0, this.size.getWidth() - 2 - (this.borderStyle.isEmpty() ? 0 : 2));
		final int effectiveTitleLength = min(titleLength, maximumTitleLength);
		final String effectiveTitle;
		switch (effectiveTitleLength) {
		case 0:
			/*
			 * Don't paint a title at all.
			 */
			return;
		case 1:
			/*
			 * Only the first title character can be painted.
			 */
			effectiveTitle = ' ' + this.title.substring(0, effectiveTitleLength) + ' ';
			break;
		default:
			/*-
			 * If the title doesn't fit, trim it and add the '>' at the end.
			 * If window width is odd and title length is even (or vice versa),
			 * add an extra space to the end of title (unless we're trimming it).
			 */
			effectiveTitle = ' ' + (titleLength == effectiveTitleLength
					? titleLength % 2 == this.size.getWidth() % 2
							? this.title
							: this.title + ' '
					: this.title.substring(0, effectiveTitleLength - 1) + '>') + ' ';
			break;
		}

		for (int i = 0; i < effectiveTitle.length(); i++) {
			this.buffer.setTextAt(effectiveTitle.charAt(i), i + (this.size.getWidth() - effectiveTitleLength) / 2, 1, false, this.foreground, null, this.foregroundAttributes);
		}
	}

	private void paintChildren() {
		if (this.contentPane != null) {
			this.contentPane.paint();
		}
	}

	/**
	 * @see Component#paint()
	 */
	@Override
	public void paint() {
		this.paintBorder();
		this.paintTitle();
		this.paintChildren();

		this.buffer.paint(this.term);
	}

	/**
	 * @see Container#isTopLevel()
	 */
	@Override
	public boolean isTopLevel() {
		return true;
	}

	/**
	 * @see Container#getComponentBuffer(ChildComponent)
	 */
	@Override
	public ComponentBuffer getComponentBuffer(@Nonnull final ChildComponent child) {
		if (child != this.contentPane) {
			throw new IllegalArgumentException();
		}

		return new AbstractComponentBuffer() {
			/**
			 * @see ComponentBuffer#setTextAt(char, int, int, boolean, Color, Color, TextAttribute[])
			 */
			@Override
			public void setTextAt(final char text, final int x, final int y, final boolean alternateCharset, final Color foreground, final Color background, @Nonnull final TextAttribute... attributes) {
				/*
				 * If the child component exceeds its bounds,
				 * then just clip it.
				 */
				if (x > child.getSize().getWidth() || y > child.getSize().getHeight()) {
					return;
				}

				final Point childLocation = child.getLocation();
				RootWindow.this.buffer.setTextAt(text, x + childLocation.getX() - 1, y + childLocation.getY() - 1, alternateCharset, foreground, background, attributes);
			}
		};
	}
}
