/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public enum TextAttribute {
	NORMAL,		// xterm+	iTerm.app+	Terminal.app+	PuTTY+	cmd.exe-cygwin+
	BOLD,		// xterm+	iTerm.app+	Terminal.app+	PuTTY+	cmd.exe-cygwin+
	FAINT,		// xterm-	iTerm.app-	Terminal.app-	PuTTY-	cmd.exe-cygwin-
	ITALIC,		// xterm-	iTerm.app-	Terminal.app-	PuTTY-	cmd.exe-cygwin-
	UNDERLINE,	// xterm+	iTerm.app+	Terminal.app+	PuTTY-	cmd.exe-cygwin±
	BLINK,		// xterm+	iTerm.app+	Terminal.app+	PuTTY±	cmd.exe-cygwin±
	BLINK_RAPID,	// xterm-	iTerm.app-	Terminal.app-	PuTTY±	cmd.exe-cygwin-
	INVERSE,		// xterm+	iTerm.app+	Terminal.app+	PuTTY+	cmd.exe-cygwin+
	CONCEAL,		// xterm+	iTerm.app-	Terminal.app+	PuTTY-	cmd.exe-cygwin+
}
