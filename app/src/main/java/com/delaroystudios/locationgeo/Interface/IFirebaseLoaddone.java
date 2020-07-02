package com.delaroystudios.locationgeo.Interface;



import android.location.Location;

import com.delaroystudios.locationgeo.Routing;

import java.util.List;

public interface IFirebaseLoaddone {

    void onFirebaseLoadSuccess(List<Routing> routingList);
    void onFirebaseLoadFailed(String message);

}
