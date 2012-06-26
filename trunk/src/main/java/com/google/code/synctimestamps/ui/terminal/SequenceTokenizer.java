/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static java.lang.System.arraycopy;
import static java.util.Collections.emptyList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class SequenceTokenizer extends Thread {
	/**
	 * <p>The threshold, in milliseconds, which, once exceeded,
	 * means that a new input event has occurred.</p>
	 *
	 * <p>The maximum delay observed between the read operations
	 * within a single escape sequence is 1 ms. The minimum delay
	 * observed between two separate escape sequences is 89 ms.</p>
	 *
	 * <p>Additionally, even a value of <em>1</em> doesn't prevent
	 * multiple keys pressed simultaneously
	 * from being interpreted as a single sequence.</p>
	 *
	 * <p>So the value of 45 ms is a fair trade.</p>
	 */
	private static final long INPUT_EVENT_THRESHOLD = 45;

	/**
	 * Despite a regular escape sequence length rarely exceeds 7,
	 * we should account for situations when multiple keys
	 * are pressed simultaneously.
	 */
	private static final int MAX_SEQUENCE_LENGTH = 1024;


	final Terminal term;

	private final InputEventHandler handler;

	final char sequence[] = new char[MAX_SEQUENCE_LENGTH];

	final Object sequenceLock = new Object();

	int sequencePositionMarker;


	/**
	 * @param term
	 * @param handler
	 */
	public SequenceTokenizer(final Terminal term, final InputEventHandler handler) {
		super("SequenceTokenizer");

		this.term = term;
		this.handler = handler;
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				/*
				 * Clear the interrupted status and sleep.
				 */
				interrupted();
				sleep(INPUT_EVENT_THRESHOLD);

				/*
				 * No new character has been read
				 * within the timeout -- process the sequence.
				 */
				final char sequenceClone[];
				synchronized (this.sequenceLock) {
					/*
					 * The sequence is empty.
					 */
					if (this.sequencePositionMarker == 0) {
						continue;
					}

					sequenceClone = new char[this.sequencePositionMarker];
					for (int j = 0; j < this.sequencePositionMarker; j++) {
						sequenceClone[j] = this.sequence[j];
					}
					this.sequencePositionMarker = 0;
				}

				this.handler.handle(this.term, split(sequenceClone, this.term.getType()));
			} catch (final InterruptedException ie) {
				/*
				 * New character has been read --
				 * continuing from the beginning.
				 */
				continue;
			}
		}
	}

	/**
	 * @see Thread#start()
	 */
	@Override
	public synchronized void start() {
		super.start();

		if (this.handler != null) {
			this.handler.printUsage(this.term);
		}

		/*
		 * This is the second thread which produces char sequences
		 * consumed by the tokenizer.
		 */
		final Runnable sequenceProducer = new Runnable() {
			/**
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				int i;
				try {
					while (/* term.isOpen() && */ (i = SequenceTokenizer.this.term.read()) != -1) {
						/*
						 * 1. Interrupt the tokenizer,
						 * so that a new cycle is started.
						 */
						SequenceTokenizer.this.interrupt();

						/*
						 * 2. Append the character to the sequence.
						 */
						final char c = (char) i;
						synchronized (SequenceTokenizer.this.sequenceLock) {
							if (SequenceTokenizer.this.sequencePositionMarker < SequenceTokenizer.this.sequence.length) {
								SequenceTokenizer.this.sequence[SequenceTokenizer.this.sequencePositionMarker++] = c;
							}
						}
					}
				} catch (final IOException ioe) {
					/*
					 * Never.
					 */
					ioe.printStackTrace();
				}
			}
		};
		final Thread thread = new Thread(sequenceProducer, "SequenceProducer");
		thread.start();
	}

	/**
	 * Splits the sequence of chars read into individual input events.
	 * This method allows individual keys to be identified whenever
	 * multiple keys are pressed simultaneously.
	 *
	 * @param sequence
	 * @param type
	 */
	static List<InputEvent> split(final char sequence[], final TerminalType type) {
		if (sequence.length == 0) {
			return emptyList();
		}

		final List<InputEvent> events = new ArrayList<InputEvent>();

		final int firstIndexOfEsc = firstIndexOf(sequence, InputEvent.ESC);
		switch (firstIndexOfEsc) {
		case -1:
			/*
			 * The char sequence doesn't contain any escape sequences.
			 */
			for (final char c : sequence) {
				events.add(new InputEvent(type, c));
			}
			break;
		case 0:
			/*
			 * The char sequence starts with an escape sequence.
			 */
			final int secondIndexOfCtrl = firstIndexOfControlChar(sequence, firstIndexOfEsc + 1);
			if (secondIndexOfCtrl == -1) {
				events.add(new InputEvent(type, sequence));
			} else {
				/*
				 * The first escape sequence (up to the next control character, exclusive).
				 */
				final char head[] = new char[secondIndexOfCtrl];
				/*
				 * The rest of the sequence, starting with a control character
				 */
				final char tail[] = new char[sequence.length - secondIndexOfCtrl];

				arraycopy(sequence, 0, head, 0, head.length);
				arraycopy(sequence, secondIndexOfCtrl, tail, 0, tail.length);
				events.add(new InputEvent(type, head));
				events.addAll(split(tail, type));
			}
			break;
		default:
			/*
			 * The char sequence starts with a normal or control character (excluding escape),
			 * and contains one or more escape sequence(s) starting at any index (excluding 0).
			 *
			 * The head doesn't contain any escape sequences.
			 */
			final char head[] = new char[firstIndexOfEsc];
			/*
			 * The tail starts with an escape sequence.
			 */
			final char tail[] = new char[sequence.length - firstIndexOfEsc];

			arraycopy(sequence, 0, head, 0, head.length);
			arraycopy(sequence, firstIndexOfEsc, tail, 0, tail.length);
			events.addAll(split(head, type));
			events.addAll(split(tail, type));
			break;
		}

		return events;
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
			if (InputEvent.isControlCharacter(sequence[i])) {
				return i;
			}
		}
		return -1;
	}
}
