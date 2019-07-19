package com.callme.platform.api.request;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;

import java.util.HashSet;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class RequestFragment extends Fragment implements LifeCycle {
    private HashSet<RequestLifecycle> mChildRequestFragments;

    public RequestFragment() {
        mChildRequestFragments = new HashSet<RequestLifecycle>();
    }

    @Override
    public void addRequestLifecycle(RequestLifecycle lifecycle) {
        if (lifecycle != null) {
            mChildRequestFragments.add(lifecycle);
        }
    }

    @Override
    public void removeRequestLifecycle(RequestLifecycle lifecycle) {
        if (lifecycle != null) {
            mChildRequestFragments.remove(lifecycle);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mChildRequestFragments != null) {
            for (RequestLifecycle rlc : mChildRequestFragments) {
                rlc.onStop();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mChildRequestFragments != null) {
            for (RequestLifecycle rlc : mChildRequestFragments) {
                rlc.onDestroy();
            }
        }

        super.onDestroy();
    }

}
