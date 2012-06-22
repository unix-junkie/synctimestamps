/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import java.util.Arrays;

/**
 * An unparsed sequence of chars from terminal response.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class InputEvent implements CharSequence {
	public static final char ESC = '\u001B';

	private final TerminalType terminalType;

	private final char data[];

	/**
	 * @param data
	 */
	InputEvent(final TerminalType terminalType, final char ... data) {
		if (data.length == 0) {
			throw new IllegalArgumentException();
		}

		this.terminalType = terminalType;
		this.data = data;
	}

	private static CharSequence toHumanReadable(final char c) {
		final StringBuilder s = new StringBuilder();

		/*
		 * Try to exclude most of the control characters.
		 */
		if (0 <= c && c <= 31) {
			/*
			 * White on green
			 */
			s.append(ESC).append("[1;37;42m");

			/*
			 * Those definitely are the control characters.
			 */
			s.append("^" + (char) ('@' + c));
			switch (c) {
			case 8:
				s.append("/BackSpace");
				break;
			case 9:
				s.append("/Tab");
				break;
			case 13:
				s.append("/Enter");
				break;
			case 27:
				s.append("/Escape");
				break;
			}
		} else if (c == 127) {
			/*
			 * White on green
			 */
			s.append(ESC).append("[1;37;45m");

			s.append((int) c).append("/Delete");
		} else {
			/*
			 * Most probably, alphanumeric or punctuation.
			 */
			s.append(c);
		}

		return s;
	}

	private boolean isControlCharacter() {
		return this.data.length == 1 && Terminal.isControlCharacter(this.data[0]);
	}

	/**
	 * @param c
	 */
	public boolean isControlWith(final char c) {
		return this.isControlCharacter() && '@' + this.data[0] == c;
	}

	/**
	 * @param data
	 */
	public boolean isEscapeSequence() {
		return this.data.length > 1 && this.data[0] == ESC;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		if (this.terminalType.isKnownEscapeSequence(this)) {
			return this.terminalType.getVtKeyOrResponse(this).toString();
		}

		/*
		 * Fallback implementation.
		 */
		final StringBuilder s = new StringBuilder();

		s.append(ESC).append("[0;1;31m");
		s.append('[');
		s.append(ESC).append("[0m");

		for (final char c : this.data) {
			s.append(toHumanReadable(c));
			s.append(' ');
		}
		if (this.data.length > 0) {
			s.deleteCharAt(s.length() - 1);
		}

		s.append(ESC).append("[0;1;31m");
		s.append(']');
		s.append(ESC).append("[0m");

		return s.toString();
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof InputEvent
				&& Arrays.equals(this.data, ((InputEvent) obj).data);
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}

	/**
	 * @see CharSequence#length()
	 */
	@Override
	public int length() {
		return this.data.length;
	}

	/**
	 * @see CharSequence#charAt(int)
	 */
	@Override
	public char charAt(final int index) {
		return this.data[index];
	}

	/**
	 * @see CharSequence#subSequence(int, int)
	 */
	@Override
	public CharSequence subSequence(final int start, final int end) {
		return start == 0 && end == this.data.length
				? this
				: String.valueOf(this.data).substring(start, end);
	}
}
