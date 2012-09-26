/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import com.google.code.synctimestamps.ui.terminal.InputEventHandler;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
abstract class AbstractInputEventHandler implements InputEventHandler {
	InputEventHandler next;

	/**
	 * @param next
	 */
	AbstractInputEventHandler(final InputEventHandler next) {
		this.setNext(next);
	}

	/**
	 * @param next
	 */
	void setNext(final InputEventHandler next) {
		if (this.next != null && this.next != next) {
			throw new IllegalStateException();
		}
		this.next = next;
	}

	/**
	 * @see InputEventHandler#append(InputEventHandler)
	 */
	@Override
	public final InputEventHandler append(final InputEventHandler next0) {
		/*
		 * We need to return a non-null instance, hence the check.
		 */
		if (next0 == null) {
			throw new IllegalArgumentException();
		}

		if (this.next == null) {
			this.setNext(next0);
		} else {
			this.next.append(next0);
		}

		return this;
	}
}
