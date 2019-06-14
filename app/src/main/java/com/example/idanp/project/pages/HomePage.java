package com.example.idanp.project.pages;

import android.os.Bundle;

import com.example.idanp.project.R;
import com.example.idanp.project.supportClasses.BaseActivity;

public class HomePage extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        super.onCreateDrawer();
    }
}
