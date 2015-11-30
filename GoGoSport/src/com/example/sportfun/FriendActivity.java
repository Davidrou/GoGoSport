package com.example.sportfun;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.geminno.entity.UserInfo;
import com.sport.chat.AlertDialog;
import com.sport.chat.ChatActivity;
import com.sport.chat.DemoHXSDKHelper;
import com.sportfun.application.MyApplication;

public class FriendActivity extends Activity implements OnClickListener {
	private static final String TAG = "FriendActivity";
	private TextView tv_name, tv_account, tv_sex, tv_address, tv_birthday,
			tv_sign;// 个性签名
	private Button bt_chat;
	private ImageView user_photo;
	private Intent intent;
	private UserInfo userInfo;
	String picPath;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);
		initView();
		initListener();

		// 获取数据
		intent = getIntent();
		userInfo = (UserInfo) intent.getSerializableExtra("user");
		tv_name.setText(userInfo.getUser_name());
		tv_account.setText(userInfo.getAccount());
		tv_sex.setText(userInfo.getSex());
		tv_address.setText(userInfo.getUser_position());
		tv_birthday.setText(userInfo.getBirthday().toString());
		tv_sign.setText(userInfo.getSignature());
		picPath = userInfo.getPhoto();// 获取图片路径
		downPic(user_photo, picPath);
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_name = (TextView) this.findViewById(R.id.user_name);
		tv_account = (TextView) this.findViewById(R.id.user_account);
		tv_sex = (TextView) this.findViewById(R.id.user_sex);
		tv_address = (TextView) this.findViewById(R.id.user_address);
		tv_birthday = (TextView) this.findViewById(R.id.user_barthday);
		tv_sign = (TextView) this.findViewById(R.id.user_sign);
		user_photo = (ImageView) this.findViewById(R.id.user_photo);
		bt_chat = (Button) this.findViewById(R.id.bt_chat);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		bt_chat.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
		case R.id.bt_chat:// 聊天按钮
			// 加好友
			String toChatName = null;
			//Log.i("userInfo.getAccount()",userInfo.getAccount());
			//Log.i("MyApplication.getInstance().getUserName()",MyApplication.getInstance().getUserName());
			if (!TextUtils.isEmpty(userInfo.getAccount())) {
				toChatName = userInfo.getAccount();
				addContentToChat(toChatName);
			} else {
				startActivity(new Intent(this, AlertDialog.class).putExtra(
						"msg", "出现了该用户用户名的罕见错误"));
				return;
			}

			break;
		default:
			break;
		}
	}

	private void addContentToChat(final String toChatName) {
		// TODO Auto-generated method stub
		final Intent intent = new Intent(this,ChatActivity.class);
		intent.putExtra("userId", toChatName);
		
		if (MyApplication.getInstance().getUserName()
				.equals(toChatName)) {
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg",
					"天了噜~怎么可以添加自己为好友呢?"));
			return;
		}
		if(!((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().containsKey(toChatName)){
			//如果不在好友列表,先加好友
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						EMContactManager.getInstance().addContact(toChatName, "");
						Log.i(TAG, "添加"+toChatName+"成功");
						startActivity(intent);
						
					} catch (EaseMobException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}else{
			Log.i(TAG,"对方已在好友已经在好友列表中,直接聊天");
			startActivity(intent);
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bitmap bitmap = (Bitmap) msg.obj;
			user_photo.setImageBitmap(bitmap);

		};

	};

	public void downPic(ImageView user_photo, final String picUrl) {
		this.user_photo = user_photo;

		new Thread() {

			@Override
			public void run() {
				try {
					if (picUrl == null) {

						System.out.println("空的url");
					}
					URL url = new URL(picUrl);// ???
					// ???
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(5000);//
					connection.setReadTimeout(5000);//
					InputStream is = connection.getInputStream();
					//
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					//
					Message message = Message.obtain();
					message.obj = bitmap;
					handler.sendMessage(message);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e);
					e.printStackTrace();
				}
			}

		}.start();
	}

	public void back(View v) {
		finish();
	}

}
