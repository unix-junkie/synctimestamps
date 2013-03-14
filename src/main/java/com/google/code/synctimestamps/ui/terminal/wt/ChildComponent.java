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
public interface ChildComponent extends Component {
	Point getLocation();

	/**
	 * @param location
	 */
	void setLocation(@Nonnull final Point location);

	Dimension getSize();

	/**
	 * @param size
	 */
	void setSize(@Nonnull final Dimension size);

	Container getParent();
}
