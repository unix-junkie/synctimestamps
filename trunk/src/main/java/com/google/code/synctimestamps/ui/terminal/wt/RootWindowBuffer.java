/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import com.google.code.synctimestamps.ui.terminal.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
final class RootWindowBuffer {
	private final ScreenCell cells[][];

	RootWindowBuffer(final int width, final int height) {
		this.cells = new ScreenCell[height][width];
		for (int y = 1, m = this.cells.length; y <= m; y++) {
			for (int x = 1, n = this.cells[y - 1].length; x <= n; x++) {
				this.cells[y - 1][x - 1] = new ScreenCell();
			}
		}
	}

	public void setTextAt(final char text, final int x, final int y) {
		this.cells[y - 1][x - 1].setText(text);
	}

	public void paint(final Terminal term) {
		term.setCursorLocation(1, 1);

		for (int y = 1, m = this.cells.length; y <= m; y++) {
			for (int x = 1, n = this.cells[y - 1].length; x <= n; x++) {
				if (x == n && y == m && !term.getType().canUpdateLowerRightCell()) {
					continue;
				}
				term.print(this.cells[y - 1][x - 1].getText());
			}
		}

		term.flush();
	}
}
