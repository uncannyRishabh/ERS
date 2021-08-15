package com.example.examregistration;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class CandidateList extends ArrayAdapter<UserHelperClass> {
    private Activity context;
    private List<UserHelperClass> canList;

    public CandidateList(Activity context, List<UserHelperClass> canList){
        super(context,R.layout.list_layout,canList);
        this.context = context;
        this.canList = canList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout,null,true);

        TextView name = listViewItem.findViewById(R.id.list_name);
        TextView candidateName = listViewItem.findViewById(R.id.list_can_name);
        TextView candidateMail = listViewItem.findViewById(R.id.list_can_mail);
        TextView uid = listViewItem.findViewById(R.id.uid);
        ImageView image = listViewItem.findViewById(R.id.list_image);

        UserHelperClass uhc = canList.get(position);

        name.setText(uhc.getUid());
        candidateName.setText(uhc.getfileName());
        candidateMail.setText(uhc.getMail());
        uid.setText(uhc.getUid());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error);
        Glide.with(listViewItem).load(uhc.getImageUrl()).apply(options).into(image);

        return listViewItem;
    }
}
