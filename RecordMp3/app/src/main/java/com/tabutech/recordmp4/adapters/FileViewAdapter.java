package com.tabutech.recordmp4.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tabutech.recordmp4.DatabaseListener.OnDatabaseChangedListener;
import com.tabutech.recordmp4.Others.DBHelper;
import com.tabutech.recordmp4.Others.RecordingFile;
import com.tabutech.recordmp4.R;
import com.tabutech.recordmp4.fragments.PlayBackFragment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FileViewAdapter extends RecyclerView.Adapter<FileViewAdapter.ViewHolder> implements OnDatabaseChangedListener {

    public static final String LOG_TAG = "FileView Adapter";

     private Context mContext;
     private RecordingFile file;
     private LinearLayoutManager layoutManager;

     private DBHelper dbHelper;
     public FileViewAdapter(Context context,LinearLayoutManager linearLayoutManager){
         super();
         mContext = context;
         dbHelper = new DBHelper(mContext);
         layoutManager = linearLayoutManager;
     }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
        mContext = parent.getContext();
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

         //get item to display

        file = getItem(position);

        long duration = file.getmLength();

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(minutes);

        holder.mTextName.setText(file.getmName());
        holder.mTextLength.setText(String.format("%02d:%02d",minutes,seconds));

        holder.mTextDateAdded.setText(
                DateUtils.formatDateTime(mContext,file.getmTime(),
                        DateUtils.FORMAT_SHOW_DATE|DateUtils.FORMAT_NUMERIC_DATE|DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_SHOW_YEAR)
        );

        //set on click listener on the cardView to open Playback fragment

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    PlayBackFragment fragment = new PlayBackFragment().newInstance(getItem(holder.getPosition()));

                    FragmentTransaction transaction = ((FragmentActivity)mContext)
                            .getSupportFragmentManager().beginTransaction();


                    fragment.show(transaction,"dialog PlayBack");

                }catch (Exception e){
                    Log.e(LOG_TAG,"Exception");
                }

            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ArrayList<String> arrayList = new ArrayList<>();

                arrayList.add(mContext.getResources().getString(R.string.dialog_file_share));
                arrayList.add(mContext.getResources().getString(R.string.dialog_file_rename));
                arrayList.add(mContext.getResources().getString(R.string.dialog_file_delete));

                final CharSequence[] items = arrayList.toArray(new CharSequence[arrayList.size()]);


                //delete procedure
                //share procedure
                //rename procedure

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(mContext.getResources().getString(R.string.dialog_title_options));

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            shareDialog(holder.getPosition());
                        }else if (which == 1){
                            renameDialog(holder.getPosition());
                        }else {
                            deleteDialog(holder.getPosition());
                        }
                    }
                });

                builder.setCancelable(true);

                builder.setNegativeButton(mContext.getResources().getString(R.string.dialog_action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();


                return false;
            }
        });

    }

    private void deleteDialog(final int position) {

         //file delete confirmation

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(mContext.getResources().getString(R.string.dialog_title_delete));
        builder.setMessage(mContext.getResources().getString(R.string.dialog_text_delete));
        builder.setCancelable(true);

        builder.setPositiveButton(mContext.getResources().getString(R.string.dialog_action_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            removeAt(position);
                        }catch (Exception e){
                            Log.e(LOG_TAG,"Exception",e);
                        }

                    }
        });

        builder.setNegativeButton(mContext.getResources().getString(R.string.dialog_action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void renameDialog(final int position) {


         //rename file

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_rename,null);

        final EditText mName = view.findViewById(R.id.rename_text);

        builder.setTitle(mContext.getResources().getString(R.string.dialog_title_rename));
        builder.setCancelable(true);

        builder.setPositiveButton(mContext.getResources().getString(R.string.dialog_action_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String name = mName.getText().toString().trim();
                    if (name == "") {
                        Toast.makeText(mContext, "Enter valid name!!", Toast.LENGTH_SHORT).show();
                    } else {
                        name = name + ".mp3";
                        renameFile(name, position);
                    }
                }catch (Exception e){
                    Log.e(LOG_TAG,"Exception",e);
                }


            }
        });

        builder.setNegativeButton(mContext.getResources().getString(R.string.dialog_action_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(view);

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void shareDialog(int position) {
         //share audio file
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getItem(position).getmFilePath())));
        shareIntent.setType("audio/mp3");
        mContext.startActivity(Intent.createChooser(shareIntent,mContext.getResources().getString(R.string.dialog_title_share)));
    }
    
    

    @Override
    public int getItemCount() {
        return dbHelper.getCount();
    }

    public RecordingFile getItem(int position){
         return dbHelper.getFileAt(position);
    }

    @Override
    public void onDatabaseEntryAdded() {

        /* item added to top of the list */
        notifyItemInserted(getItemCount()-1);
        layoutManager.scrollToPosition(getItemCount()-1);

    }

    @Override
    public void OnDataBaseEntryRenamed() {


    }

    public void removeAt(int position){
         //remove item from database, RecycleView and Storage
         File files = new File(getItem(position).getmFilePath());
         files.delete();

        Toast.makeText(mContext,String.format(mContext.getResources()
                        .getString(R.string.toast_file_delete),
                getItem(position).getmName()),Toast.LENGTH_SHORT).show();

        dbHelper.removeItemWithId(getItem(position).getmId());
        notifyItemRemoved(position);
    }

    public void removeFromOutOfApp(String file ){
         //this is used to delete file from other apps
    }

    private void renameFile(String name, int position){
         String mFileName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

         mFileName += "/RecordMp3/"+name;

         File f = new  File(mFileName);

         //here were checking if the file name already exists
         if (f.exists() && f.isDirectory()){
             Toast.makeText(mContext, String.format(mContext.getResources().getString(R.string.toast_file_exists),name), Toast.LENGTH_SHORT).show();
         }else {

             //if the name does not exists

             File oldPath = new File(getItem(position).getmFilePath());
             oldPath.renameTo(f);

             dbHelper.renameRecordings(getItem(position),name, mFileName);
             notifyItemChanged(position);
         }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTextName;
        private TextView mTextLength;
        private TextView mTextDateAdded;
        private View cardView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        mTextName = (TextView)itemView.findViewById(R.id.file_name);
        mTextLength = (TextView)itemView.findViewById(R.id.file_length);
        mTextDateAdded = (TextView)itemView.findViewById(R.id.date_file_added);
        cardView = itemView.findViewById(R.id.card_view);
    }
}
}
