package com.taxiCliect.util.db;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.taxiCliect.activity.R;
import com.taxiCliect.util.Annotation.TableName;
import com.taxiCliect.util.Annotation.TableProperty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库增删查改操作类
 * 
 * @author talkliu
 * 
 */
public class Database2Pojo {
	/**
	 * 传入的activity
	 */
	private Context context;
	/**
	 * 操作数据库对象
	 */
	private SQLiteDatabase sDatabase;

	/**
	 * 构造方法，传入context获取默认数据库
	 * 
	 * @param context
	 */
	public Database2Pojo(Context context) {
		this.context = context;
		// 连接数据库数据库
		DataBase dbBase = new DataBase(context,
				context.getString(R.string.database_name));
		// 初始化连接
		sDatabase = dbBase.getWritableDatabase();
	}

	/**
	 * 构造方法返回默认数据库的不同版本
	 * 
	 * @param context
	 * @param version
	 */
	public Database2Pojo(Context context, int version) {
		this.context = context;
		// 连接数据库数据库
		DataBase dbBase = new DataBase(context,
				context.getString(R.string.database_name), version);
		// 初始化连接
		sDatabase = dbBase.getWritableDatabase();
	}

	/**
	 * 构造方法，连接不同的数据库
	 * 
	 * @param context
	 * @param dbNanem
	 */
	public Database2Pojo(Context context, String dbNanem) {
		this.context = context;
		// 连接数据库数据库
		DataBase dbBase = new DataBase(context, dbNanem);
		// 初始化连接
		sDatabase = dbBase.getWritableDatabase();
	}

	/**
	 * 构造方法，连接不同的数据库的不同版本
	 * 
	 * @param context
	 * @param dbNanem
	 */
	public Database2Pojo(Context context, String dbNanem, int version) {
		this.context = context;
		// 连接数据库数据库
		DataBase dbBase = new DataBase(context, dbNanem, version);
		// 初始化连接
		sDatabase = dbBase.getWritableDatabase();
	}

	/**
	 * 插入数据
	 * 
	 * @param saveObject
	 * @return
	 * @throws Exception
	 */
	public void save(Object object) throws Exception {
		TableType sqlins = object2Content(object);
		if (sqlins.keyType) {
			sqlins.values.remove(sqlins.tableKey);
		}
		sDatabase.insert(sqlins.tableName, null, sqlins.values);
	}

	/**
	 * 根据ID修改
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public void update2Id(Object object) throws Exception {
		TableType sql = object2Content(object);
		sDatabase.update(sql.tableName, sql.values, sql.tableKey + "=?",
				new String[] { String.valueOf(sql.values.get(sql.tableKey)) });
	}

	/**
	 * 根据ID删除
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public void delete2Id(Object object) throws Exception {
		TableType sql = object2Content(object);
		sDatabase.delete(sql.tableName, sql.tableKey + "=?",
				new String[] { String.valueOf(sql.values.get(sql.tableKey)) });
	}

	/**
	 * 根据id返回list
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public Object query2Id(Object object) throws Exception {
		TableType sql = getObjectkey(object);
		String sqlString = "select * from " + sql.tableName + " where "
				+ sql.tableKey + "=?";
		Cursor cursor = sDatabase.rawQuery(sqlString,
				new String[] { String.valueOf(sql.values.get(sql.tableKey)) });
		List list = outObjectList(cursor, object.getClass());
		return list.size() != 0 ? list.get(0) : null;
	}

	/**
	 * 通过条件查询某表
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public List<Object> query2Where(Object object, String sqlWhere,
			String[] sqlWhereStr) throws Exception {
		TableType sql = getObjectkey(object);
		String sqlString = "select * from " + sql.tableName + " where "
				+ sqlWhere;
		Cursor cursor = sDatabase.rawQuery(sqlString, sqlWhereStr);
		return outObjectList(cursor, object.getClass());
	}

	/**
	 * 返回某表所有数据
	 * 
	 * @param object
	 * @return
	 */
	public List<Object> queryAllObject(Object object) throws Exception {
		TableType sql = getObjectkey(object);
		String sqlString = "select * from " + sql.tableName;
		Cursor cursor = sDatabase.rawQuery(sqlString, null);
		return outObjectList(cursor, object.getClass());
	}

	/**
	 * 对某表所有数据进行分页
	 * 
	 * @param object
	 * @return
	 */
	public List<Object> queryAllObjectForLimit(Object object, int start,
			int pageNum, String where) throws Exception {
		TableType sql = getObjectkey(object);
		String sqlString = "select * from " + sql.tableName + " where 1=1 "
				+ where + " limit " + start + "," + pageNum;
		Cursor cursor = sDatabase.rawQuery(sqlString, null);
		return outObjectList(cursor, object.getClass());
	}

