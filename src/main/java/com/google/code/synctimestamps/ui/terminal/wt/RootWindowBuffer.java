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
		this.setTextAt(text, x, y, false);
	}

	public void setTextAt(final char text, final int x, final int y, final boolean alternateCharset) {
		final ScreenCell currentCell = this.cells[y - 1][x - 1];
		currentCell.setText(text);
		currentCell.setAlternateCharset(alternateCharset);
	}

	public void paint(final Terminal term) {
		for (int y = 1, m = this.cells.length; y <= m; y++) {
			term.setCursorLocation(1, y);

			for (int x = 1, n = this.cells[y - 1].length; x <= n; x++) {
				if (x == n && y == m && !term.getType().canUpdateLowerRightCell()) {
					continue;
				}

				final ScreenCell currentCell = this.cells[y - 1][x - 1];
				final ScreenCell previousCell = this.findPrevious(x, y);
				final boolean alternateCharsetNeeded = currentCell.isAlternateCharset();
				if ((previousCell == null || previousCell.isAlternateCharset()) && !alternateCharsetNeeded) {
					term.stopAlternateCs();
				} else if ((previousCell == null || !previousCell.isAlternateCharset()) && alternateCharsetNeeded) {
					term.startAlternateCs();
				}
				term.print(currentCell.getText());
			}
		}

		/*
		 * A safety net so that the next output is not messed up.
		 */
		term.stopAlternateCs();

		term.flush();
	}

	/**
	 * @param x the 1-based x coordinate
	 * @param y the 1-based y coordinate
	 * @return the previous cell in this buffer, or {@code null} if none found.
	 */
	private ScreenCell findPrevious(final int x, final int y) {
		if (x == 1 && y == 1) {
			/*
			 * Return null is current cell is the top left corner.
			 */
			return null;
		}

		final int previousX;
		final int previousY;
		if (x == 1) {
			/*
			 * We're at the first column already.
			 */
			previousX = this.cells[y - 1].length;
			previousY = y - 1;
		} else {
			previousX = x - 1;
			previousY = y;
		}

		return this.cells[previousY - 1][previousX - 1];
	}
}
