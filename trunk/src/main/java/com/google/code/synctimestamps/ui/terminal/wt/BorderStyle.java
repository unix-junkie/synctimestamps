/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import static com.google.code.synctimestamps.ui.terminal.wt.BorderStyle.Style.FLAT;
import static com.google.code.synctimestamps.ui.terminal.wt.BorderStyle.Style.LOWERED;
import static com.google.code.synctimestamps.ui.terminal.wt.BorderStyle.Style.RAISED;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public enum BorderStyle {
	NONE(		true, 	FLAT),
	SINGLE(		false, 	FLAT),
	SINGLE_RAISED(	false,	RAISED),
	SINGLE_LOWERED(	false,	LOWERED),
	DOUBLE(		false,	FLAT),
	DOUBLE_RAISED(	false,	RAISED),
	DOUBLE_LOWERED(	false,	LOWERED),
	;

	private final boolean empty;

	private final Style style;

	/**
	 * @param empty
	 * @param style
	 */
	private BorderStyle(final boolean empty, final Style style) {
		this.empty = empty;
		this.style = style;
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public boolean isRaised() {
		return this.style == RAISED;
	}

	public boolean isLowered() {
		return this.style == LOWERED;
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 * @author $Author$
	 * @version $Revision$, $Date$
	 */
	static enum Style {
		FLAT,
		RAISED,
		LOWERED,
		;
	}
}
