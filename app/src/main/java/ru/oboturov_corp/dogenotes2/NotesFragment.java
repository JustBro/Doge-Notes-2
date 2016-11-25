package ru.oboturov_corp.dogenotes2;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

public class NotesFragment extends Fragment {

    private String mOpenFolder;
    private DogeAdapter adapter;

    public static NotesFragment newInstance(String openFolder) {
        NotesFragment notesFragment = new NotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DbHelper.ARG_OPEN_FOLDER, openFolder);
        notesFragment.setArguments(bundle);
        return notesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOpenFolder = getArguments().getString(DbHelper.ARG_OPEN_FOLDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.notes_rv);

        Animation animation = AnimationUtils
                .loadAnimation(container.getContext() ,R.anim.notes_show_anim);
        rv.setLayoutAnimation(new LayoutAnimationController(animation));

        RecyclerView.LayoutManager manager = new GridLayoutManager(container.getContext(), 3);
        rv.setLayoutManager(manager);

        adapter = new DogeAdapter(getContext(), DbHelper.TYPE_NOTE, mOpenFolder);
        rv.setAdapter(adapter);
        return v;
    }

    public void rebuildView() {
        adapter.notifyDataSetChanged();
    }
}
