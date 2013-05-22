package com.taxiCliect.util.db;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
/**
 * db初始化类
 * @author talkliu
 *
 */
public class DataBase extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private List<String> updateDatabasesList;
	private Context context;

	public DataBase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context=context;
		// TODO Auto-generated constructor stub
	}

	public DataBase(Context context, String name, int version) {
		this(context, name, null, version);
		this.context=context;
	}

	public DataBase(Context context, String name) {
		this(context, name, VERSION);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 表结构初始化'
		try {
			for (String sql : new Xml2Data(context, null).getCreateSql()) {
				db.execSQL(sql);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 表结构更新
		for (String sql : updateDatabasesList) {
			db.execSQL(sql);
		}
	}

	public List<String> getUpdateDatabasesList() {
		return updateDatabasesList;
	}

	public void setUpdateDatabasesList(List<String> updateDatabasesList) {
		this.updateDatabasesList = updateDatabasesList;
	}

}

