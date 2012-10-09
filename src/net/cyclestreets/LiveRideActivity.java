package net.cyclestreets;

import net.cyclestreets.planned.Route;
import net.cyclestreets.service.LiveRideService;
import net.cyclestreets.views.CycleMapView;
import net.cyclestreets.views.overlay.RouteOverlay;
import net.cyclestreets.views.overlay.StopActivityOverlay;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager.LayoutParams;
import android.widget.RelativeLayout;

public class LiveRideActivity extends Activity implements ServiceConnection
{
  private CycleMapView map_; 
  private RouteOverlay path_;
  private LiveRideService.Binding binding_;

  @Override
  public void onCreate(final Bundle saved)
  {
    super.onCreate(saved);
    
    map_ = new CycleMapView(this, this.getClass().getName());
    path_ = new RouteOverlay(this);
    map_.overlayPushBottom(path_);
    map_.overlayPushTop(new StopActivityOverlay(this));
    map_.hideLocationButton();
    
    final RelativeLayout rl = new RelativeLayout(this);
    rl.addView(map_, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    setContentView(rl);

    final Intent intent = new Intent(this, LiveRideService.class);
    bindService(intent, this, Context.BIND_AUTO_CREATE);
    
    path_.setRoute(Route.journey().segments());
  } // onCreate
     
  //////////////////////////
  @Override
  public void onPause()
  {
    map_.disableFollowLocation();
    map_.onPause();
    super.onPause();
  } // onPause
  
  @Override
  public void onResume()
  {
    super.onResume();
    map_.onResume();
    map_.enableAndFollowLocation();
  } // onResume

  @Override
  protected void onDestroy()
  {
    binding_.stopRiding();
    super.onDestroy();
  } // onStop

  ///////////////////////////
  @Override
  public void onServiceConnected(final ComponentName className, final IBinder binder)
  {
    binding_ = (LiveRideService.Binding)binder;
    binding_.startRiding();
  } // onServiceConnected

  @Override
  public void onServiceDisconnected(final ComponentName className)
  {
  } // onServiceDisconnected

} // class LiveRideActivity