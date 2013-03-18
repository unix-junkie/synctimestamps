/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import javax.annotation.Nonnull;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public interface Container extends Component {
	boolean isTopLevel();

	/**
	 * @param child
	 */
	ComponentBuffer getComponentBuffer(@Nonnull final ChildComponent child);
}
