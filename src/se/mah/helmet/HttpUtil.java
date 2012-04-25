package se.mah.helmet;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * Utility methods for http requests.
 *
 */
public class HttpUtil {
	
	private static final String TAG = HttpUtil.class.getSimpleName();

	/**
	 * Returns a HttpUriRequest for a http GET request with the specified url
	 * and Accept content type (header "Accept").
	 * 
	 * @param path resource url
	 * @param acceptContentType accepted content type of the response (header "Accept")
	 * @return http request
	 */
	public static HttpUriRequest newGetRequest(String path, String acceptContentType) {
		HttpGet request = new HttpGet(path);
		
		request.addHeader(new BasicHeader("Accept", acceptContentType));
		return request;
	}
	
	/**
	 * Returns a HttpUriRequest for a http POST request with the specified url
	 * and json data. The request will be encoded in UTF8 and have content type
	 * "application/json".
	 * 
	 * @param path resource url
	 * @param json json data to POST
	 * @return http request
	 */
	public static HttpUriRequest newJsonPostRequest(String path, String json) {
		HttpPost request = new HttpPost(path);
		
		StringEntity entity = null;
		try {
			entity = new StringEntity(json, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		request.setEntity(entity);
		return request;
	}
	
	/**
	 * Executes a http request that POSTs the specified json data.
	 * 
	 * @param client HttpClient to use for the request
	 * @param path resource url
	 * @param json json data to send
	 * @param buffer buffer to read the response to
	 * @return the response
	 */
	public static String httpPostJson(HttpClient client, String path, String json, byte[] buffer) {
		HttpUriRequest request = newJsonPostRequest(path, json);
		
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
	
	/**
	 * Executes a http request that GETs the specified url.
	 * 
	 * @param client HttpClient to use for the request
	 * @param path resource url
	 * @param acceptContentType accepted content type for the response (header "Accept")
	 * @param buffer buffer to save the response in
	 * @return the response
	 */
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
