/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.Point;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public abstract class Panel implements ChildComponent {
	@Nonnull
	Container parent;

	@Nonnull
	Point location;

	@Nonnull
	Dimension size;

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
		if (this.parent.isTopLevel()) {
			final ComponentBuffer buffer = this.parent.getComponentBuffer(this);
			this.paint(buffer);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	protected abstract void paint(final ComponentBuffer buffer);

	/**
	 * @see ChildComponent#getParent()
	 */
	@Override
	public final Container getParent() {
		return this.parent;
	}

	/**
	 * @see ChildComponent#getLocation()
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
	 * @see ChildComponent#getSize()
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
}
