package nvluan.rikkei.moneymanager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import nvluan.rikkei.adapter.UserAdapter;
import nvluan.rikkei.adapter.UserItem;
import nvluan.rikkei.dbmanager.UserDbManager;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
	private UserItem mUserItem;
	private int position;
	private UserItem mUser;
	private ArrayList<UserItem> mUserArray;
	private UserAdapter mUserAdapter;
	private final String encrypt = "!|<>*(";

	private UserDbManager mUserDbManager;

	private RelativeLayout rlListUser0;
	private RelativeLayout rlListUser1;
	private Button btnCreateUser0;
	private ImageButton btnCreateUser1;
	private ListView lvUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initLayout();
		accessDatabase();
		selectUser();
		addUser();
	}

	@Override
	protected void onResume() {
		initView();
		super.onResume();
	}

	private void accessDatabase() {
		mUserDbManager = new UserDbManager(MainActivity.this);
		mUserDbManager.createTable();
	}

	private void addUser() {
		btnCreateUser0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addUserDialog();
			}
		});

		btnCreateUser1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addUserDialog();
			}
		});
	}

	private void addUserDialog() {
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.add_user_dialog);
		dialog.setTitle("Add User");
		final EditText etUsername = (EditText) dialog.findViewById(R.id.etUsername);
		final EditText etPassword = (EditText) dialog.findViewById(R.id.etPassword);
		final EditText etPasswordC = (EditText) dialog.findViewById(R.id.etPasswordC);
		Button btnAddUser = (Button) dialog.findViewById(R.id.btnAddUser);
		dialog.show();

		btnAddUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();
				String passwordC = etPasswordC.getText().toString();
				if (username.trim().length() == 0) {
					showToast("Please fill username");
				} else {
					if (mUserDbManager.isKeyExist("tblUser", "username", username)) {
						showToast("User has existed!");
					} else {
						if (!password.equals(passwordC)) {
							showToast("Check password!");
						} else {
							UserItem data = new UserItem();
							if (password.trim().length() == 0) {
								data.setPassword_enable(false);
							} else {
								data.setPassword_enable(true);
							}
							data.setUsername(username);
							data.setPassword(computeMd5(password + encrypt));
							mUserDbManager.insertUser(data);
							mUserArray.add(data);
							mUserAdapter.notifyDataSetChanged();

							if (mUserArray.size() == 0) {
								rlListUser0.setVisibility(View.VISIBLE);
								rlListUser1.setVisibility(View.INVISIBLE);
							} else {
								rlListUser0.setVisibility(View.INVISIBLE);
								rlListUser1.setVisibility(View.VISIBLE);
							}

							dialog.dismiss();
						}
					}
				}
			}
		});

	}

	private void selectUser() {
		lvUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mUser = mUserArray.get(position);
				if (mUser.isPassword_enable()) {
					loginUser(mUser.getUsername());
				} else {
				}
			}
		});
	}

	protected void loginUser(String username) {
		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.login_dialog);
		dialog.setTitle("Login");
		final EditText etUsername = (EditText) dialog.findViewById(R.id.etUsername);
		final EditText etPassword = (EditText) dialog.findViewById(R.id.etPassword);
		Button btnLogin = (Button) dialog.findViewById(R.id.btnLogin);

		etUsername.setText(username);
		etUsername.setEnabled(false);
		dialog.show();

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString() + encrypt;
				if (mUserDbManager.checkPassword(username, computeMd5(password))) {
					dialog.dismiss();
				} else {
					showToast("Password wrong!");
				}
			}
		});

	}

	private void initView() {

		mUserArray = new ArrayList<UserItem>();
		SQLiteDatabase database = mUserDbManager.accessDatabase();
		Cursor cursor = database.query("tblUser", null, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				mUser = new UserItem();
				mUser.setUsername(cursor.getString(1));
				mUser.setPassword(cursor.getString(2));
				if (mUser.getPassword().trim().length() == 0) {
					mUser.setPassword_enable(false);
				} else {
					mUser.setPassword_enable(true);
				}
				mUserArray.add(mUser);
				cursor.moveToNext();
			}
		}

		if (mUserArray.size() == 0) {
			rlListUser0.setVisibility(View.VISIBLE);
			rlListUser1.setVisibility(View.INVISIBLE);
		} else {
			rlListUser0.setVisibility(View.INVISIBLE);
			rlListUser1.setVisibility(View.VISIBLE);
		}
		mUserAdapter = new UserAdapter(this, R.layout.user_item, mUserArray);
		lvUser.setAdapter(mUserAdapter);
	}

	private void initLayout() {
		rlListUser0 = (RelativeLayout) findViewById(R.id.rlListUser0);
		rlListUser1 = (RelativeLayout) findViewById(R.id.rlListUser1);
		btnCreateUser0 = (Button) findViewById(R.id.btnCreateUser0);
		btnCreateUser1 = (ImageButton) findViewById(R.id.btnCreateUser1);
		lvUser = (ListView) findViewById(R.id.lvUser);
	}

	private String computeMd5(String message) {
		String md5 = null;
		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(message.getBytes());
			byte messageDigest[] = digest.digest();

			StringBuffer Md5Hash = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2) {
					h = "0" + h;
				}
				Md5Hash.append(h);
			}
			md5 = Md5Hash.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5;
	}

	// private String computeSHA(String message) {
	// String shaHash = null;
	//
	// MessageDigest mdSha1 = null;
	// try {
	// mdSha1 = MessageDigest.getInstance("SHA-1");
	// } catch (NoSuchAlgorithmException e1) {
	// Log.e("myapp", "Error initializing SHA1 message digest");
	// }
	// try {
	// mdSha1.update(message.getBytes("ASCII"));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
	// byte[] data = mdSha1.digest();
	// try {
	// shaHash = convertToHex(data);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return shaHash;
	// }
	//
	// private static String convertToHex(byte[] data) throws
	// java.io.IOException {
	//
	// StringBuffer sb = new StringBuffer();
	// String hex = null;
	//
	// hex = Base64.encodeToString(data, 0, data.length, 0);
	//
	// sb.append(hex);
	//
	// return sb.toString();
	// }

	private void showToast(String message) {
		Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
	}
}
