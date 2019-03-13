package com.codingwithmitch.debttracker.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.debttracker.R;
import com.codingwithmitch.debttracker.adapters.ChooseContactRecyclerAdapter;
import com.codingwithmitch.debttracker.models.Person;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseContactDialog extends DialogFragment implements ChooseContactRecyclerAdapter.OnContactClick
{

    private static final String TAG = "ChooseContactDialog";

    private static ChooseContactDialog instance;

    // ui


    private List<Person> people;
    private OnRetrievedPerson onRetrievedPerson;


    public static ChooseContactDialog getInstance(ArrayList<Person> people){
        if(instance == null){
            instance = new ChooseContactDialog();
        }
        if(people != null){
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("people", people);
            instance.setArguments(bundle);
        }

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            people = (getArguments().getParcelableArrayList("people"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called.");
        return inflater.inflate(R.layout.dialog_choose_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: called.");
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    private void init(View view){
        // vars
        RecyclerView recyclerView = view.findViewById(R.id.contacts_list);
        ChooseContactRecyclerAdapter mAdapter = new ChooseContactRecyclerAdapter(this, initGlide(), people);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
    }

    public RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar);
        options.circleCrop();
        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    @Override
    public void onContactSelected(int position) {
        Log.d(TAG, "onContactSelected: clicked.");
        onRetrievedPerson.onPersonClicked(people.get(position));
        getDialog().dismiss();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        onRetrievedPerson = (OnRetrievedPerson) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRetrievedPerson = null;
    }
}
















