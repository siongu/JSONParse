package com.example.android_jsonparse;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class MyFragment extends Fragment {
	public String content;

	public MyFragment(String content) {
		this.content = content;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.browse, null);
		TextView tv_content = (TextView) view.findViewById(R.id.content);
		tv_content.setText(Html
				.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
		tv_content.append(content);
//		System.out.println("view++++++"+view);
		return view;
	}

}
