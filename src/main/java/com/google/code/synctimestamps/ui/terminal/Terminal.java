/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.InputEvent.ESC;
import static com.google.code.synctimestamps.ui.terminal.TerminalType.safeValueOf;
import static com.google.code.synctimestamps.ui.terminal.TextAttribute.NORMAL;
import static java.lang.System.getProperty;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.code.synctimestamps.ui.terminal.handlers.QuietTerminalSizeHandler;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class Terminal extends PrintWriter {
	private final TerminalType type;

	private final Reader in;

	final Thread sequenceTokenizer;

	private Color defaultForeground;

	private Color defaultBackground;

	private final EventQueueFactory eventQueueFactory = new EventQueueFactory();

	/**
	 * The background thread this executor is backed by is used for
	 * all terminal output (i.&nbsp;e. to schedule a terminal size request,
	 * print the terminal response, etc.).
	 */
	private final ExecutorService eventQueue = newSingleThreadExecutor(this.eventQueueFactory);

	{
		/*
		 * Submit an empty task in order to force the event queue
		 * to start.
		 */
		this.eventQueue.submit(new Runnable() {
			/**
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				// empty
			}
		});
	}

	private final QuietTerminalSizeHandler sizeHandler;

	/**
	 * @param ttyName
	 * @param term
	 * @param handler
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected Terminal(final String ttyName, final String term, final InputEventHandler handler)
	throws FileNotFoundException, UnsupportedEncodingException {
		super(ttyName, getTerminalEncoding(term));
		this.type = safeValueOf(term);
		this.in = new FileReader(ttyName);

		final QuietTerminalSizeHandler probablySizeHandler = findSizeHandler(handler);
		final InputEventHandler rootHandler = probablySizeHandler == null
				? new QuietTerminalSizeHandler().append(handler)
				: handler;
		this.sizeHandler = probablySizeHandler == null
				? (QuietTerminalSizeHandler) rootHandler
				: probablySizeHandler;

		this.sequenceTokenizer = new SequenceTokenizer(this, rootHandler);

		this.invokeLater(new Runnable() {
			/**
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				/*
				 * Auto linefeed can have any value;
				 */
				Terminal.this.setAutoLinefeed(false);

				/*
				 * Auto wraparound should be on.
				 */
				Terminal.this.setAutoWraparound(true);
			}});
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
	 * @param task
	 * @see javax.swing.SwingUtilities#invokeLater(Runnable)
	 */
	public void invokeLater(@Nonnull final Runnable task) {
		if (task == null) {
			throw new IllegalArgumentException();
		}

		this.eventQueue.submit(task);
	}

	/**
	 * @param <T>
	 * @param task
	 * @see javax.swing.SwingUtilities#invokeLater(Runnable)
	 */
	public <T> Future<T> invokeLater(@Nonnull final Callable<T> task) {
		if (task == null) {
			throw new IllegalArgumentException();
		}

		return this.eventQueue.submit(task);
	}

	public boolean isEventQueue() {
		return this.eventQueueFactory.isEventQueue();
	}

	private void checkIfEventQueue() {
		if (!this.isEventQueue()) {
			throw new IllegalStateException("Thread " + currentThread().getName() + " is not an event queue");
		}
	}

	/**
	 * @see PrintWriter#println()
	 */
	@Override
	public void println() {
		this.checkIfEventQueue();
		super.println();
	}

	/**
	 * @see PrintWriter#write(int)
	 */
	@Override
	public void write(final int c) {
		this.checkIfEventQueue();
		super.write(c);
	}

	/**
	 * @see PrintWriter#write(char[], int, int)
	 */
	@Override
	public void write(final char[] buf, final int off, final int len) {
		this.checkIfEventQueue();
		super.write(buf, off, len);
	}

	/**
	 * @see PrintWriter#write(String, int, int)
	 */
	@Override
	public void write(final String s, final int off, final int len) {
		this.checkIfEventQueue();
		super.write(s, off, len);
	}

	/**
	 * @see PrintWriter#close()
	 */
	@Override
	public void close() {
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

	private Terminal printEsc() {
		this.print(ESC);
		return this;
	}

	public Terminal requestTerminalSize() {
		this.printCsi().print("18t");
		return this;
	}

	public Terminal requestCursorLocation() {
		this.printCsi().print("6n");
		return this;
	}

	/**
	 * @param x
	 * @param y
	 */
	public Terminal setCursorLocation(final int x, final int y) {
		if (x <= 0 || y <= 0) {
			throw new IllegalArgumentException(String.format("[%d; %d]", String.valueOf(x), String.valueOf(y)));
		}

		this.printCsi();
		this.print(y);
		this.print(';');
		this.print(x);
		this.print('H');
		return this;
	}

	/**
	 * VT100 alternate character set is not supported by PuTTY.
	 */
	public Terminal startAlternateCs() {
		this.printEsc().print("(0");
		return this;
	}

	/**
	 * VT100 alternate character set is not supported by PuTTY.
	 */
	public Terminal stopAlternateCs() {
		this.printEsc().print("(B");
		return this;
	}

	/**
	 * @param foreground if {@code null}, no foreground color is set.
	 */
	public Terminal setForeground(final Color foreground) {
		return this.setTextAttributes(foreground, null);
	}

	/**
	 * @param background if {@code null}, no background color is set.
	 */
	public Terminal setBackground(final Color background) {
		return this.setTextAttributes(null, background);
	}

	/**
	 * @param defaultForeground
	 */
	public Terminal setDefaultForeground(final Color defaultForeground) {
		this.defaultForeground = defaultForeground;
		return this.setForeground(defaultForeground);
	}

	/**
	 * @param defaultBackground
	 */
	public Terminal setDefaultBackground(final Color defaultBackground) {
		this.defaultBackground = defaultBackground;
		return this.setBackground(defaultBackground);
	}

	/**
	 * @param attributes
	 */
	public Terminal setTextAttributes(@Nonnull final TextAttribute ... attributes) {
		return this.setTextAttributes(null, null, attributes);
	}

	/**
	 * @param foreground
	 * @param background
	 * @param attributes
	 */
	public Terminal setTextAttributes(@Nullable final Color foreground,
			@Nullable final Color background,
			@Nonnull final TextAttribute ... attributes) {
		final StringBuilder s = new StringBuilder();

		/*
		 * Other text attributes may contain NORMAL (^[[0m),
		 * so they should be applied *before* colors.
		 */
		for (final TextAttribute attribute : attributes) {
			s.append(attribute.ordinal()).append(';');
			if (attribute == NORMAL) {
				/*
				 * Whenever a rest to default attributes (CSI 0 m) is requested,
				 * also restore the default foreground and background.
				 */
				if (this.defaultForeground != null) {
					s.append(30 + this.defaultForeground.ordinal()).append(';');
				}
				if (this.defaultBackground != null) {
					s.append(40 + this.defaultBackground.ordinal()).append(';');
				}
			}
		}
		if (foreground != null) {
			s.append(30 + foreground.ordinal()).append(';');
		}
		if (background != null) {
			s.append(40 + background.ordinal()).append(';');
		}

		final int length = s.length();
		if (length != 0) {
			s.deleteCharAt(length - 1);

			this.printCsi();
			this.print(s);
			this.print('m');
		}

		return this;
	}

	/**
	 * @param title
	 */
	public Terminal setTitle(@Nullable final String title) {
		this.type.getTitleWriter().setTitle(this, title);
		return this;
	}

	/**
	 * Prints the <em>Control Sequence Introducer</em> (<em>CSI</em>).
	 *
	 * @return This terminal
	 */
	Terminal printCsi() {
		this.printEsc();
		this.print('[');
		return this;
	}

	/**
	 * Prints the <em>Operating System Command</em> (<em>OSC</em>).
	 *
	 * @return This terminal
	 */
	Terminal printOsc() {
		this.printEsc();
		this.print(']');
		return this;
	}

	/**
	 * @param vtKeyOrResponse
	 * @return This terminal
	 */
	public Terminal print(final VtKeyOrResponse vtKeyOrResponse) {
		vtKeyOrResponse.toString(this);
		return this;
	}

	/**
	 * @param inputEvent
	 * @return This terminal
	 */
	public Terminal print(final InputEvent inputEvent) {
		inputEvent.toString(this);
		return this;
	}

	public Terminal restoreDefaultForeground() {
		return this.setForeground(this.defaultForeground);
	}

	public Terminal restoreDefaultBackground() {
		return this.setBackground(this.defaultBackground);
	}

	public Terminal clear() {
		this.restoreDefaultForeground().restoreDefaultBackground().setCursorLocation(1, 1).eraseInDisplay(EraseInDisplay.ERASE_BELOW).flush();
		return this;
	}

	/**
	 * @param visible
	 * @return This terminal
	 */
	public Terminal setToolbarVisible(final boolean visible) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(10);
		this.print(visible ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param visible
	 * @return This terminal
	 */
	public Terminal setCursorVisible(final boolean visible) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(25);
		this.print(visible ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param visible
	 * @return This terminal
	 */
	public Terminal setScrollbarVisible(final boolean visible) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(30);
		this.print(visible ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param enabled
	 */
	public Terminal setAutoWraparound(final boolean enabled) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(7);
		this.print(enabled ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param enabled
	 */
	public Terminal setAutoLinefeed(final boolean enabled) {
		/*
		 * LNM: Automatic Newline (h)/Normal Linefeed (l)
		 */
		this.printCsi().print(20);
		this.print(enabled ? 'h' : 'l');
		this.flush();

		return this;
	}
	/**
	 * Can be invoked from any thread except for {@linkplain
	 * SequenceConsumer#isDispatchThread() SequenceConsumer Dispatch Thread}.
	 *
	 * @return this terminal's size
	 * @throws IllegalStateException if invoked from {@linkplain
	 *         SequenceConsumer#isDispatchThread() SequenceConsumer Dispatch Thread}
	 * @see SequenceConsumer#isDispatchThread()
	 */
	public Dimension getSize() {
		return this.sizeHandler.getTerminalSize(this);
	}

	public Dimension getDefaultSize() {
		return this.type.getDefaultSize();
	}

	/**
	 * @param mode
	 */
	private Terminal eraseInDisplay(@Nonnull final EraseInDisplay mode) {
		this.printCsi().printf("%dJ", Integer.valueOf(mode.ordinal()));
		return this;
	}

	/**
	 * @param term
	 */
	private static String getTerminalEncoding(final String term) {
		switch (safeValueOf(term)) {
		case SUN_COLOR:
			return "ISO8859-1";
		case VTNT:
			return isLocaleCyrilic() ? "IBM866" : "IBM437";
		default:
			return getProperty("file.encoding");
		}

	}

	private static boolean isLocaleCyrilic() {
		return asList(getProperty("user.language"),
				getProperty("user.language.format"),
				getProperty("user.langage.display")).contains("ru");
	}

	/**
	 * @param initial
	 */
	private static QuietTerminalSizeHandler findSizeHandler(final InputEventHandler initial) {
		if (initial == null) {
			return null;
		}

		for (final InputEventHandler handler : initial) {
			if (handler instanceof QuietTerminalSizeHandler) {
				return (QuietTerminalSizeHandler) handler;
			}
		}

		return null;
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 * @author $Author$
	 * @version $Revision$, $Date$
	 */
	private static final class EventQueueFactory implements ThreadFactory {
		private Thread eventQueue;

		private final Object lock = new Object();

		EventQueueFactory() {
			// empty
		}

		/**
		 * @see ThreadFactory#newThread(Runnable)
		 */
		@Override
		public Thread newThread(final Runnable r) {
			synchronized (this.lock) {
				return this.eventQueue == null
						? this.eventQueue = new Thread(r, "EventQueue")
						: this.eventQueue;
			}
		}

		public boolean isEventQueue() {
			synchronized (this.lock) {
				if (this.eventQueue == null) {
					throw new IllegalStateException("Event queue is not yet runnable");
				}

				return currentThread() == this.eventQueue;
			}
		}
	}
}
