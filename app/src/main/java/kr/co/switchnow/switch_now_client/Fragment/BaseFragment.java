package kr.co.switchnow.switch_now_client.Fragment;

import android.support.v4.app.Fragment;

import java.io.Serializable;

/**
 * Created by ceo on 2017-04-30.
 */

public abstract class BaseFragment extends Fragment implements Serializable{

    public abstract String getFragmentName();
    public abstract String getFragmentContentsCounter();

}
