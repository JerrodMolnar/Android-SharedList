package com.slim74562.sharedlist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ListView lst_List;
    private EditText et_Input;
    private Button btn_Add;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private int itemsInList = 0;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_Input = findViewById(R.id.et_Input);
        lst_List = findViewById(R.id.lst_List);
        btn_Add = findViewById(R.id.btnAddList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Shopping");

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getValue();

        lst_List.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String text = (String) lst_List.getItemAtPosition(position);

            }
        });

        btn_Add.setOnClickListener(v ->
        {
            String sValue = et_Input.getText().toString().trim();
            Log.d("BtnAdd", sValue);
            if (!sValue.equals(""))
            {
                for (String value: arrayList)
                {
                    Log.d("BtnAdd", "For Loop " + value);
                    if (sValue == value)
                    {
                        Toast.makeText(getApplicationContext(), sValue + " is in the list.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        itemsInList++;
                        databaseReference.push().setValue(sValue);
                        et_Input.setText("");
                        Log.d("BtnAdd", "OnClickListener else");
                    }
                }
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_Input.getWindowToken(), 0);
        });
    }

    private void getValue()
    {
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                arrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    String sValue = dataSnapshot.getValue(String.class);
                    arrayList.add(sValue);
                    itemsInList++;
                }
                lst_List.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error)
            {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}