package ru.oboturov_corp.dogenotes2;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NoteModifyFragment extends Fragment {

    private String mOpenNote;
    private DbHelper mDbHelper;

    public static NoteModifyFragment newInstance(String noteUuid) {
        NoteModifyFragment noteModifyFragment = new NoteModifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DbHelper.ARG_OPEN_NOTE, noteUuid);
        noteModifyFragment.setArguments(bundle);
        return noteModifyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOpenNote = getArguments().getString(DbHelper.ARG_OPEN_NOTE);
        mDbHelper = new DbHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note_modify, container, false);

        final EditText name = (EditText) v.findViewById(R.id.name_editing);
        final EditText text = (EditText) v.findViewById(R.id.text_editing);

        name.setText(mDbHelper.getDateForId(mOpenNote, DbHelper.KEY_NOTE_NAME));
        text.setText(mDbHelper.getDateForId(mOpenNote, DbHelper.KEY_NOTE_TEXT));

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) mDbHelper.saveNoteToDb(mOpenNote,
                        name.getText().toString(),
                        text.getText().toString());
            }
        };

        name.setOnFocusChangeListener(listener);
        text.setOnFocusChangeListener(listener);

        return v;
    }
}
