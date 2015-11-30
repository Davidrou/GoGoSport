package com.gogosport.activity;

import com.easemob.applib.controller.HXSDKHelper;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

//public class BaseActivity extends AppCompatActivity {
public class BaseActivity extends FragmentActivity {
	// private LinearLayout rootLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 这句很关键,注意时调用父类的方法
		// super.setContentView(R.layout.activity_base);

		// 经测试在代码里直接声明透明状态栏更有效
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		// // 4.4以上版本可以设置透明状态栏,
		// // 布局可以覆盖到最上面,为了不和status_bar重合,
		// // 设置布局的padding_top为25dp
		// // 而4.4以下,设置0dp就可以
		// WindowManager.LayoutParams localLayoutParams = getWindow()
		// .getAttributes();
		// localLayoutParams.flags =
		// (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
		// WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		// }
		// initToolbar();
	}

	// private void initToolbar() {
	// // TODO Auto-generated method stub
	// Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	// if (toolbar != null) {
	// setSupportActionBar(toolbar);
	// }
	// }
	//
	// @Override
	// public void setContentView(int layoutResID) {
	// setContentView(View.inflate(this, layoutResID, null));
	// }

	// @Override
	// public void setContentView(View view) {
	// rootLayout = (LinearLayout) findViewById(R.id.root_layout);
	// if (rootLayout == null)
	// return;
	// rootLayout.addView(view, new ViewGroup.LayoutParams(
	// ViewGroup.LayoutParams.MATCH_PARENT,
	// ViewGroup.LayoutParams.MATCH_PARENT));
	// initToolbar();
	// }
	@Override
	protected void onResume() {
		super.onResume();
		// onresume时，取消notification显示
		HXSDKHelper.getInstance().getNotifier().reset();

		// umeng
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// umeng
		MobclickAgent.onPause(this);
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

}
