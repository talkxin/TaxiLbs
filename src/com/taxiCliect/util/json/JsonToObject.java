package com.taxiCliect.util.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.taxiCliect.util.Annotation.JsonToAction;

/**
 * 将JSON转换为可用的Array或Object，泛型类
 * 
 * @author talkliu
 * 
 */
public class JsonToObject<T> {
	/**
	 * 操作的泛型Class
	 */
	private Class<T> modelClass;

	/**
	 * 构造方法，取得泛型的Class，用户创建新类或，反射方法用
	 */
	public JsonToObject(Class class1) {
		// Type genType = getClass().getGenericSuperclass();
		// Type[] params = ((ParameterizedType)
		// genType).getActualTypeArguments();
		// modelClass = (Class) params[0];
		modelClass = class1;
	}

	/**
	 * 传入json字符串返回可用的List
	 * 
	 * @param jsonString
	 * @return
	 * @throws Exception
	 */
	public List<T> getArrayJsonList(String jsonString) throws Exception {
		JSONArray jsonArray = new JSONArray(jsonString);
		if (jsonArray.length() == 0)
			return null;
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < jsonArray.length(); i++) {
			list.add(getJsonObject(jsonArray.getString(i)));
		}
		return list;
	}

	/**
	 * 传入字符串返回可用的对象
	 * 
	 * @param jsonString
	 * @return
	 */
	public T getJsonObject(String jsonString) throws Exception {
		// 将json字符串赋予对象
		JSONObject jsonObject = new JSONObject(jsonString);
		// 通过class创建一个module对象
		Object obj = modelClass.newInstance();
		for (Field field : modelClass.getDeclaredFields()) {
			JsonToAction json = field.getAnnotation(JsonToAction.class);
			if (json.toJson()) {
				String string = jsonObject.getString(field.getName());
				String class1 = modelClass.getDeclaredField(field.getName())
						.getType().toString().replaceAll("class ", "");
				Constructor con = Class.forName(
						modelClass.getDeclaredField(field.getName()).getType()
								.toString().replaceAll("class ", ""))
						.getConstructor(String.class);
				field.setAccessible(true);
				// 若为非数字类型可为空否则给与0的初始值
				field.set(obj, con.newInstance((string == null
						|| string.equals("") || string.equals("null"))
						&& !class1.equals("java.lang.String") ? "0" : string));
			}
		}
		return (T) obj;
	}
}
