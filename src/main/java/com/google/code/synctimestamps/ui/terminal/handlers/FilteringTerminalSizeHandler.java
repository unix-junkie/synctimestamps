/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal.handlers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.code.synctimestamps.ui.terminal.Dimension;
import com.google.code.synctimestamps.ui.terminal.InputEvent;
import com.google.code.synctimestamps.ui.terminal.InputEventHandler;
import com.google.code.synctimestamps.ui.terminal.Terminal;
import com.google.code.synctimestamps.ui.terminal.TerminalSizeProvider;
import com.google.code.synctimestamps.ui.terminal.TerminalType;
import com.google.code.synctimestamps.ui.terminal.VtKeyOrResponse;
import com.google.code.synctimestamps.ui.terminal.VtTerminalSize;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class FilteringTerminalSizeHandler implements InputEventHandler, TerminalSizeProvider {
	/**
	 * Terminal emulator on the same host: ~50 ms.<br>
	 * Local area connection: ~220 ms.
	 */
	private static long DEFAULT_EXPECTING_TIMEOUT_MILLIS = 250;

	private final InputEventHandler next;

	final Object expectingTerminalSizeLock = new Object();

	/**
	 * The moment (in milliseconds) when {@link
	 * #setExpectingTerminalSize(boolean) expectingTerminalSize}
	 * was set to {@code true}. If this value is {@code 0L}, then
	 * {@link #isExpectingTerminalSize() expectingTerminalSize} flag
	 * is {@code false}.
	 */
	private long t0;

	final long expectingTimeoutMillis;

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	/**
	 * @param next
	 * @param expectingTimeoutMillis
	 */
	public FilteringTerminalSizeHandler(final InputEventHandler next, final long expectingTimeoutMillis) {
		this.next = next;
		this.expectingTimeoutMillis = expectingTimeoutMillis;
	}

	public FilteringTerminalSizeHandler(final InputEventHandler next) {
		this(next, DEFAULT_EXPECTING_TIMEOUT_MILLIS);
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		synchronized (this.expectingTerminalSizeLock) {
			if (this.isExpectingTerminalSize()) {
				final Iterator<InputEvent> it = events.iterator();
				while (it.hasNext()) {
					final InputEvent event = it.next();
					final TerminalType type = term.getType();
					if (type.isKnownEscapeSequence(event)) {
						 final VtKeyOrResponse vtKeyOrResponse = type.getVtKeyOrResponse(event);
						 if (vtKeyOrResponse instanceof VtTerminalSize) {
							 final VtTerminalSize terminalSize = (VtTerminalSize) vtKeyOrResponse;
							 it.remove();

							 final int width = terminalSize.getWidth();
							 final int height = terminalSize.getHeight();
							 final long t1 = System.currentTimeMillis();
							 term.println("Terminal size of " + width + 'x' + height + " reported " + (t1 - this.t0) + " ms after the request.");

							 /*
							  * Reset the "expectingTerminalSize" status.
							  */
							 this.setExpectingTerminalSize(false, term);

							 /*
							  * We're expecting only a single terminal size event.
							  */
							 break;
						 }
					}
				}
			}
		}

		if (this.next != null) {
			this.next.handle(term, events);
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		if (this.next != null) {
			this.next.printUsage(term);
		}
	}

	/**
	 * @see TerminalSizeProvider#getTerminalSize(Terminal)
	 */
	@Override
	public Dimension getTerminalSize(final Terminal term) {
		this.setExpectingTerminalSize(true, term);
		term.requestTerminalSize();
		term.flush();
		return null;
	}

	/**
	 * @param expectingTerminalSize
	 * @param term
	 */
	void setExpectingTerminalSize(final boolean expectingTerminalSize, final Terminal term) {
		synchronized (this.expectingTerminalSizeLock) {
			if (expectingTerminalSize) {
				while (this.isExpectingTerminalSize()) {
					/*
					 * Don't start a new background task
					 * if there's one already running.
					 */
					try {
						this.expectingTerminalSizeLock.wait();
					} catch (final InterruptedException ie) {
						// ignore
					}
				}

				this.executor.schedule(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						synchronized (FilteringTerminalSizeHandler.this.expectingTerminalSizeLock) {
							if (FilteringTerminalSizeHandler.this.isExpectingTerminalSize()) {
								FilteringTerminalSizeHandler.this.setExpectingTerminalSize(false, term);

								/*
								 * This background task can easily expire
								 * if multiple events are being collected.
								 */
								term.println("Timed out waiting for terminal size for " + FilteringTerminalSizeHandler.this.expectingTimeoutMillis + " ms.");
								term.setCursorLocation(999, 999).requestCursorLocation(); // Workaround for buggy terminals
								term.println(); // Temporary, only as long as we don't return the cursor to its original position
								term.flush();
							}
						}
					}
				}, this.expectingTimeoutMillis, MILLISECONDS);
			} else {
				this.expectingTerminalSizeLock.notifyAll();
			}

			this.t0 = expectingTerminalSize ? System.currentTimeMillis() : 0L;
		}
	}

	boolean isExpectingTerminalSize() {
		synchronized (this.expectingTerminalSizeLock) {
			return this.t0 != 0L;
		}
	}
}
