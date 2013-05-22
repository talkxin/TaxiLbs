package com.taxiCliect.map;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import android.net.Proxy;

import com.taxiCliect.module.MLocation;

/**
 * 通过地理经纬度获取粗略的地理位置信息
 * 
 * @author talkliu
 * 
 */
public class GPSLocation {
	private HttpResponse response;

	public GPSLocation(double lat, double lng) throws Exception {
		response = execute(doGps(lat, lng));
	}

	/**
	 * 通过谷歌地图获取粗略地理位置
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private HttpResponse execute(JSONObject params) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();

		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
				20 * 1000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 20 * 1000);

		HttpPost post = new HttpPost("http://74.125.71.147/loc/json");

		StringEntity se = new StringEntity(params.toString());
		post.setEntity(se);
		HttpResponse response = httpClient.execute(post);
		return response;
	}

	/**
	 * 获取粗略地理位置的module
	 * 
	 * @return
	 */
	public MLocation transResponse() {
		MLocation location = null;
		if (response.getStatusLine().getStatusCode() == 200) {
			location = new MLocation();
			HttpEntity entity = response.getEntity();
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				StringBuffer sb = new StringBuffer();
				String result = br.readLine();
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				JSONObject json = new JSONObject(sb.toString());
				JSONObject lca = json.getJSONObject("location");

				location.Access_token = json.getString("access_token");
				if (lca != null) {
					if (lca.has("accuracy"))
						location.Accuracy = lca.getString("accuracy");
					if (lca.has("longitude"))
						location.Longitude = lca.getDouble("longitude");
					if (lca.has("latitude"))
						location.Latitude = lca.getDouble("latitude");
					if (lca.has("address")) {
						JSONObject address = lca.getJSONObject("address");
						if (address != null) {
							if (address.has("region"))
								location.Region = address.getString("region");
							if (address.has("street_number"))
								location.Street_number = address
										.getString("street_number");
							if (address.has("country_code"))
								location.Country_code = address
										.getString("country_code");
							if (address.has("street"))
								location.Street = address.getString("street");
							if (address.has("city"))
								location.City = address.getString("city");
							if (address.has("country"))
								location.Country = address.getString("country");
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				location = null;
			}
		}
		return location;
	}

	/**
	 * 通过位置，获取粗略的地理位置
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 * @throws Exception
	 */
	private JSONObject doGps(double lat, double lng) throws Exception {
		JSONObject holder = new JSONObject();
		holder.put("version", "1.1.0");
		holder.put("host", "maps.google.com");
		holder.put("address_language", "zh_CN");
		holder.put("request_address", true);

		JSONObject data = new JSONObject();
		data.put("latitude", lat);
		data.put("longitude", lng);
		holder.put("location", data);

		return holder;
	}
}
