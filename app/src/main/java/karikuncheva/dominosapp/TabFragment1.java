package karikuncheva.dominosapp;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import karikuncheva.dominosapp.R;

/**
 * Created by Mariela Zviskova on 10.3.2017 г..
 */
    public class TabFragment1 extends Fragment {

     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }
}

