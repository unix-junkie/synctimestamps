/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Insets;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface Border {
	/**
	 * @param component
	 * @param buffer
	 * @param term
	 */
	void paintBorder(@Nonnull final Component component,
			@Nonnull final ComponentBuffer buffer);

	Insets getBorderInsets();
}