	/**
	 * 返回某表的总条目数
	 * 
	 * @param class1
	 * @return
	 * @throws Exception
	 */
	public Integer queryCount(Object object) throws Exception {
		TableType sql = getObjectkey(object);
		String sqlString = "select count(*) as count from " + sql.tableName;
		Cursor cursor = sDatabase.rawQuery(sqlString, null);
		cursor.moveToFirst();
		return Integer
				.parseInt(cursor.getString(cursor.getColumnIndex("count")));
	}

	/**
	 * 直接使用sql操作数据库
	 * 
	 * @param sql
	 * @param obj
	 */
	public void execForSql(String sql, Object[] obj) {
		sDatabase.execSQL(sql, obj);
	}

	/**
	 * 直接使用sql进行查询返回字符串数组
	 * 
	 * @param sql
	 * @param obj
	 * @return
	 */
	public Cursor queryForSql(String sql, String[] obj) {
		return sDatabase.rawQuery(sql, obj);
	}

	/**
	 * 通过sql语句，强行进行查询转换
	 * 
	 * @param object
	 * @param sqlWhere
	 * @param sqlWhereStr
	 * @return
	 * @throws Exception
	 */
	public List<Object> queryForSql(Object object, String sqlString,
			String[] sqlWhereStr) throws Exception {
		Cursor cursor = sDatabase.rawQuery(sqlString, sqlWhereStr);
		return outObjectList(cursor, object.getClass());
	}

	/**
	 * 通过对象反射表名与ContentValues
	 * 
	 * @param object
	 * @return
	 */
	private TableType object2Content(Object object) throws Exception {
		// 取出注解中的表名，主键，是否递增
		TableName tableName = object.getClass().getAnnotation(TableName.class);
		String tableString = tableName.name();
		String tableKeyString = tableName.tableKey();
		boolean nullable = tableName.nullable();
		// 创建一个contentvalue
		ContentValues values = new ContentValues();
		Field[] allFields = object.getClass().getDeclaredFields();
		for (Field field : allFields) {
			TableProperty tableProperty = field
					.getAnnotation(TableProperty.class);
			// 判断是否被映射进contentvalue
			if (tableProperty.toObject()) {
				Object objValueObject = object.getClass()
						.getMethod("get" + fistString(field.getName()))
						.invoke(object);
				if (objValueObject != null) {
					// 如果属性不为空，则向contentvalue插入。若主键递增则部队主键进行注入
					values.put(field.getName(), String.valueOf(objValueObject));
				}
			}
		}
		return new TableType(tableString, tableKeyString, nullable, values);
	}

	/**
	 * 返回表名主键值
	 * 
	 * @param object
	 * @return
	 */
	private TableType getObjectkey(Object object) throws Exception {
		TableName tableName = object.getClass().getAnnotation(TableName.class);
		String tableString = tableName.name();
		String tableKeyString = tableName.tableKey();
		boolean nullable = tableName.nullable();
		ContentValues values = new ContentValues();
		values.put(
				tableKeyString,
				String.valueOf(object.getClass()
						.getMethod("get" + fistString(tableKeyString))
						.invoke(object)));
		return new TableType(tableString, tableKeyString, nullable, values);
	}

	/**
	 * 返回表关键信息
	 * 
	 * @param object
	 * @return
	 */
	private TableType getTableRes(Object object) throws Exception {
		TableName tableName = object.getClass().getAnnotation(TableName.class);
		String tableString = tableName.name();
		String tableKeyString = tableName.tableKey();
		boolean nullable = tableName.nullable();
		return new TableType(tableString, tableKeyString, nullable, null);
	}

	/**
	 * 反射生成
	 * 
	 * @return
	 */
	private List<Object> outObjectList(Cursor cursor, Class cl)
			throws Exception {
		List<Object> objList = new ArrayList<Object>();
		String[] nameStrings = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Object object = cl.newInstance();
			for (String str : nameStrings) {
				// System.out.println("内容=======" + cursor.getColumnIndex(str));
				Constructor con = Class.forName(
						cl.getDeclaredField(str).getType().toString()
								.replaceAll("class ", "")).getConstructor(
						String.class);
				Field field = cl.getDeclaredField(str);
				field.setAccessible(true);
				String input = cursor.getString(cursor.getColumnIndex(str));
				if (input != null)
					field.set(object, con.newInstance(input));
			}
			objList.add(object);
		}
		return objList;
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

	/**
	 * 表操作内部类
	 */
	class TableType {
		/**
		 * 表名
		 */
		private String tableName;
		/**
		 * 主键
		 */
		private String tableKey;
		/**
		 * 是否递增
		 */
		private Boolean keyType = false;
		/**
		 * 进行操作的结果集
		 */
		private ContentValues values;

		public TableType(String tableName, String tableKey, boolean keyType,
				ContentValues values) {
			this.tableName = tableName;
			this.tableKey = tableKey;
			this.keyType = keyType;
			this.values = values;
		}
	}
}
