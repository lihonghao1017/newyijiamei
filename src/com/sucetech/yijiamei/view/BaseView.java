package com.sucetech.yijiamei.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.mapbar.scale.ScaleLinearLayout;
import com.sucetech.yijiamei.manager.EventManager;
import com.sucetech.yijiamei.manager.EventObserver;

public abstract class BaseView extends ScaleLinearLayout implements EventObserver {
	public EventManager mEventManager;
	public ViewGroup prentView;

	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mEventManager=EventManager.getEventManager();
		mEventManager.addObserver(this);
		initView(context);
	}

	public BaseView(Context context) {
		super(context);
		mEventManager=EventManager.getEventManager();
		mEventManager.addObserver(this);
		initView(context);
	}
	public BaseView(Context context,ViewGroup ParentView) {
		super(context);
		mEventManager=EventManager.getEventManager();
		mEventManager.addObserver(this);
		this.prentView=ParentView;
		initView(context);
	}
	public abstract void initView(Context context);

}
