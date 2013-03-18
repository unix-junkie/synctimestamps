/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import javax.annotation.Nullable;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.Point;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface Component {
	Point getLocation();

	Dimension getSize();

	/**
	 * @param border
	 */
	void setBorder(@Nullable final Border border);

	Color getForeground();

	Color getBackground();

	/**
	 * @param background
	 */
	void setBackground(@Nullable final Color background);

	char getBackgroundPattern();

	/**
	 * @param backgroundPattern
	 */
	void setBackgroundPattern(final char backgroundPattern);

	void paint();
}
