package kr.co.switchnow.switch_now_client.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import kr.co.switchnow.switch_now_client.R;


/**
 * Created by ceo on 2017-07-04.
 */

public class FindMidpointFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.find_midpoint_fragment, container, false);
        MapView mapFragment = (MapView) view.findViewById(R.id.map);
        mapFragment.onCreate(saveInstanceState);
        mapFragment.onResume();
        mapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
