package ru.oboturov_corp.dogenotes2;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

class DogeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private DbHelper mDbHelper;
    private String mType;
    private String mOpenFolder;

    DogeAdapter(Context context, String type, String openFolder){
        mContext = context;
        mDbHelper = new DbHelper(context);
        mType = type;
        mOpenFolder = openFolder;
    }

    private static class FolderVh extends RecyclerView.ViewHolder {

        CardView card;
        CheckBox box;
        TextView name;
        String id;

        FolderVh(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.item_folder);
            box = (CheckBox) itemView.findViewById(R.id.folder_checkbox);
            name = (TextView) itemView.findViewById(R.id.item_folder_name);
        }
    }

    private static class NoteVh extends RecyclerView.ViewHolder {

        CardView card;
        CheckBox box;
        TextView name;
        TextView text;
        String id;

        NoteVh(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.item_note);
            box = (CheckBox) itemView.findViewById(R.id.note_checkbox);
            name = (TextView) itemView.findViewById(R.id.item_note_name);
            text = (TextView) itemView.findViewById(R.id.item_note_text);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;
        if(mType.equals(DbHelper.TYPE_FOLDER)){
            v = LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
            vh = new FolderVh(v);
        }else{
            v = LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.item_note, parent, false);
            vh = new NoteVh(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MainActivity mainActivity = (MainActivity) mContext;
        if(mType.equals(DbHelper.TYPE_FOLDER)){
            final FolderVh folderVh = (FolderVh) holder;
            folderVh.name.setText(mDbHelper.getDate(
                    DbHelper.TYPE_FOLDER,
                    mOpenFolder,
                    DbHelper.KEY_FOLDER_NAME,
                    position));
            folderVh.id = mDbHelper.getDate(
                    DbHelper.TYPE_FOLDER,
                    mOpenFolder,
                    DbHelper.KEY_ID,
                    position);
            folderVh.box.setChecked(mainActivity.isSetContains(folderVh.id));
            folderVh.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.onItemClick(folderVh.id);
                }
            });
            folderVh.box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (folderVh.box.isChecked()) {
                        mainActivity.putInSet(folderVh.id);
                    } else {
                        mainActivity.removeFromSet(folderVh.id);
                    }
                }
            });
        }else{
            final NoteVh noteVh = (NoteVh) holder;
            noteVh.name.setText(mDbHelper.getDate(
                    DbHelper.TYPE_NOTE,
                    mOpenFolder,
                    DbHelper.KEY_NOTE_NAME,
                    position));
            noteVh.text.setText(mDbHelper.getDate(
                    DbHelper.TYPE_NOTE,
                    mOpenFolder,
                    DbHelper.KEY_NOTE_TEXT,
                    position));
            noteVh.id = mDbHelper.getDate(
                    DbHelper.TYPE_NOTE,
                    mOpenFolder,
                    DbHelper.KEY_ID,
                    position);
            noteVh.box.setChecked(mainActivity.isSetContains(noteVh.id));
            noteVh.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.onItemClick(noteVh.id);
                }
            });
            noteVh.box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (noteVh.box.isChecked()) {
                        mainActivity.putInSet(noteVh.id);
                    } else {
                        mainActivity.removeFromSet(noteVh.id);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mType.equals(DbHelper.KEY_FOLDER_NAME)){
            return mDbHelper.getCount(mType, mOpenFolder);
        }else{
            return mDbHelper.getCount(mType, mOpenFolder);
        }
    }
}