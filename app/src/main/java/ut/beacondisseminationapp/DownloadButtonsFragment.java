package ut.beacondisseminationapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class DownloadButtonsFragment extends Fragment {

    private static final String TAG = DownloadButtonsFragment.class.getSimpleName();

    private static final String SHOW_SUBSCRIBE = "show_subscribe";
    private static final String SEARCH_TEXT = "search_text";

    //boolean showSubscribe;
    Button mCancelButton;
    Button mDownloadButton;

    private OnDownloadButtonsListener mListener;


    // TODO: Rename and change types and number of parameters
    public static DownloadButtonsFragment newInstance(boolean showSubscribe) {
        DownloadButtonsFragment fragment = new DownloadButtonsFragment();
        Bundle args = new Bundle();
        args.putBoolean(SHOW_SUBSCRIBE, showSubscribe);
        //args.putString(SEARCH_TEXT, searchText);
        fragment.setArguments(args);
        return fragment;
    }
    public DownloadButtonsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            showSubscribe = getArguments().getBoolean(SHOW_SUBSCRIBE);
            //searchText = getArguments().getString(SEARCH_TEXT);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download_buttons, container, false);

        mCancelButton = (Button) view.findViewById(R.id.cancel_button);
        //mCancelButton.setVisibility(showSubscribe ? View.VISIBLE : View.GONE);

        mDownloadButton = (Button) view.findViewById(R.id.download_button);
        //mSearchButton = (Button) view.findViewById(R.id.search_button);
        //mTopButton = (Button) view.findViewById(R.id.top_button);
        //mSearchText = (EditText) view.findViewById(R.id.search_text);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelButtonHandler();
            }
        });

        mDownloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mListener.downloadButtonHandler();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDownloadButtonsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStreamButtonsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    public interface OnDownloadButtonsListener {
        public void cancelButtonHandler();
        public void downloadButtonHandler();
    }

}
