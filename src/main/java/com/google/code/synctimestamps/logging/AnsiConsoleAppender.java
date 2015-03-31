/*-
 * $Id$
 */
package com.google.code.synctimestamps.logging;

import static com.google.code.synctimestamps.logging.AnsiConsoleAppender.Color.BLUE;
import static com.google.code.synctimestamps.logging.AnsiConsoleAppender.Color.CYAN;
import static com.google.code.synctimestamps.logging.AnsiConsoleAppender.Color.GREEN;
import static com.google.code.synctimestamps.logging.AnsiConsoleAppender.Color.MAGENTA;
import static com.google.code.synctimestamps.logging.AnsiConsoleAppender.Color.RED;
import static com.google.code.synctimestamps.logging.AnsiConsoleAppender.Color.WHITE;
import static com.google.code.synctimestamps.logging.AnsiConsoleAppender.Color.YELLOW;
import static java.lang.System.getenv;
import static org.apache.log4j.Layout.LINE_SEP;
import static org.apache.log4j.Level.ALL;
import static org.apache.log4j.Level.DEBUG;
import static org.apache.log4j.Level.ERROR;
import static org.apache.log4j.Level.FATAL;
import static org.apache.log4j.Level.INFO;
import static org.apache.log4j.Level.TRACE;
import static org.apache.log4j.Level.WARN;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
 */
public final class AnsiConsoleAppender extends ConsoleAppender {
	private static final char ESC = 0x1b;

	private static final String EOL = ESC + "[0m";

	private static final Map<Level, String> PREFIXES = new LinkedHashMap<>();

	static {
		PREFIXES.put(FATAL, getPrefix(WHITE, RED, true));
		PREFIXES.put(ERROR, getPrefix(RED, null, true));
		PREFIXES.put(WARN, getPrefix(YELLOW, null, true));
		PREFIXES.put(INFO, getPrefix(GREEN, null, true));
		PREFIXES.put(DEBUG, getPrefix(CYAN, null, true));
		PREFIXES.put(TRACE, getPrefix(BLUE, null, true));
		PREFIXES.put(ALL, getPrefix(MAGENTA, null, true));
	}

	private static final boolean DUMB;

	static {
		final String term = getenv("TERM");
		DUMB = term == null || term.isEmpty() || System.console() == null;
	}

	@Override
	protected void subAppend(@Nullable final LoggingEvent event) {
		if (event == null) {
			return;
		}

		if (!DUMB) {
			final Level level = event.getLevel();
			final String prefix;
			if (level.isGreaterOrEqual(FATAL)) {
				prefix = PREFIXES.get(FATAL);
			} else if (level.isGreaterOrEqual(ERROR)) {
				prefix = PREFIXES.get(ERROR);
			} else if (level.isGreaterOrEqual(WARN)) {
				prefix = PREFIXES.get(WARN);
			} else if (level.isGreaterOrEqual(INFO)) {
				prefix = PREFIXES.get(INFO);
			} else if (level.isGreaterOrEqual(DEBUG)) {
				prefix = PREFIXES.get(DEBUG);
			} else if (level.isGreaterOrEqual(TRACE)) {
				prefix = PREFIXES.get(TRACE);
			} else {
				prefix = PREFIXES.get(ALL);
			}
			this.qw.write(prefix);
		}

		this.qw.write(this.layout.format(event));

		if (this.layout.ignoresThrowable()) {
			final String s[] = event.getThrowableStrRep();
			if (s != null) {
				final int len = s.length;
				for (int i = 0; i < len; i++) {
					this.qw.write(s[i]);
					this.qw.write(LINE_SEP);
				}
			}
		}

		if (!DUMB) {
			this.qw.write(EOL);
		}

		if (this.shouldFlush(event)) {
			this.qw.flush();
		}
	}

	private static String getPrefix(@Nullable final Color foreground,
			@Nullable final Color background,
			final boolean bold) {
		final StringBuilder prefix = new StringBuilder();
		prefix.append(ESC);
		prefix.append('[');
		prefix.append(bold ? 1 : 0);
		if (foreground != null) {
			prefix.append(';').append(30 + foreground.ordinal());
		}
		if (background != null) {
			prefix.append(';').append(40 + background.ordinal());
		}
		prefix.append('m');
		return prefix.toString();
	}

	/**
	 * @author Andrey ``Bass'' Shcheglov &lt;mailto:andrewbass@gmail.com&gt;
	 */
	static enum Color {
		BLACK,
		RED,
		GREEN,
		YELLOW,
		BLUE,
		MAGENTA,
		CYAN,
		WHITE,
	}
}
