/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static com.google.code.synctimestamps.ui.terminal.InputEvent.ESC;
import static com.google.code.synctimestamps.ui.terminal.VtKey.DELETE;
import static com.google.code.synctimestamps.ui.terminal.VtKey.DOWN;
import static com.google.code.synctimestamps.ui.terminal.VtKey.END;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F1;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F10;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F11;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F12;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F2;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F3;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F4;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F5;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F6;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F7;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F8;
import static com.google.code.synctimestamps.ui.terminal.VtKey.F9;
import static com.google.code.synctimestamps.ui.terminal.VtKey.HOME;
import static com.google.code.synctimestamps.ui.terminal.VtKey.INSERT;
import static com.google.code.synctimestamps.ui.terminal.VtKey.LEFT;
import static com.google.code.synctimestamps.ui.terminal.VtKey.PAGE_DOWN;
import static com.google.code.synctimestamps.ui.terminal.VtKey.PAGE_UP;
import static com.google.code.synctimestamps.ui.terminal.VtKey.RIGHT;
import static com.google.code.synctimestamps.ui.terminal.VtKey.UP;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public enum TerminalType {
	ANSI("ansi"),
	DTTERM("dtterm"),
	/**
	 * SunOS rxvt reports TERM=kterm.
	 */
	KTERM("kterm"),
	LINUX("linux"),
	RXVT("rxvt"),
	RXVT_UNICODE("rxvt-unicode"),
	RXVT_UNICODE_256COLOR("rxvt-unicode-256color"),
	SCOANSI("scoansi"),
	SCREEN("screen"),
	SCREEN_LINUX("screen.linux"),
	SUN_CMD("sun-cmd"),
	SUN_COLOR("sun-color"),
	VT100("vt100"),
	VT320("vt320"),
	XTERM("xterm"),
	;

	static {
		/*
		 * This can't be done in a constructor,
		 * because enum members are not yet known
		 * during the constructor execution.
		 */
		for (final TerminalType type : values()) {
			type.registerEscapeSequences();
		}
	}

	private final String term;

	private final Map<InputEvent, VtKey> knownEscapeSequences = new HashMap<InputEvent, VtKey>();

	/**
	 * @param term
	 */
	private TerminalType(final String term) {
		this.term = term;
	}

	private void registerEscapeSequences() {
		switch (this) {
		case XTERM:
			this.registerOldFunctionKeys(); // PuTTY sends old function keys by default
			this.registerLinuxFunctionKeys(); // PuTTY can also send linux function keys
			this.registerVt100FunctionKeys(); // PuTTY can also send VT100 function keys
			this.registerScoFunctionKeys(); // PuTTY can also send SCO function keys
			this.registerSunFunctionKeys(); // XTerm can send Sun function keys
			this.registerAnsiFunctionKeys();

			/*
			 * SunOS xterm
			 */
			this.registerEscapeSequence(F11, ESC, '[', '5', '7', '~');
			this.registerEscapeSequence(F12, ESC, '[', '5', '8', '~');

			this.registerAnsiKeypad();
			this.registerAnsiApplicationKeypad(); // XTerm in application keypad mode.
			this.registerRxvtKeypad(); // PuTTY can be switched to RXVT keypad mode.
			this.registerScoKeypad(); // PuTTY can be switched to SCO keypad mode.

			this.registerCursorKeys();
			this.registerApplicationCursorKeys(); // XTerm and PuTTY can also send application cursor keys
			break;
		case LINUX:
			this.registerLinuxFunctionKeys();
			//$FALL-THROUGH$
		case ANSI:
		case SCREEN_LINUX:
			this.registerAnsiFunctionKeys();

			this.registerAnsiKeypad();

			this.registerCursorKeys();
			break;
		case DTTERM:
			/*
			 * Home/End/PgUp/PgDn don't work in dtterm
			 * F11-F12 don't work in dtterm.
			 */
			this.registerOldFunctionKeys();
			this.registerAnsiFunctionKeys();

			this.registerCursorKeys();
			this.registerApplicationCursorKeys(); // DtTerm can also send application cursor keys
			break;
		case SUN_COLOR:
			this.registerSunFunctionKeys();

			this.registerSunKeypad();

			this.registerCursorKeys();
			break;
		case SUN_CMD:
			/*
			 * Home/End/PgUp/PgDn don't work in sun-cmd (shelltool)
			 */
			this.registerSunFunctionKeys();

			this.registerCursorKeys();
			break;
		case RXVT:
		case RXVT_UNICODE:
		case RXVT_UNICODE_256COLOR:
		case KTERM:
			this.registerOldFunctionKeys();
			this.registerAnsiFunctionKeys();

			this.registerAnsiKeypad();
			this.registerRxvtKeypad();

			this.registerCursorKeys();
			break;
		case VT320:
			this.registerAnsiFunctionKeys();

			this.registerAnsiKeypad();
			//$FALL-THROUGH$
		case VT100:
			this.registerVt100FunctionKeys();

			this.registerCursorKeys();
			break;
		case SCOANSI:
			this.registerScoFunctionKeys();

			this.registerScoKeypad();

			this.registerCursorKeys();
			break;
		default:
			break;
		}
	}

	private void registerApplicationCursorKeys() {
		this.registerEscapeSequence(UP, ESC, 'O', 'A');
		this.registerEscapeSequence(DOWN, ESC, 'O', 'B');
		this.registerEscapeSequence(RIGHT, ESC, 'O', 'C');
		this.registerEscapeSequence(LEFT, ESC, 'O', 'D');
	}

	private void registerCursorKeys() {
		this.registerEscapeSequence(UP, ESC, '[', 'A');
		this.registerEscapeSequence(DOWN, ESC, '[', 'B');
		this.registerEscapeSequence(RIGHT, ESC, '[', 'C');
		this.registerEscapeSequence(LEFT, ESC, '[', 'D');
	}

	private void registerAnsiKeypad() {
		this.registerEscapeSequence(HOME, ESC, '[', '1', '~');
		this.registerEscapeSequence(INSERT, ESC, '[', '2', '~');
		this.registerEscapeSequence(DELETE, ESC, '[', '3', '~');
		this.registerEscapeSequence(END, ESC, '[', '4', '~');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', '5', '~');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', '6', '~');
	}

	private void registerAnsiApplicationKeypad() {
		this.registerEscapeSequence(HOME, ESC, 'O', 'H');
		this.registerEscapeSequence(END, ESC, 'O', 'F');
	}

	private void registerAnsiFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, 'O', 'P');
		this.registerEscapeSequence(F2, ESC, 'O', 'Q');
		this.registerEscapeSequence(F3, ESC, 'O', 'R');
		this.registerEscapeSequence(F4, ESC, 'O', 'S');

		this.registerEscapeSequence(F5, ESC, '[', '1', '5', '~');
		this.registerEscapeSequence(F6, ESC, '[', '1', '7', '~');
		this.registerEscapeSequence(F7, ESC, '[', '1', '8', '~');
		this.registerEscapeSequence(F8, ESC, '[', '1', '9', '~');
		this.registerEscapeSequence(F9, ESC, '[', '2', '0', '~');
		this.registerEscapeSequence(F10, ESC, '[', '2', '1', '~');
		this.registerEscapeSequence(F11, ESC, '[', '2', '3', '~');
		this.registerEscapeSequence(F12, ESC, '[', '2', '4', '~');
	}

	private void registerRxvtKeypad() {
		/*
		 * Consistent with ANSI.
		 */
		this.registerEscapeSequence(DELETE, ESC, '[', '3', '~');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', '5', '~');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', '6', '~');

		/*
		 * RXVT in SunOS and Linux
		 */
		this.registerEscapeSequence(HOME, ESC, '[', '7', '~');
		this.registerEscapeSequence(END, ESC, '[', '8', '~');

		/*
		 * PuTTY in RXVT keypad mode.
		 */
		this.registerEscapeSequence(HOME, ESC, '[', 'H');
		this.registerEscapeSequence(END, ESC, 'O', 'w');
	}

	private void registerVt100FunctionKeys() {
		this.registerEscapeSequence(F5, ESC, 'O', 'T');
		this.registerEscapeSequence(F6, ESC, 'O', 'U');
		this.registerEscapeSequence(F7, ESC, 'O', 'V');
		this.registerEscapeSequence(F8, ESC, 'O', 'W');
		this.registerEscapeSequence(F9, ESC, 'O', 'X');
		this.registerEscapeSequence(F10, ESC, 'O', 'Y');
		this.registerEscapeSequence(F11, ESC, 'O', 'Z');
		this.registerEscapeSequence(F12, ESC, 'O', '[');
	}

	private void registerLinuxFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', '[', 'A');
		this.registerEscapeSequence(F2, ESC, '[', '[', 'B');
		this.registerEscapeSequence(F3, ESC, '[', '[', 'C');
		this.registerEscapeSequence(F4, ESC, '[', '[', 'D');
		this.registerEscapeSequence(F5, ESC, '[', '[', 'E');
	}

	private void registerOldFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', '1', '1', '~');
		this.registerEscapeSequence(F2, ESC, '[', '1', '2', '~');
		this.registerEscapeSequence(F3, ESC, '[', '1', '3', '~');
		this.registerEscapeSequence(F4, ESC, '[', '1', '4', '~');
	}

	private void registerSunKeypad() {
		this.registerEscapeSequence(HOME, ESC, '[', '2', '1', '4', 'z');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', '2', '1', '6', 'z');
		this.registerEscapeSequence(END, ESC, '[', '2', '2', '0', 'z');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', '2', '2', '2', 'z');
	}

	private void registerSunFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', '2', '2', '4', 'z');
		this.registerEscapeSequence(F2, ESC, '[', '2', '2', '5', 'z');
		this.registerEscapeSequence(F3, ESC, '[', '2', '2', '6', 'z');
		this.registerEscapeSequence(F4, ESC, '[', '2', '2', '7', 'z');
		this.registerEscapeSequence(F5, ESC, '[', '2', '2', '8', 'z');
		this.registerEscapeSequence(F6, ESC, '[', '2', '2', '9', 'z');
		this.registerEscapeSequence(F7, ESC, '[', '2', '3', '0', 'z');
		this.registerEscapeSequence(F8, ESC, '[', '2', '3', '1', 'z');
		this.registerEscapeSequence(F9, ESC, '[', '2', '3', '2', 'z');
		this.registerEscapeSequence(F10, ESC, '[', '2', '3', '3', 'z');
		this.registerEscapeSequence(F11, ESC, '[', '2', '3', '4', 'z');
		this.registerEscapeSequence(F12, ESC, '[', '2', '3', '5', 'z');

		/*
		 * XTerm in Sun function key mode sends F11 and F12 slightly differently.
		 */
		this.registerEscapeSequence(F11, ESC, '[', '1', '9', '2', 'z');
		this.registerEscapeSequence(F12, ESC, '[', '1', '9', '3', 'z');
	}

	private void registerScoKeypad() {
		this.registerEscapeSequence(END, ESC, '[', 'F');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', 'G');
		this.registerEscapeSequence(HOME, ESC, '[', 'H');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', 'I');
		this.registerEscapeSequence(INSERT, ESC, '[', 'L');
	}

	private void registerScoFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', 'M');
		this.registerEscapeSequence(F2, ESC, '[', 'N');
		this.registerEscapeSequence(F3, ESC, '[', 'O');
		this.registerEscapeSequence(F4, ESC, '[', 'P');
		this.registerEscapeSequence(F5, ESC, '[', 'Q');
		this.registerEscapeSequence(F6, ESC, '[', 'R');
		this.registerEscapeSequence(F7, ESC, '[', 'S');
		this.registerEscapeSequence(F8, ESC, '[', 'T');
		this.registerEscapeSequence(F9, ESC, '[', 'U');
		this.registerEscapeSequence(F10, ESC, '[', 'V');
		this.registerEscapeSequence(F11, ESC, '[', 'W');
		this.registerEscapeSequence(F12, ESC, '[', 'X');
	}

	/**
	 * @param vtKey
	 * @param data
	 */
	private void registerEscapeSequence(final VtKey vtKey, final char ... data) {
		this.registerEscapeSequence(vtKey, new InputEvent(this, data));
	}

	/**
	 * @param vtKey
	 * @param event
	 */
	private void registerEscapeSequence(final VtKey vtKey, final InputEvent event) {
		this.knownEscapeSequences.put(event, vtKey);
	}

	/**
	 * @param event
	 */
	public boolean isKnownEscapeSequence(final InputEvent event) {
		return event.isEscapeSequence() && this.knownEscapeSequences.containsKey(event);
	}

	/**
	 * @param event
	 */
	public VtKey getVtKey(final InputEvent event) {
		return this.knownEscapeSequences.get(event);
	}

	/**
	 * @param term
	 */
	public static TerminalType safeValueOf(final String term) {
		for (final TerminalType type : values()) {
			if (type.term.equals(term)) {
				return type;
			}
		}
		return ANSI;
	}

	/**
	 * @see Enum#toString()
	 */
	@Override
	public String toString() {
		return this.term;
	}
}
