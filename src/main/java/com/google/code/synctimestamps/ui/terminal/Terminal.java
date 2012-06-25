/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class Terminal extends PrintWriter {
	private final TerminalType type;

	private final Reader in;

	final Thread sequenceTokenizer;

	/**
	 * @param ttyName
	 * @param term
	 * @param handler
	 * @throws FileNotFoundException
	 */
	protected Terminal(final String ttyName, final String term, final InputEventHandler handler)
	throws FileNotFoundException {
		super(ttyName);
		this.type = TerminalType.safeValueOf(term);
		this.in = new FileReader(ttyName);
		this.sequenceTokenizer = new SequenceTokenizer(this, handler);
	}

	public void start() {
		this.sequenceTokenizer.start();
	}

	/**
	 * @throws IOException
	 */
	public int read() throws IOException {
		return this.in.read();
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

	public TerminalType getType() {
		return this.type;
	}
}
