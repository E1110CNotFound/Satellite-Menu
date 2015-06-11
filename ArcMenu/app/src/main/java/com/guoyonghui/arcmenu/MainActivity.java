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
			fragment = new AdobeFragment();
			fm.beginTransaction()
				.add(R.id.fragment_container, fragment)
				.commit();
		}
	}
	
	private void initEvents() {
		mArcMenu.setOnArcMenuItemClickListener(this);
	}
	
	private void toogleFragment(Fragment newFragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment oldFragment = fm.findFragmentById(R.id.fragment_container);
		if(oldFragment != null) {
			if(oldFragment.getClass().equals(newFragment.getClass())) {
				return;
			}
			ft.remove(oldFragment);
		}
		
		ft.add(R.id.fragment_container, newFragment);
		
		ft.commit();
	}

	@Override
	public void onArcMenuItemClick(View v, int pos) {
		switch (v.getId()) {
		case R.id.menu_item_adobe:
			toogleFragment(new AdobeFragment());
			break;
		case R.id.menu_item_amazon:
			toogleFragment(new AmazonFragment());
			break;
		case R.id.menu_item_android:
			toogleFragment(new AndroidFragment());
			break;
		case R.id.menu_item_angry_bird:
			toogleFragment(new AngryBirdFragment());
			break;
		case R.id.menu_item_flash:
			toogleFragment(new FlashFragment());
			break;

		default:
			break;
		}
	}
	
}
