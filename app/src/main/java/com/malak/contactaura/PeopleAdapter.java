package com.malak.contactaura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.AuraHolder> {

    private List<PhonePerson> people;

    public PeopleAdapter(List<PhonePerson> people) {
        this.people = people;
    }

    @NonNull
    @Override
    public AuraHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aura_contact, parent, false);

        return new AuraHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull AuraHolder holder, int position) {
        PhonePerson person = people.get(position);

        holder.nameView.setText(person.getDisplayName());
        holder.phoneView.setText(person.getPhoneNumber());

        String name = person.getDisplayName();
        if (name != null && !name.trim().isEmpty()) {
            holder.avatarView.setText(name.substring(0, 1).toUpperCase());
        } else {
            holder.avatarView.setText("?");
        }
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public void refreshWith(List<PhonePerson> updatedPeople) {
        this.people = updatedPeople;
        notifyDataSetChanged();
    }

    static class AuraHolder extends RecyclerView.ViewHolder {

        TextView avatarView;
        TextView nameView;
        TextView phoneView;

        public AuraHolder(@NonNull View itemView) {
            super(itemView);

            avatarView = itemView.findViewById(R.id.tvAvatarBubble);
            nameView = itemView.findViewById(R.id.tvPersonName);
            phoneView = itemView.findViewById(R.id.tvPersonPhone);
        }
    }
}
