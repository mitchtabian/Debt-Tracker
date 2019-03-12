package com.codingwithmitch.debttracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.codingwithmitch.debttracker.R;
import com.codingwithmitch.debttracker.models.Person;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseContactRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnContactClick onContactClick;
    private RequestManager requestManager;
    private List<Person> people;

    public ChooseContactRecyclerAdapter(
            OnContactClick onContactClick,
            RequestManager requestManager,
            List<Person> people) {
        this.onContactClick = onContactClick;
        this.requestManager = requestManager;
        this.people = people;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_list_item, parent, false);
        return new ContactViewHolder(view, onContactClick, requestManager);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ContactViewHolder)holder).onBind(people.get(position));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener
    {

        ImageView image;
        TextView text;
        OnContactClick onContactClick;
        RequestManager requestManager;

        public ContactViewHolder(@NonNull View itemView,
                                 OnContactClick contactClick,
                                 RequestManager requestManager) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            this.onContactClick = contactClick;
            this.requestManager = requestManager;
            itemView.setOnClickListener(this);
        }


        public void onBind(Person person){
            text.setText(person.getName());
            requestManager
                    .load(person.getPhoto_uri())
                    .into(image);
        }

        @Override
        public void onClick(View v) {
            onContactClick.onContactSelected(getAdapterPosition());
        }
    }

    public interface OnContactClick{
        void onContactSelected(int position);
    }

}

















