package ru.oboturov_corp.dogenotes2;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateNewItemFragment extends DialogFragment {

    DbHelper mDbHelper;
    String mType;
    String mOpenFolder;

    public static CreateNewItemFragment newInstance(String type, String openFolder) {
        CreateNewItemFragment createNewItemFragment = new CreateNewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DbHelper.ARG_ITEM_TYPE, type);
        bundle.putString(DbHelper.ARG_OPEN_FOLDER, openFolder);
        createNewItemFragment.setArguments(bundle);
        return createNewItemFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DbHelper(getContext());
        mType = getArguments().getString(DbHelper.ARG_ITEM_TYPE);
        mOpenFolder = getArguments().getString(DbHelper.ARG_OPEN_FOLDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_name, container, false);

        final EditText editName = (EditText) v.findViewById(R.id.edit_name);
        Button create = (Button) v.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(editName.getText())
                        .trim()
                        .replaceAll("[\\s]{2,}", " ")
                        .replaceAll("\n", "");

                if (name.length() == 0) {
                    Toast.makeText(getContext(), R.string.empty_name, Toast.LENGTH_SHORT).show();
                } else {
                    mDbHelper.addItem(mType, mOpenFolder, name);
                    dismiss();
                }
            }
        });

        return v;
    }

}
