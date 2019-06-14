package com.example.idanp.project.supportClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.idanp.project.R;
import com.example.idanp.project.pages.HomePage;
import com.example.idanp.project.pages.settings.Settings;
import com.example.idanp.project.pages.subject.SubjectTemplate;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPref;
    private String[] subjectNames;
    private ActionBarDrawerToggle toggle;

    protected void onCreateDrawer(){
        //Init subjectNames
        sharedPref = getSharedPreferences("storage", MODE_PRIVATE);
        subjectNames = sharedPref.getString("subjectNames","").split(",");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        for (int i = 0; i < subjectNames.length; i++){
            menu.add(0, i, menu.size(), menuIconWithText(getDrawable(R.drawable.ic_subject),subjectNames[i]));
        }

        navigationView.setNavigationItemSelectedListener(this);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void addSubjectToMenuEnd(String subjectName){
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.add(0, menu.size() - 3, menu.size(), menuIconWithText(getDrawable(R.drawable.ic_subject),subjectName));
    }

    private CharSequence menuIconWithText(Drawable r, String title) {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_settings){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, SubjectTemplate.class);
            intent.putExtra("subjectName", subjectNames[id]);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // This is required to make the drawer toggle work
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}
