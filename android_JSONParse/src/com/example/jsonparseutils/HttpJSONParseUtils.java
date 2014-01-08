package com.example.jsonparseutils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class HttpJSONParseUtils {

	private static final String ERROR = "服务器连接错误";

	// 获取服务器端到的JSON数据
	public static List<Person> JsonArrayParse(String url) {
		String jsonArrayString = sendGet(url);
		try {
			if (jsonArrayString != null && !jsonArrayString.equals(ERROR)) {
				JSONObject jo = new JSONObject(jsonArrayString);
				String errno = String.valueOf(jo.get("errno"));
				if (errno.equals("0")) {
					JSONArray jArray = new JSONArray(String.valueOf(jo
							.get("users")));
					List<Person> persons = new ArrayList<Person>();
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject jObject = jArray.getJSONObject(i);
						int id=jObject.getInt("id");
						String name = jObject.getString("name");
						int age = jObject.getInt("age");
						String address = jObject.getString("address");
						Bitmap headImg = GetImageFromServerUtils.getBitmap(
								jObject.getString("img"), id);
						persons.add(new Person(name, age, address, headImg));
					}
					return persons;
				}
			} else {
				System.out.println("error-->" + jsonArrayString);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String JsonObjectParse(String url){
		try {
			String jsonObjectString=sendGet(url);
			JSONObject jObject=new JSONObject(jsonObjectString);
			String jString=jObject.getString("info");
			return jString;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * HttpURLConnection 发送Get请求
	 */
	public static String sendGet(String url) {
		try {
			URL realurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realurl
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(3000);
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int len = 0;
			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				while ((len = is.read(b)) != -1) {
					baos.write(b, 0, len);
				}
				is.close();
				String result = new String(baos.toByteArray());
				return result;
			} else {
				System.out.println(responseCode);
				return responseCode + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}

	/**
	 * HttpURLConnection 发送Post请求
	 */
	public static String sendPost(String url, Map<String, String> map) {
		StringBuilder sbf = new StringBuilder();
		try {
			for (Map.Entry<String, String> item : map.entrySet()) {
				sbf.append(item.getKey()).append("=")
						.append(URLEncoder.encode(item.getValue(), "utf-8"))
						.append("&");
			}
			sbf.deleteCharAt(sbf.length() - 1);
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			byte[] b = sbf.toString().getBytes();
			conn.setRequestProperty("Content-TYPE",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-length", String.valueOf(b.length));
			OutputStream os = conn.getOutputStream();
			os.write(b, 0, b.length);
			os.close();
			int responseCode = conn.getResponseCode();
			// System.out.println("responseCode****" + responseCode);
			InputStream is = conn.getInputStream();
			if (responseCode == 200) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] data = new byte[1024];
				int len = 0;
				while ((len = is.read(data)) != -1) {
					baos.write(data, 0, len);
				}
				is.close();
				return new String(baos.toByteArray(), "utf-8");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "数据错误";
	}

	/**
	 * HttpClient的Get请求
	 */
	public static String HttpClientGet(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(get);
			HttpEntity mEntity = response.getEntity();
			InputStream is = mEntity.getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(is));
			String result = "";
			String line;
			while ((line = buf.readLine()) != null) {
				result = result + line;
			}
			// String result=EntityUtils.toString(mEntity, HTTP.UTF_8);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * HttpClient的Post请求
	 */
	public static String HttpClientPost(String url) {

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> entity = new ArrayList<NameValuePair>();
			entity.add(new BasicNameValuePair("name", "zhangsan"));
			entity.add(new BasicNameValuePair("age", "25"));
			entity.add(new BasicNameValuePair("address", "shanghai"));
			post.setEntity(new UrlEncodedFormEntity(entity, HTTP.UTF_8));
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity mEntity = response.getEntity();
				String result = EntityUtils.toString(mEntity, HTTP.UTF_8);
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}