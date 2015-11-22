package addams.family.gui;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Connector {
	private String url;

	public Connector(String url) {
		this.url = url;
	}

	public String getHTML(String path) throws Exception {
		String urlToRead = url + path;
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}
	
	
	public void post(String channel, String value) throws Exception {
		URL url;
		URLConnection urlConn;
		DataOutputStream printout;
		DataInputStream input;
		// URL of CGI-Bin script.
		url = new URL(this.url + "/eBrightness/" + channel + "/" + value);
		// URL connection channel.
		urlConn = url.openConnection();
		// Let the run-time system (RTS) know that we want input.
		urlConn.setDoInput(true);
		// Let the RTS know that we want to do output.
		urlConn.setDoOutput(true);
		// No caching, we want the real thing.
		urlConn.setUseCaches(false);
		// Specify the content type.
		urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// Send POST output.
		printout = new DataOutputStream(urlConn.getOutputStream());
	//	String content = "name=" + URLEncoder.encode("Buford Early") + "&email="
	//			+ URLEncoder.encode("buford@known-space.com");
	//	printout.writeBytes(content);
		printout.flush();
		printout.close();
		// Get response data.
		input = new DataInputStream(urlConn.getInputStream());
		String str;
		while (null != ((str = input.readLine()))) {
			System.out.println(str);
		}
		input.close();
	}
	
	public void setBrightness(String channel, int value) {
		try {
			post(channel, ""+value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject getCurrentBrightnessValues() {
		JSONObject json = null;
		try {
			String s = getHTML("/eBrightness");
			json = (JSONObject)new JSONParser().parse(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
}
