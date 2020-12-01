package com.tabutech.recordmp4.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tabutech.recordmp4.R;
import com.tabutech.recordmp4.adapters.FileViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileViewerFragment#} factory method to
 * create an instance of this fragment.
 */
public class FileViewerFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "FileViewerFragment";

    private int position;

    FileViewAdapter viewAdapter;

    public FileViewerFragment(){
        //required empty Constructor//
    }

    public FileViewerFragment newInstance(int position) {
        FileViewerFragment f = new FileViewerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION,position);
        f.setArguments(b);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        observer.startWatching();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_file_viewer, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.file_view_recycle);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llmanager = new LinearLayoutManager(getActivity());
        llmanager.setOrientation(LinearLayoutManager.VERTICAL);

        //display from newest to oldest because database
        //stores them from oldest to newest

        llmanager.setReverseLayout(true);
        llmanager.setStackFromEnd(true);

        //set layout in RecycleView
        recyclerView.setLayoutManager(llmanager);
        //set animation
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        viewAdapter = new FileViewAdapter(getContext(),llmanager);
        //TODO this is an adapter fix it
        recyclerView.setAdapter(viewAdapter);

        return v;
    }

    FileObserver observer = new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()+"/Record MP3/") {
        //set up file observer to watch this directory on sd card
        @Override
        public void onEvent(int event, @Nullable String path) {
            if (event == FileObserver.DELETE){
                //user deletes file out of the app

                String file = android.os.Environment.getExternalStorageDirectory().toString()+"/Record MP3"+path+"]";

                Log.e(LOG_TAG,"File deleted "+
                        android.os.Environment.getExternalStorageDirectory().toString()+"/Record Mp3"+path+"");

                //remove file out of the database and recycle view
                //TODO Missing some thing here to delete the file
                viewAdapter.removeFromOutOfApp(file);


            }
        }
    };
}