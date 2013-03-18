/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import static com.google.code.synctimestamps.ui.terminal.Dimension.UNDEFINED;
import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.Point;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public class Panel implements ChildComponent {
	@Nonnull
	private final Container parent;

	@Nonnull
	private Point location;

	@Nonnull
	private Dimension size = UNDEFINED;

	@Nullable
	protected Border border;

	@Nullable
	private Color foreground;

	private final Set<TextAttribute> foregroundAttributes = noneOf(TextAttribute.class);

	@Nullable
	private Color background;

	private char backgroundPattern = ' ';

	/**
	 * @param parent
	 */
	public Panel(@Nonnull final RootWindow parent) {
		this.parent = parent;
		parent.setContentPane(this);
	}

	/**
	 * @param parent
	 * @param weight
	 */
	public Panel(@Nonnull final PanelContainer parent, final double weight) {
		this.parent = parent;
		parent.add(this, weight);
	}

	/**
	 * @see Component#paint()
	 */
	@Override
	public final void paint() {
		if (this.size.isUndefined() || this.size.getWidth() == 0 || this.size.getHeight() == 0) {
			return;
		}

		if (this.parent.isTopLevel()) {
			final ComponentBuffer buffer = this.parent.getComponentBuffer(this);
			this.paintBackground(buffer);
			this.paint(buffer);
			if (this.border != null) {
				this.border.paintBorder(this, buffer);
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @param buffer
	 */
	private void paintBackground(final ComponentBuffer buffer) {
		for (int x = 1; x <= this.size.getWidth(); x++) {
			for (int y = 1; y <= this.size.getHeight(); y++) {
				buffer.setTextAt(this.backgroundPattern, x, y, false, this.getForeground(), this.getBackground(), this.foregroundAttributes);
			}
		}
	}

	/**
	 * @param buffer
	 */
	protected void paint(@SuppressWarnings("unused") final ComponentBuffer buffer) {
		// empty
	}

	/**
	 * @see ChildComponent#getParent()
	 */
	@Override
	public final Container getParent() {
		return this.parent;
	}

	/**
	 * @see Component#getLocation()
	 */
	@Override
	public final Point getLocation() {
		return this.location;
	}

	/**
	 * @see ChildComponent#setLocation(Point)
	 */
	@Override
	public final void setLocation(@Nonnull final Point location) {
		this.location = location;
	}

	/**
	 * @see Component#getSize()
	 */
	@Override
	public final Dimension getSize() {
		return this.size;
	}

	/**
	 * @see ChildComponent#setSize(Dimension)
	 */
	@Override
	public final void setSize(@Nonnull final Dimension size) {
		this.size = size;
	}

	/**
	 * @see ChildComponent#getBorder()
	 */
	@Override
	public final Border getBorder() {
		return this.border;
	}

	/**
	 * @see Component#setBorder(Border)
	 */
	@Override
	public final void setBorder(@Nullable final Border border) {
		this.border = border;
	}

	@Override
	public Color getForeground() {
		return this.foreground == null
				? this.getParent().getForeground()
				: this.foreground;
	}

	/**
	 * @param foreground
	 * @param foregroundAttributes
	 */
	public void setForeground(@Nullable final Color foreground,
			@Nonnull final TextAttribute ... foregroundAttributes) {
		this.foreground = foreground;
		this.setForegroundAttributes(foregroundAttributes);
	}

	/**
	 * @param foregroundAttributes
	 */
	public void setForegroundAttributes(@Nonnull final TextAttribute ... foregroundAttributes) {
		this.foregroundAttributes.clear();
		this.foregroundAttributes.addAll(asList(foregroundAttributes));
	}

	/**
	 * @see Component#getBackground()
	 */
	@Override
	public final Color getBackground() {
		return this.background == null
				? this.getParent().getBackground()
				: this.background;
	}

	/**
	 * @see Component#setBackground(Color)
	 */
	@Override
	public final void setBackground(@Nullable final Color background) {
		this.background = background;
	}

	/**
	 * @see Component#getBackgroundPattern()
	 */
	@Override
	public char getBackgroundPattern() {
		return this.backgroundPattern;
	}

	/**
	 * @see Component#setBackgroundPattern(char)
	 */
	@Override
	public void setBackgroundPattern(final char backgroundPattern) {
		this.backgroundPattern = backgroundPattern;
	}
}
