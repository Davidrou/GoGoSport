package com.sportfun.usercenter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.easemob.chatuidemo.utils.URLPropertiesUtil;
import com.example.sportfun.R;
import com.geminno.entity.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/*
 * 个人中心
 * 
 */
public class Information extends Activity {

	private TextView t_userinfo, t_address;
	private TextView t_account;
	private TextView t_sex;
	private TextView t_barthday;
	private TextView t_sign;
	private Button b_edit;

	private HttpUtils httpUtils;
	RequestQueue requestQueue;
	Properties properties = URLPropertiesUtil.getProperties(this);
	String u = properties.getProperty("u");
	private String uploadURL = u + "AndroidUploadServlet";
	String getUserUrl;
	private ImageView photo;
	private String[] items = { "拍照", "相册" };
	private String title = "更改头像";

	private static final int PHOTO_CARMERA = 1;
	private static final int PHOTO_PICK = 2;
	private static final int PHOTO_CUT = 3;
	String account;// 当前用户account
	String imgPath;// 上传的图片路径（根据当前用户id，存入数据库）
	String photoPath;// 从数据库获取的图片路径
	
	private UserInfo user;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bitmap bitmap = (Bitmap) msg.obj;
			photo.setImageBitmap(bitmap);

		};

	};

	// 创建一个以当前系统时间为名称的文件，防止重复
	private File tempFile = new File(Environment.getExternalStorageDirectory(),
			getPhotoFileName());

	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("'PNG'_yyyyMMdd_HHmmss");
		String picname = sdf.format(date) + ".png";
		String picUrl = URLPropertiesUtil.getProperties(this).getProperty(
				"picUrl");
		imgPath = picUrl + picname;
		return sdf.format(date) + ".png";
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_information);
		initView();// 初始化控件（给每个控件获取值）
		// 1.初始化RequestQueue对象
		requestQueue = Volley.newRequestQueue(getApplicationContext());
		// 获取当前用户
		SharedPreferences sharedPreferences = getSharedPreferences("user",
				Activity.MODE_PRIVATE);
		account = sharedPreferences.getString("account", "");

		// 设置每个控件的值
		getUserUrl = u + "AndroidQueryUserByAccount?account=" + account;
		
		getUserInfo(getUserUrl);
		
		photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder dialog = AndroidUtil.getListDialogBuilder(
						Information.this, items, title, dialogListener);
				dialog.show();
			}
		});
		httpUtils = new HttpUtils(10000);
