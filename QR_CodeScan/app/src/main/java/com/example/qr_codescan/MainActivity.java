package com.example.qr_codescan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//TODO 看到MipcaActivityCapture类了
public class MainActivity extends Activity {
	// 扫描的请求码
	private final static int SCANNIN_GREQUEST_CODE = 1;
	/**
	 * 显示扫描结果
	 */
	private TextView mTextView;
	/**
	 * 显示扫描拍的图片
	 */
	private ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTextView = (TextView) findViewById(R.id.result);
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);

		// 点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
		// 扫描完了之后调到该界面
		Button mButton = (Button) findViewById(R.id.button1);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MipcaActivityCapture.class);
				// 采用FLAG_ACTIVITY_CLEAR_TOP，退出整个程序（多activity）
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				/**
				 * 首先startActivityForResult
				 * 有两个参数，第一个为当前activity的intent（假设为activity
				 * A），至于第二个参数的用法，还是先看一下官方SDK文档的解释 :requestCode ： If >= 0, this
				 * code will be returned in onActivityResult() when the activity
				 * exits.----------> 一般都是设置大于或者等于0，因为activity A可能不单单要跳转到activity
				 * B，也可能跳转到activity C，D，E……，这些activity返回来的数据都交由activityA处理，
				 * 那activityA又通过什么来分辨返回来的数据到底是哪个activity回来的呢。 所以在activity
				 * A跳转到某一个activity时
				 * ，要设定目标activity的requestCode，这个requestCode就唯一地标识了相对应的activity。
				 * 如下所示，当要跳转到activityB时，SCANNIN_GREQUEST_CODE表示的是activityB的标识，
				 */
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
	}

	/**
	 * 数据处理方法onActivityResult
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/**
		 * activityA通过复写onActivityResult方法来处理这些activity返回来的数据
		 * 1.第一个参数requestCode是用来区分哪个activity回来的数据，可以通过swith语句来筛选
		 * 2.第二个参数为resultCode，也是一个int类型，如果activity
		 * B有几种不同返回的结果，同样地可以通过resultCode来筛选 3.第三个参数为Intent
		 * data，为activity返回来的数值，如通过data.getStringExtra("key");就可以取出来
		 */
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				if (resultCode == RESULT_OK) {
					Bundle bundle = data.getExtras();// 这是从MipcaActivityCapture返回来的数据
					// 显示扫描到的内容
					mTextView.setText(bundle.getString("result"));
					// 显示
					mImageView.setImageBitmap((Bitmap) data
							.getParcelableExtra("bitmap"));
				}
				break;
		}
		/**
		 * 注意：调用setResult()方法跳回原来的activity时，一定要调用finish方法结束当前的activity
		 * 另外，如果activity A只需要跳转到activity B而已，那跳转时，request
		 * code只要大于或者等于0就行，而数据处理的方法onActivityResult()如下结构就行了，不用筛选request code。
		 */
	}

}
