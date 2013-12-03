package org.o2i2b2.utils;

import java.io.*;

public class LogHelper {

	Exception exception = null;
	
	public LogHelper () {}

	public LogHelper (Exception exception) {
		this.exception = exception;
	}
	
	public String toString () {
		return stack2string(exception);
	}

	public String stack2string() {
		return stack2string(exception);
	}
	
	public String stack2string(Exception e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "------\r\n" + sw.toString() + "------\r\n";
		} catch(Exception e2) {
			return "bad stack2string";
		}
	}
	
	
}
