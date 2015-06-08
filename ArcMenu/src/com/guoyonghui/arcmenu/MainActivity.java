package com.guoyonghui.arcmenu;

import com.guoyonghui.arcmenu.view.ArcMenu;
import com.guoyonghui.arcmenu.view.ArcMenu.onArcMenuItemClickListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class MainActivity extends FragmentActivity implements onArcMenuItemClickListener {

	private ArcMenu mArcMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);
		
		initViews();
		
		initEvents();
	}
	
	private void initViews() {
		mArcMenu = (ArcMenu)findViewById(R.id.arc_menu);
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragment_container);
		
		if(fragment == null) {
			fragment = new CameraFragment();
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
			ft.remove(oldFragment);
		}
		
		ft.add(R.id.fragment_container, newFragment);
		
		ft.commit();
	}

	@Override
	public void onArcMenuItemClick(View v, int pos) {
		switch (v.getId()) {
		case R.id.arc_menu_item_camera:
			toogleFragment(new CameraFragment());
			break;
		case R.id.arc_menu_item_contact:
			toogleFragment(new ContactFragment());
			break;
		case R.id.arc_menu_item_location:
			toogleFragment(new LocationFragment());
			break;
		case R.id.arc_menu_item_message:
			toogleFragment(new MessageFragment());
			break;
		case R.id.arc_menu_item_music:
			toogleFragment(new MusicFragment());
			break;

		default:
			break;
		}
	}
	
}
