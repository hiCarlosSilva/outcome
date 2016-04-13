package pro.outcome.util;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Iterator;


public class HttpClient {

	public static final String CHARSET = "UTF-8";
	
	public static String get(String url) throws IOException {
		Checker.checkEmpty(url);
		URL theUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)theUrl.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("charset", CHARSET);
		return _read(connection);
	}
	
	public static String post(String url, Map<String,String> params) throws IOException { 
		Checker.checkEmpty(url);
		// Process parameters:
		StringBuilder postParams = new StringBuilder();
		if(params != null) {
			Iterator<Map.Entry<String,String>> it = params.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry<String,String> entry = it.next();
				postParams.append(URLEncoder.encode(entry.getKey(), CHARSET));
				postParams.append("=");
				postParams.append(URLEncoder.encode(entry.getValue(), CHARSET));
				if(it.hasNext()) {
					postParams.append("&");
				}
			}
		}
		byte[] postData = postParams.toString().getBytes(CHARSET);
		// Send the request:
		URL theUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)theUrl.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("charset", CHARSET);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
		connection.setDoOutput(true);
		connection.getOutputStream().write(postData);
		// Process response:
		return _read(connection);
	}

	private static String _read(HttpURLConnection connection) throws IOException {
		int statusCode = connection.getResponseCode(); 
		if(statusCode != HttpURLConnection.HTTP_OK) {
  			throw new IOException(Strings.expand("received HTTP status code {}", statusCode));
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET));
		StringBuilder resp = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null) {
			resp.append(line);
		}
		reader.close();
		return resp.toString();
	}
}
