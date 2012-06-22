/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static java.lang.System.arraycopy;
import static java.util.Collections.emptyList;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class Terminal extends PrintWriter {
	private final TerminalType type;

	private final Reader in;

	/**
	 * @param ttyName
	 * @param term
	 * @throws FileNotFoundException
	 */
	protected Terminal(final String ttyName, final String term) throws FileNotFoundException {
		super(ttyName);
		this.type = TerminalType.safeValueOf(term);
		this.in = new FileReader(ttyName);
	}

	/**
	 * @throws IOException
	 */
	public int read() throws IOException {
		return this.in.read();
	}

	/**
	 * Splits the sequence of chars read into individual input events.
	 * This methodss allows individual keys to be identified whenever
	 * multiple keys are pressed simultaneously.
	 *
	 * @param sequence
	 * @todo split on all control characters, not only escape (see {@link #firstIndexOfControlChar(char[], int)}). Still, it is only Escape who can start a sequence.
	 */
	public List<InputEvent> split(final char sequence[]) {
		if (sequence.length == 0) {
			return emptyList();
		}

		final List<InputEvent> events = new ArrayList<InputEvent>();

		final int firstIndexOfEsc = firstIndexOf(sequence, InputEvent.ESC);
		switch (firstIndexOfEsc) {
		case -1:
			for (final char c : sequence) {
				events.add(new InputEvent(this.type, c));
			}
			break;
		case 0:
			final int secondIndexOfEsc = firstIndexOf(sequence, InputEvent.ESC, firstIndexOfEsc + 1);
			if (secondIndexOfEsc == -1) {
				events.add(new InputEvent(this.type, sequence));
			} else {
				final char head[] = new char[secondIndexOfEsc];
				final char tail[] = new char[sequence.length - secondIndexOfEsc];

				arraycopy(sequence, 0, head, 0, head.length);
				arraycopy(sequence, secondIndexOfEsc, tail, 0, tail.length);
				events.add(new InputEvent(this.type, head));
				events.addAll(this.split(tail));
			}
			break;
		default:
			final char head[] = new char[firstIndexOfEsc];
			final char tail[] = new char[sequence.length - firstIndexOfEsc];

			arraycopy(sequence, 0, head, 0, head.length);
			arraycopy(sequence, firstIndexOfEsc, tail, 0, tail.length);
			events.addAll(this.split(head));
			events.addAll(this.split(tail));
			break;
		}

		return events;
	}

	/**
	 * @see PrintWriter#close()
	 */
	@Override
	public final void close() {
		/*
		 * The reading while-loop should be modified
		 * before enabling this.
		 * Otherwise, an IOE: Stream closed is thrown.
		 */
		if (false) {
			try {
				this.in.close();
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			}
		}

		super.close();
	}

	/**
	 * @param sequence
	 * @param c
	 */
	private static int firstIndexOf(final char sequence[], final char c) {
		return firstIndexOf(sequence, c, 0);
	}

	/**
	 * @param sequence
	 * @param c
	 * @param fromIndex
	 * @return index of the first occurrence of <em>c</em> in
	 *         <em>sequence</em>, or <em>-1</em> if no such character
	 *         occurs in the char array.
	 */
	private static int firstIndexOf(final char sequence[], final char c, final int fromIndex) {
		for (int i = fromIndex; i < sequence.length; i++) {
			if (c == sequence[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param sequence
	 * @param fromIndex
	 */
	private static int firstIndexOfControlChar(final char sequence[], final int fromIndex) {
		for (int i = fromIndex; i < sequence.length; i++) {
			if (isControlCharacter(sequence[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param c
	 */
	public static boolean isControlCharacter(final char c) {
		return 0 <= c && c <= 31 || c == 127;
	}
}
