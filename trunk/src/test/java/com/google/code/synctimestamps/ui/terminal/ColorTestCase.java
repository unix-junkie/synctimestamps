/*-
 * $Id$
 */
package com.google.code.synctimestamps.ui.terminal;

import static junit.framework.Assert.assertSame;

import org.junit.Test;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 * @author $Author$
 * @version $Revision$, $Date$
 */
public final class ColorTestCase {
	@Test
	public void testIsBright() {
		for (final Color color : Color.values()) {
			System.out.println(color + "\tbright? " + color.isBright());
		}
	}

	@Test
	public void testValueOf() {
		for (final Color color : Color.values()) {
			assertSame(color, Color.valueOf(color.ordinal()));
		}
	}

	@Test
	public void testDarker() {
		for (final Color color : Color.values()) {
			System.out.println(color + "\t-> " + color.darker());
		}
	}
}