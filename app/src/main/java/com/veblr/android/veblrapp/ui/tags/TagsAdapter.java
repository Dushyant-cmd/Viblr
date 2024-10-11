package com.veblr.android.veblrapp.ui.tags;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.tokenautocomplete.FilteredArrayAdapter;
import com.veblr.android.veblrapp.R;
import com.veblr.android.veblrapp.ui.UploadEditActivity;

public class TagsAdapter extends FilteredArrayAdapter<String> {
    @LayoutRes
    private int layoutId;

    public TagsAdapter(Context context, @LayoutRes int layoutId, String[] people) {
        super(context, layoutId, people);
        this.layoutId = layoutId;
    }



    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {

            LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = l.inflate(layoutId, parent, false);
        }

        String tag = getItem(position);
        ((TextView)convertView.findViewById(R.id.value)).setText(tag);

        return convertView;
    }

    @Override
    protected boolean keepObject(String tag, String mask) {
        mask = mask.toLowerCase();
        return tag.toLowerCase().startsWith(mask) || tag.toLowerCase().startsWith(mask);
    }
}