//编辑按钮监听
		b_edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent();
				intent.setClass(Information.this, ModificationActivity.class);
				Bundle bundle = new Bundle();
			
				bundle.putSerializable("user", user);

				intent.putExtras(bundle);
				startActivityForResult(intent, 0);

			}
		});

	}
	@Override
	protected void onResume() {
		super.onResume();
		getUserInfo(getUserUrl);
	
	}


	public void getUserInfo(String url){

		// 2. 发送StringRequest请求（获取用户信息）
		StringRequest getRequsset = new StringRequest(Method.GET, url,
				new Listener<String>() {

					

					@Override
					public void onResponse(String response) {

						Gson gson = new GsonBuilder().setDateFormat("yyyyMMdd")
								.create();
						user = gson.fromJson(response, UserInfo.class);

						t_account.setText(user.getAccount().toString());
						t_address.setText(user.getUser_position().toString());
						if (user.getPhoto() != null) {
							photoPath = user.getPhoto().toString();
							// 设置图片控件内容
							downPic(photo, photoPath);
						}
						if (user.getUser_name() != null) {
							t_userinfo.setText(user.getUser_name().toString());
						}
						if (user.getSex() != null) {
							t_sex.setText(user.getSex().toString());
						}
						if (user.getBirthday() != null) {
							t_barthday.setText(user.getBirthday().toString());
						}
						if (user.getSignature() != null) {
							t_sign.setText(user.getSignature().toString());
						}

					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

					}
				});

		// 3.将StringRequest加入到RequestQueue当中
		requestQueue.add(getRequsset);
	}
	//
	private void initView() {
		t_userinfo = (TextView) this.findViewById(R.id.user_name);
		t_account = (TextView) this.findViewById(R.id.account);
		t_address = (TextView) this.findViewById(R.id.adress);
		t_sex = (TextView) this.findViewById(R.id.sex);
		t_barthday = (TextView) this.findViewById(R.id.barthday);
		t_sign = (TextView) this.findViewById(R.id.gexing_sign);
		b_edit = (Button) this.findViewById(R.id.edit);
		photo = (ImageView) this.findViewById(R.id.photo);

	}

	//

	public void downPic(ImageView photo, final String picUrl) {
		this.photo = photo;

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

	// 更改头像dialogListener
	private android.content.DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				startCamera(dialog);// 调用拍照
				break;
			case 1:
				startPick(dialog);// 调用相册
				break;

			default:
				break;
			}
		}
	};

	// 调用系统相机
	protected void startCamera(DialogInterface dialog) {
		dialog.dismiss();
		// 调用系统的拍照功能
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("camerasensortype", 2); // 调用前置摄像头
		intent.putExtra("autofocus", true); // 自动对焦
		intent.putExtra("fullScreen", false); // 全屏
		intent.putExtra("showActionIcons", false);
		// 指定调用相机拍照后照片的存储路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		startActivityForResult(intent, PHOTO_CARMERA);
	}

	// 调用系统相册
	protected void startPick(DialogInterface dialog) {
		dialog.dismiss();
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, PHOTO_PICK);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_CARMERA:
			startPhotoZoom(Uri.fromFile(tempFile), 300);
			break;
		case PHOTO_PICK:
			if (null != data) {
				startPhotoZoom(data.getData(), 300);
			}
			break;
		case PHOTO_CUT:
			if (null != data) {
				setPicToView(data);
			}
			break;
		}
		switch (resultCode){	
		case 4:
			String name = data.getExtras().getString("name1");
			String sex = data.getExtras().getString("name2");
			String address = data.getExtras().getString("name3");
			String birthday = data.getExtras().getString("name4");
			String sign = data.getExtras().getString("name5");
			t_userinfo.setText(name);
			System.out.println(sex);
			t_sex.setText(sex);
			t_address.setText(address);
			t_barthday.setText(birthday);
			t_sign.setText(sign);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 调用系统裁剪
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以裁剪
		intent.putExtra("crop", true);
		// aspectX,aspectY是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY是裁剪图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		// 设置是否返回数据
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_CUT);
	}

	// 将裁剪后的图片显示在ImageView上
	private void setPicToView(Intent data) {
		Bundle bundle = data.getExtras();
		if (null != bundle) {
			final Bitmap bmp = bundle.getParcelable("data");
			photo.setImageBitmap(bmp);
			saveCropPic(bmp);// 保存在sd卡
			upload();// 上传到服务器

			Log.i("MainActivity", tempFile.getAbsolutePath());

		}
	}

	// 把裁剪后的图片保存到sdcard上
	private void saveCropPic(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileOutputStream fis = null;
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		try {
			fis = new FileOutputStream(tempFile);
			fis.write(baos.toByteArray());
			fis.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != baos) {
					baos.close();
				}
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 上传文件到服务器
	protected void upload() {
		RequestParams params = new RequestParams();
		params.addBodyParameter(tempFile.getPath().replace("/", ""), tempFile);
		System.out.println(tempFile);
		httpUtils.send(HttpMethod.POST, uploadURL, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException e, String msg) {
						Toast.makeText(Information.this, "上传失败，检查一下服务器地址是否正确",
								Toast.LENGTH_SHORT).show();
						Log.i("MainActivity", e.getExceptionCode() + "====="
								+ msg);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Toast.makeText(Information.this, "上传成功，马上去服务器看看吧！",
								Toast.LENGTH_SHORT).show();
						Log.i("MainActivity", "====upload_error====="
								+ responseInfo.result);
						// 将图片名称存到当前用户所对应的信息表中

						String updateUrl = u + "AndroidUpdatePic?account="
								+ account + "&imgPath=" + imgPath;

						// 2. 发送StringRequest请求（将修改好的图片存入数据库）
						StringRequest picRequsset = new StringRequest(
								Method.GET, updateUrl, new Listener<String>() {

									@Override
									public void onResponse(String response) {

									}
								}, new ErrorListener() {

									@Override
									public void onErrorResponse(
											VolleyError error) {

									}
								});
						// 3.将StringRequest加入到RequestQueue当中
						requestQueue.add(picRequsset);

					}
				});
	}

	public void back(View v){
		finish();
	} 
	
}
