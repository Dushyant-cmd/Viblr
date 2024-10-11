package com.veblr.android.veblrapp.ui.tags;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;

import com.tokenautocomplete.TokenCompleteTextView;
import com.veblr.android.veblrapp.R;

public class TagsCompletionView extends TokenCompleteTextView<String> {

    InputConnection testAccessibleInputConnection;
    String personToIgnore;

    public TagsCompletionView(Context context) {
        super(context);
    }

    public TagsCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsCompletionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View getViewForObject(String person) {
        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TokenTextView token = (TokenTextView) l.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
        token.setText(person);
        return token;
    }

    @Override
    protected String defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
      /*  int index = completionText.indexOf('@');
        if (index == -1) {
            return completionText, completionText.replace(" ", ""));
        } else {
            return new String(completionText.substring(0, index), completionText);
        }*/
        return completionText.replace(" ", "");
    }

    //Methods for testing
    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        testAccessibleInputConnection = super.onCreateInputConnection(outAttrs);
        return testAccessibleInputConnection;
    }

    void setPersonToIgnore(String person) {
        this.personToIgnore = person;
    }

    @Override
    public boolean shouldIgnoreToken(String token) {
        return personToIgnore != null && personToIgnore.equals(token);
    }

    public void simulateSelectingPersonFromList(String person) {
        convertSelectionToString(person);
        replaceText(currentCompletionText());
    }
}

