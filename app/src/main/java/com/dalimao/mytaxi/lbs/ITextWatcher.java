package com.dalimao.mytaxi.lbs;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @Title:ITextWatcher
 * @Package:com.dalimao.mytaxi.lbs
 * @Description:
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/2510:00
 */
public class ITextWatcher implements TextWatcher {
    TextWatcherListener listener;
    public ITextWatcher(TextWatcherListener listener){
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        this.listener.onTextWatcher(s);
    }


    public interface TextWatcherListener{
        void onTextWatcher(Editable s);
    }
}
