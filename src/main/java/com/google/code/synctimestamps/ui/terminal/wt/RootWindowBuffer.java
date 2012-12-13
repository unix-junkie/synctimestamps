/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.wt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.code.synctimestamps.ui.terminal.Color;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
final class RootWindowBuffer implements Iterable<ScreenCell> {
	final ScreenCell cells[][];

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
		this.setTextAt(text, x, y, alternateCharset, null);
	}

	public void setTextAt(final char text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			@Nonnull final Set<TextAttribute> attributes) {
		this.setTextAt(text, x, y, alternateCharset, foreground, TextAttribute.toArray(attributes));
	}

	public void setTextAt(final char text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			@Nonnull final TextAttribute ... attributes) {
		final ScreenCell currentCell = this.cells[y - 1][x - 1];
		currentCell.setText(text);
		currentCell.setAlternateCharset(alternateCharset);
		currentCell.setAttributes(attributes);

		if (foreground != null) {
			currentCell.setForeground(foreground);
		}
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
				/*
				 * Attributes should precede color settings.
				 */
				final Set<TextAttribute> attributes = currentCell.getAttributes();
				final boolean attributesChanged = previousCell == null || !previousCell.getAttributes().equals(attributes);
				if (attributesChanged) {
					term.setTextAttributes(attributes);
				}
				final Color foreground = currentCell.getForeground();
				if (attributesChanged || previousCell == null || previousCell.getForeground() != foreground) {
					term.setForeground(foreground);
				}
				final Color background = currentCell.getBackground();
				if (attributesChanged || previousCell == null || previousCell.getBackground() != background) {
					term.setBackground(background);
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

	/**
	 * @see Iterable#iterator()
	 */
	@Override
	public Iterator<ScreenCell> iterator() {
		return new ScreenCellIterator();
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 * @author $Author$
	 * @version $Revision$, $Date$
	 */
	private final class ScreenCellIterator implements Iterator<ScreenCell> {
		private int x = 1;

		private int y = 1;

		ScreenCellIterator() {
			// empty
		}

		/**
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.y != RootWindowBuffer.this.cells.length
					|| this.x != RootWindowBuffer.this.cells[this.y - 1].length;
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public ScreenCell next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			final ScreenCell current = RootWindowBuffer.this.cells[this.y - 1][this.x - 1];

			if (this.x == RootWindowBuffer.this.cells[this.y - 1].length) {
				/*
				 * We're at the last column already.
				 */
				this.x = 1;
				this.y += 1;
			} else {
				this.x += 1;
			}

			return current;
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
