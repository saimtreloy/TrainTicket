package saim.com.trainticket.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import saim.com.trainticket.R;
import saim.com.trainticket.Utils.SharedPrefDatabase;

public class MainActivity extends AppCompatActivity {

    public static Toolbar toolbar;
    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    android.support.v7.app.ActionBarDrawerToggle actionBarDrawerToggle;

    Button btnFareQuery, btnPurchesTicket, btnProfile, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppThemeMain);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait data loading...");
        progressDialog.setCanceledOnTouchOutside(false);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        actionBarDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        ImageView profile_image = (ImageView) headerView.findViewById(R.id.profile_image);
        TextView txtProfileName = (TextView) headerView.findViewById(R.id.txtProfileName);

        Picasso.with(getApplicationContext()).
                load(new SharedPrefDatabase(getApplicationContext()).RetriveIMAGE()).
                placeholder(R.drawable.ic_train).
                error(R.drawable.ic_train).
                into(profile_image);

        txtProfileName.setText(new SharedPrefDatabase(getApplicationContext()).RetriveNAME());

        btnFareQuery = (Button) findViewById(R.id.btnFareQuery);
        btnPurchesTicket = (Button) findViewById(R.id.btnPurchesTicket);
        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnHistory = (Button) findViewById(R.id.btnHistory);

        btnFareQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FareQuery.class));
            }
        });

        NavigationItemClicked();
    }

    public void NavigationItemClicked() {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.btbMenuHome) {
                    drawerLayout.closeDrawers();
                } else if (item.getItemId() == R.id.btbMenuProfile) {
                    drawerLayout.closeDrawers();
                    //startActivity(new Intent(getApplicationContext(), Profile.class));
                } else if (item.getItemId() == R.id.btbMenuSavedContent) {
                    drawerLayout.closeDrawers();
                    //startActivity(new Intent(getApplicationContext(), SaveContent.class));
                } else if (item.getItemId() == R.id.btbMenuCategory) {
                    drawerLayout.closeDrawers();
                    //startActivity(new Intent(getApplicationContext(), AllCategory.class));
                } else if (item.getItemId() == R.id.btbMenuVideo) {
                    drawerLayout.closeDrawers();
                    //Intent intent = new Intent(getApplicationContext(), AllContentAudioVideo.class);
                    //intent.putExtra("CONTENT_TYPE", "Video");
                    //startActivity(intent);
                } else if (item.getItemId() == R.id.btbMenuAudio) {
                    drawerLayout.closeDrawers();
                    //Intent intent = new Intent(getApplicationContext(), AllContentAudioVideo.class);
                    //intent.putExtra("CONTENT_TYPE", "Audio");
                    //startActivity(intent);
                }else if (item.getItemId() == R.id.btbMenuExit) {
                    drawerLayout.closeDrawers();
                    AlertExit();
                }
                return false;
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                AlertExit();
                break;

            case R.id.btbMenuSearch:
                //startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        AlertExit();
    }


    public void AlertExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    public boolean isInternetConnected(){
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}
