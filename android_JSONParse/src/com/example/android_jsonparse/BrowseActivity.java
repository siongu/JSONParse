package com.example.android_jsonparse;

import java.util.zip.Inflater;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jsonparseutils.HttpJSONParseUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class BrowseActivity extends Activity {
	protected static final int SHOW = 0;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	private TextView tv_content;
	private ImageView backView;
	private ActionBar aBar;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == SHOW) {
				String content = (String) msg.obj;
				tv_content.append(content);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse);
		aBar=getActionBar();
//		aBar.setDisplayHomeAsUpEnabled(true);
//		aBar.setHomeButtonEnabled(true);
		aBar.setCustomView(R.layout.browse_title_bar);
		aBar.setDisplayShowCustomEnabled(true);
		aBar.setDisplayShowHomeEnabled(false);
		aBar.setDisplayShowTitleEnabled(false);
		backView=(ImageView) findViewById(R.id.back);
		mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		tv_content = (TextView) findViewById(R.id.content);
		tv_content.setText(Html
				.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
		new Thread() {
			@Override
			public void run() {
				String jString = refreshContentFromServer();
				Message msg = mHandler.obtainMessage();
				msg.what = SHOW;
				msg.obj = jString;
				mHandler.sendMessage(msg);
				super.run();
			}
		}.start();
		mPullRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						new GetDataFromServer().execute();
					}
				});
		
		backView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(BrowseActivity.this,MainActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});
	}

	private String refreshContentFromServer() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String url = bundle.getString("url");
		String jString = HttpJSONParseUtils.JsonObjectParse(url);
		return jString;
	}

	class GetDataFromServer extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(200);
				String result = refreshContentFromServer();
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			tv_content
					.setText(Html
							.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
			tv_content.append(result);
			mPullRefreshScrollView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
//		getMenuInflater().inflate(R.menu.browse, menu);
		return super.onCreateOptionsMenu(menu);
	}

/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case R.id.back:
			Intent intent=new Intent(BrowseActivity.this,MainActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			break;
		}
		return super.onOptionsItemSelected(item);
	}*/
	
	
	
}
