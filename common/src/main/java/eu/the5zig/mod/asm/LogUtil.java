/*
 * Copyright (c) 2019 5zig Reborn
 *
 * This file is part of The 5zig Mod
 * The 5zig Mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The 5zig Mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The 5zig Mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.the5zig.mod.asm;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class LogUtil {

	private static final String PREFIX = "[5zig] ";
	private static boolean DEBUG = false;

	private static String currentClass = null;
	private static String currentMethod = null;

	public static void startClass(String className, Object... format) {
		currentClass = className;
		out(PREFIX + "Patching: " + String.format(className, format));
	}

	public static void endClass() {
		currentClass = null;
	}

	public static void startMethod(String methodName, Object... format) {
		currentMethod = methodName;
		if (currentClass != null) {
			out(PREFIX + "    Patching: " + String.format(methodName, format));
		} else {
			out(PREFIX + "Patching: " + String.format(methodName, format));
		}
	}

	public static void endMethod() {
		currentMethod = null;
	}

	public static void log(String message, Object... format) {
		if (currentClass != null) {
			if (currentMethod != null) {
				out(PREFIX + "        Patching: " + String.format(message, format));
			} else {
				out(PREFIX + "    Patching: " + String.format(message, format));
			}
		} else {
			out(PREFIX + String.format(message, format));
		}
	}

	private static void out(String message) {
		if (!DEBUG)
			return;
		System.out.println(message);
	}

}
