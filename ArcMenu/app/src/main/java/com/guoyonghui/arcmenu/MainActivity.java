package com.guoyonghui.arcmenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.guoyonghui.arcmenu.view.ArcMenu;

public class MainActivity extends FragmentActivity implements ArcMenu.OnArcMenuItemClickListener {

	private ArcMenu mArcMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initViews();
		
		initEvents();
	}
	
	private void initViews() {
		mArcMenu = (ArcMenu)findViewById(R.id.arc_menu);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_container);
		
		if(fragment == null) {
			fragment = ContentFragment.newInstance(R.layout.fragment_adobe);
			fm.beginTransaction()
				.add(R.id.fragment_container, fragment)
				.commit();
		}
	}
	
	private void initEvents() {
		mArcMenu.setOnArcMenuItemClickListener(this);
	}
	
	private void toogleFragment(ContentFragment newFragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		ContentFragment oldFragment = (ContentFragment) fm.findFragmentById(R.id.fragment_container);
		if(oldFragment != null) {
			ft.remove(oldFragment);
		}
		
		ft.add(R.id.fragment_container, newFragment);
		
		ft.commit();
	}

	@Override
	public void onArcMenuItemClick(View v, int pos) {
		int layoutResID;
		switch (v.getId()) {
		case R.id.menu_item_adobe:
			layoutResID = R.layout.fragment_adobe;
			break;
		case R.id.menu_item_amazon:
			layoutResID = R.layout.fragment_amazon;
			break;
		case R.id.menu_item_android:
			layoutResID = R.layout.fragment_android;
			break;
		case R.id.menu_item_angry_bird:
			layoutResID = R.layout.fragment_angry_bird;
			break;
		case R.id.menu_item_flash:
			layoutResID = R.layout.fragment_flash;
			break;

		default:
			layoutResID = R.layout.fragment_adobe;
			break;
		}

		toogleFragment(ContentFragment.newInstance(layoutResID));
	}
	
}
