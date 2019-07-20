package me.nov.cafebabe.utils.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLReader {
	public static String getURLContent(String url) {
		try {
			URL website = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) website.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
			connection.setConnectTimeout(4000);
			connection.setRequestMethod("GET");
			connection.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			in.close();
			return response.toString();
		} catch (Exception e) {
			return null;
		}
	}
}
