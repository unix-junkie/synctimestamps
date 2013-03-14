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
public final class PanelContainer implements ChildComponent, Container {
	public void add(@Nonnull final ChildComponent component, final double weight) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#paint()
	 */
	@Override
	public void paint() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#setLocation(Point)
	 */
	@Override
	public void setLocation(@Nonnull final Point location) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#getSize()
	 */
	@Override
	public Dimension getSize() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#setSize(Dimension)
	 */
	@Override
	public void setSize(@Nonnull final Dimension size) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Container#isTopLevel()
	 */
	@Override
	public boolean isTopLevel() {
		return false;
	}

	/**
	 * @see Container#getComponentBuffer(ChildComponent)
	 */
	@Override
	public ComponentBuffer getComponentBuffer(@Nonnull final ChildComponent child) {
		if (child.getParent() != this) {
			throw new IllegalArgumentException();
		}

		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#getParent()
	 */
	@Override
	public Container getParent() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#getLocation()
	 */
	@Override
	public Point getLocation() {
		throw new UnsupportedOperationException();
	}
}
