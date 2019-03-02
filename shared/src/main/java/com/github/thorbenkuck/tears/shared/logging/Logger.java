package com.github.thorbenkuck.tears.shared.logging;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class Logger {

	private static PrintStream out = System.out;

	private static void dispatchLog(Object o) {
		out.println(o);
	}

	private static String prefix(String depth) {
		return "[" + depth + "] (" + Thread.currentThread() + ") {" + LocalDateTime.now() + "} : ";
	}

	public static void trace(Object o) {
		dispatchLog(prefix("TRACE") + o);
	}

	public static void debug(Object o) {
		dispatchLog(prefix("DEBUG") + o);
	}

	public static void info(Object o) {
		dispatchLog(prefix("INFO") + o);
	}

	public static void warn(Object o) {
		dispatchLog(prefix("WARN") + o);
	}

	public static void error(Object o) {
		dispatchLog(prefix("ERROR") + o);
	}

	public static void catching(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter));
		String stackTrace = stringWriter.toString();

		dispatchLog(prefix("CATCHING") + throwable.getMessage());
		dispatchLog(stackTrace);
	}

}
