package ru.oboturov_corp.dogenotes2;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

public class FoldersFragment extends Fragment {

    private String mOpenFolder;
    private DogeAdapter mAdapter;

    public static FoldersFragment newInstance(String openFolder) {
        FoldersFragment foldersFragment = new FoldersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DbHelper.ARG_OPEN_FOLDER, openFolder);
        foldersFragment.setArguments(bundle);
        return foldersFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOpenFolder = getArguments().getString(DbHelper.ARG_OPEN_FOLDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_folders, container, false);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.folders_rv);

        Animation animation = AnimationUtils
                .loadAnimation(container.getContext() ,R.anim.folders_show_anim);
        rv.setLayoutAnimation(new LayoutAnimationController(animation));

        RecyclerView.LayoutManager manager = new LinearLayoutManager(container.getContext());
        rv.setLayoutManager(manager);

        mAdapter = new DogeAdapter(getContext(), DbHelper.TYPE_FOLDER, mOpenFolder);
        rv.setAdapter(mAdapter);

        return v;
    }

    public void rebuildView() {
        mAdapter.notifyDataSetChanged();
    }
}
