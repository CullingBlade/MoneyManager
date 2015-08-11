package nvluan.rikkei.dbmanager;

import nvluan.rikkei.adapter.UserItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.widget.Toast;

public class UserDbManager extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "MoneyManager";

	private static final String USER_TABLE_NAME = "tblUser";
	private Context context;

	public UserDbManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public SQLiteDatabase accessDatabase() {
		SQLiteDatabase database = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE,
				null);
		return database;
	}

	public void createTable() {
		SQLiteDatabase database = accessDatabase();
		if (!isTableExist(database, USER_TABLE_NAME)) {
			createUserTbl(database);
		}

		database.close();
	}

	public void insertUser(UserItem data) {
		SQLiteDatabase database = accessDatabase();
		ContentValues values = new ContentValues();

		String username = data.getUsername();
		values.put("username", username);
		values.put("password", data.getPassword());
		if (database.insert(USER_TABLE_NAME, null, values) == -1) {
			showToast("Can not add User!");
		}
	}

	public boolean isTableExist(SQLiteDatabase database, String tblName) {
		Cursor cursor = database.rawQuery(
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tblName + "'",
				null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public boolean isKeyExist(String tblName, String row, String key) {
		SQLiteDatabase database = accessDatabase();
		String queryString = "select * from " + tblName + " where " + row + " = '" + key + "'";
		Cursor cursor = database.rawQuery(queryString, null);

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				database.close();
				return true;
			}
		}
		cursor.close();
		database.close();
		return false;
	}

	public boolean checkPassword(String username, String password) {
		SQLiteDatabase database = accessDatabase();
		String querryString = "select * from " + USER_TABLE_NAME + " where username = '" + username
				+ "'";
		Cursor cursor = database.rawQuery(querryString, null);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			String temp = cursor.getString(2);
			if (temp.equals(password)) {
				cursor.close();
				database.close();
				return true;
			}
		}
		cursor.close();
		database.close();
		return false;
	}

	private void createUserTbl(SQLiteDatabase db) {
		String sql = "CREATE TABLE tblUser (";
		sql += "id INTEGER,";
		sql += "username TEXT,";
		sql += "password TEXT,";
		sql += "PRIMARY KEY(ID) )";
		db.execSQL(sql);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("UserDbManager", "onCreate!");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e("UserDbManager", "onUpgrade!");
	}

	private void showToast(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

}
