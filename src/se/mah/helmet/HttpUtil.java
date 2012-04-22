package se.mah.helmet;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class HttpUtil {
	
	private static final String TAG = HttpUtil.class.getSimpleName();

	public static HttpUriRequest newGetRequest(String path, String acceptContentType) {
		HttpUriRequest request = new HttpGet(path);
		request.addHeader(new BasicHeader("Accept", acceptContentType));
		return request;
	}
	
	public static String httpGet(HttpClient client, String path, String acceptContentType, byte[] buffer) {
		HttpUriRequest request = newGetRequest(path, acceptContentType);

		HttpResponse response = null;
		InputStream is = null;
		int size = 0;
		try {
			response = client.execute(request);
			is = response.getEntity().getContent();
			size = is.read(buffer);
		} catch (Exception e) {
			Log.e(TAG, "Http request failed: " + e.getMessage());
			size = -1;
		} finally {
			try {
				is.close();
			} catch (Exception e) { }
		}
		
		if (size >= 0)
			return new String(buffer, 0, size);
		else
			return null;
	}
}
