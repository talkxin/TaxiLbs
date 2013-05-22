package com.taxiCliect.util.postAction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.taxiCliect.util.Annotation.JsonToAction;

/**
 * 封装提交至服务器的各种方法
 * 
 * @author talkliu
 * 
 */
public class PostToAction {
	/**
	 * 利用post进行提交至服务器,并返回json字符串，可自行解析
	 * 
	 * @param url
	 *            提交的地址
	 * @param objects
	 * @throws Exception
	 */
	public String postToServer(String url, Object[]... objects)
			throws Exception {
		HttpEntityEnclosingRequestBase httpRequest = new HttpPost(url);
		List<NameValuePair> params = getNameValuePairsList(objects);
		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpResponse httpResponse = new DefaultHttpClient()
				.execute(httpRequest);
		// 得到应答的字符串，这也是一个 JSON 格式保存的数据
		String retSrc = EntityUtils.toString(httpResponse.getEntity());
		return retSrc;
	}

	/**
	 * 返回post提交的列表
	 * 
	 * @param objects
	 *            该为传入的值，Object数组类型若[0]为String则为单值，若[0]为其他对象，则默认为bean，
	 *            将反射所有get方法构成
	 * @return
	 */
	private List<NameValuePair> getNameValuePairsList(Object[]... objects)
			throws Exception {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Object[] obj : objects) {
			if (obj[0].getClass() == String.class) {
				params.add(new BasicNameValuePair((String) obj[0], String
						.valueOf(obj[1])));
			} else {
				// 传入对象的头
				String head = obj[1] + ".";
				Field[] allFields = obj[0].getClass().getDeclaredFields();
				for (Field field : allFields) {
					JsonToAction jsonToAction = field
							.getAnnotation(JsonToAction.class);
					if (jsonToAction.toJson()) {
						Object objValueObject = obj[0].getClass()
								.getMethod("get" + fistString(field.getName()))
								.invoke(obj[0]);
						// 只支持简单bean，对对象中内涵其他对象的bean支持不能
						if (objValueObject != null) {
							params.add(new BasicNameValuePair(head
									+ field.getName(), String
									.valueOf(objValueObject)));
						}
					}
				}
			}
		}
		return params;
	}

	/**
	 * 首字母大写
	 * 
	 * @param nameString
	 * @return
	 */
	private String fistString(String nameString) {
		nameString = nameString.replaceFirst(nameString.substring(0, 1),
				nameString.substring(0, 1).toUpperCase());
		return nameString;
	}
}
