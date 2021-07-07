package com.firstapp.firebasechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firstapp.firebasechat.Fragments.ChatsFragment;
import com.firstapp.firebasechat.Fragments.ClassRoomsFragment;
import com.firstapp.firebasechat.Fragments.ProfileFragment;
import com.firstapp.firebasechat.Fragments.TimelineFragment;
import com.firstapp.firebasechat.Fragments.UserFragment;
import com.firstapp.firebasechat.Model.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

//New branch
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference myRef;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("I am in main");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser
                .getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                //Toast.makeText(MainActivity.this, "Welcome Back! "+ users.getUsername(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        //Tab Layout and viewpager instantiate and declaring
        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Adds the fragment to main activity. Displayed from Left to Right
        viewPagerAdapter.addFragment(new ClassRoomsFragment(), "My Chat Rooms");
        //viewPagerAdapter.addFragment(new ProfileFragment(), "My Profile");
        viewPagerAdapter.addFragment(new UserFragment(), "All Users");
        viewPagerAdapter.addFragment(new ChatsFragment(),"Recent Chats");
        viewPagerAdapter.addFragment(new TimelineFragment(),"Posts");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login_Activity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        if(item.getItemId() == R.id.profile){
            
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    // Class ViewPager Adapter
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }


        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position){
            return titles.get(position);
        }
    }



    //Updates user if online or offline
    private void CheckStatus(String status){

        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        myRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume(){
        super.onResume();
        CheckStatus("Online");
    }

    @Override
    protected void onPause(){
        super.onPause();
        CheckStatus("Offline");
    }
}