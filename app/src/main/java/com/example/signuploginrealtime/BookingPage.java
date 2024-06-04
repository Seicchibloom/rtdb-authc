package com.example.signuploginrealtime;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingPage extends Fragment {

    private Spinner spinnerChoices;
    private TextView description;
    private TextView price;
    private TextView placename;
    private EditText userEmail;
    private Button buttonSave;

    private String[] places = {"Place 1","Place 2","Place 3","Place 4","Place 5"};
    private String[] choices = {"Choice 1", "Choice 2", "Choice 3", "Choice 4", "Choice 5"};
    private String[] descriptions = {"Description for Choice 1", "Description for Choice 2", "Description for Choice 3", "Description for Choice 4", "Description for Choice 5"};
    private String[] prices = {"$10", "$20", "$30", "$40", "$50"};

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_page, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("users");

        placename = view.findViewById(R.id.placename);
        spinnerChoices = view.findViewById(R.id.spinner_choices);
        description = view.findViewById(R.id.description);
        price = view.findViewById(R.id.price);
        userEmail = view.findViewById(R.id.userEmail);
        buttonSave = view.findViewById(R.id.button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, choices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChoices.setAdapter(adapter);

        spinnerChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                placename.setText(places[position]);
                description.setText(descriptions[position]);
                price.setText(prices[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmailText = userEmail.getText().toString().trim();
                if (!userEmailText.isEmpty()) {
                    saveToFirebase(userEmailText, placename.getText().toString(), description.getText().toString(), price.getText().toString());
                } else {
                    Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private String encodeEmail(String email) {
        // Replace '.' with ',' to create a valid key
        return email.replace(".", ",");
    }

    private void saveToFirebase(String userEmail, String place, String description, String price) {
        String encodedEmail = encodeEmail(userEmail);
        DatabaseReference bookingsReference = firebaseDatabase.getReference("bookings").child(encodedEmail).push();
        String bookingId = bookingsReference.getKey();
        Booking booking = new Booking(place, description, price);
        bookingsReference.setValue(booking)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Booking saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to save booking", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class Booking {
        public String place;
        public String description;
        public String price;

        public Booking() {
            // Default constructor required for calls to DataSnapshot.getValue(Booking.class)
        }

        public Booking(String place, String description, String price) {
            this.place = place;
            this.description = description;
            this.price = price;
        }
    }
}
