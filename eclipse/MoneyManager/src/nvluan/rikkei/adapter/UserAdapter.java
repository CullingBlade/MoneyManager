package nvluan.rikkei.adapter;

import java.util.ArrayList;

import nvluan.rikkei.moneymanager.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<UserItem> {
	private UserItem mUserItem;
	private ArrayList<UserItem> mUserItemArray;
	private Context context;
	private int position;

	public UserAdapter(Context context, int textViewResourceId, ArrayList<UserItem> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.mUserItemArray = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		this.position = position;
		UserHolder holder = new UserHolder();

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.user_item, parent, false);

			holder.tvUsername = (TextView) row.findViewById(R.id.tvUsername);

			row.setTag(holder);
		} else {
			holder = (UserHolder) row.getTag();
		}

		mUserItem = mUserItemArray.get(position);

		holder.tvUsername.setText(mUserItem.getUsername());
		return row;
	}

	public class UserHolder {
		TextView tvUsername;
	}

}
