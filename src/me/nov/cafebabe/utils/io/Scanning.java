package me.nov.cafebabe.utils.io;

import java.io.InputStream;
import java.util.Scanner;

public class Scanning {
	@SuppressWarnings("resource")
	public static String readInputStream(InputStream is) {
		return new Scanner(is).useDelimiter("\\A").next();
	}
}
