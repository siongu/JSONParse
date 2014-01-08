package com.example.android_jsonparse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

import com.example.jsonparseutils.GetImageFromServerUtils;
import com.example.jsonparseutils.HttpJSONParseUtils;
import com.example.jsonparseutils.Person;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends Activity implements OnScrollListener,
		OnClickListener {
	private static final int REFRESH = 0;
	private static final int LOADMORE = 1;
	private static final int LOADING = 2;
	private static final int NO_MORE = 3;
	private static final int DETAILS = 4;
	private List<Map<String, Object>> listMap;
	private List<Person> persons;
	private SimpleAdapter adapter;
	private PullToRefreshListView mPullRefreshListView;
	private MyFragment mFragment;
	private FragmentManager fmManager;
	private FragmentTransaction fTransaction;
	private ListView mView;
	private View footerView;
	private View loading;
	private Button loadButton;
	private String url = "http://192.168.1.104:80/?m=1";
	private int lastIndex = 0;
	private int lastItemIndex = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH:
				mView.setAdapter(adapter);
				mPullRefreshListView.onRefreshComplete();
				break;
			case LOADING:
				loadButton.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				break;
			case LOADMORE:
				loadButton.setVisibility(View.VISIBLE);
				loading.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				mView.setSelection(lastItemIndex);
				break;
			case NO_MORE:
				loadButton.setVisibility(View.VISIBLE);
				loading.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "没有更多了!",
						Toast.LENGTH_SHORT).show();
				break;
		/*	case DETAILS:
				
				 * String content=(String) msg.obj;
				 * System.out.println("content++++++"+content); mFragment = new
				 * MyFragment(content);
				 * fTransaction=fmManager.beginTransaction();
				 * fTransaction.replace(R.id.viewgroup, mFragment);
				 * fTransaction.addToBackStack(null); fTransaction.commit();
				 
				break;*/
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mView = mPullRefreshListView.getRefreshableView();
		mView.setSelector(getResources().getDrawable(R.drawable.list_selector));
		footerView = View.inflate(getApplicationContext(),
				R.layout.footer_view, null);
		loading = footerView.findViewById(R.id.loading);
		loadButton = (Button) footerView.findViewById(R.id.load);
		mView.addFooterView(footerView);
		mView.setOnScrollListener(this);
		loadButton.setOnClickListener(this);
		fmManager = getFragmentManager();
		listMap = new ArrayList<Map<String, Object>>();
		refreshListView();
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						refreshListView();
					}
				});
		// 给listView的列表项注册监听器
		mView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), BrowseActivity.class);
				String url_details = "http://192.168.1.104/view?id=" + position;
				intent.putExtra("url", url_details);
				startActivity(intent);

				// 用fragment显示详细信息
				// final String url_details = "http://192.168.1.101/view?id=" +
				// position;
				// new Thread(){
				// public void run() {
				// String
				// jString=HttpJSONParseUtils.JsonObjectParse(url_details);
				// Message msg=Message.obtain();
				// msg.what=DETAILS;
				// msg.obj=jString;
				// mHandler.sendMessage(msg);
				// };
				// }.start();
			}
		});
	}

	public void refreshListView() {
		new Thread() {
			@Override
			public void run() {
				persons = HttpJSONParseUtils.JsonArrayParse(url);
				if (persons != null) {
					listMap.clear();
					int num = persons.size() > 6 ? 6 : persons.size();
					for (int i = 0; i < num; i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("name",
								getApplicationContext()
										.getString(R.string.name)
										+ persons.get(i).getName());
						map.put("age",
								getApplicationContext().getString(R.string.age)
										+ persons.get(i).getAge());
						map.put("address",
								getApplicationContext().getString(
										R.string.address)
										+ persons.get(i).getAddress());
						map.put("headImg", persons.get(i).getHeadImg());
						listMap.add(map);
					}
					adapter = new SimpleAdapter(getApplicationContext(),
							listMap, R.layout.list_items, new String[] {
									"name", "age", "address", "headImg" },
							new int[] { R.id.name, R.id.age, R.id.address,
									R.id.img });
					// SimpleAdapter
					// 不能直接显示Bitmap，需要通过setViewBinder方法设置要显示Bitmap图片
					adapter.setViewBinder(new ViewBinder() {
						@Override
						public boolean setViewValue(View view, Object data,
								String textRepresentation) {
							// 判断view是否为ImageView，data是否为Bitmap类型数据
							if (view instanceof ImageView
									&& data instanceof Bitmap) {
								ImageView img = (ImageView) view;
								Bitmap bm = (Bitmap) data;
								img.setImageBitmap(bm);
								return true;
							} else {
								return false;
							}
						}
					});
					mHandler.sendEmptyMessage(REFRESH);
					super.run();
				}else{
					System.out.println("persons is null!");
				}
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.load) {
			loadMoreData();
		}
	}

	public void loadMoreData() {
		new Thread() {
			@Override
			public void run() {
				try {
					if (lastItemIndex + 1 <= persons.size()) {
						mHandler.sendEmptyMessage(LOADING);
						Thread.sleep(1000);
						List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
						int startPosition = lastItemIndex;
						// System.out.println("lastItemIndex*****"+lastItemIndex);
						int num = lastItemIndex + 5 > persons.size() ? persons
								.size() : lastItemIndex + 5;
						for (int i = startPosition; i < num; i++) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("name",
									getApplicationContext().getString(
											R.string.name)
											+ persons.get(i).getName());
							map.put("age",
									getApplicationContext().getString(
											R.string.age)
											+ persons.get(i).getAge());
							map.put("address",
									getApplicationContext().getString(
											R.string.address)
											+ persons.get(i).getAddress());
							map.put("headImg", persons.get(i).getHeadImg());
							list.add(map);
						}
						listMap.addAll(list);
						mHandler.sendEmptyMessage(LOADMORE);
					} else {
						mHandler.sendEmptyMessage(LOADING);
						Thread.sleep(1000);
						mHandler.sendEmptyMessage(NO_MORE);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.clearCache:
			return GetImageFromServerUtils.clearCache();
		default:
			return false;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// ListView滚动到底部自动加载代码部分
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// System.out.println("firstVisibleItem*****" + firstVisibleItem);
		// System.out.println("visibleItemCount*****" + visibleItemCount);
		lastItemIndex = firstVisibleItem + visibleItemCount - 1;
		lastIndex = lastItemIndex + 1;
	}

}
