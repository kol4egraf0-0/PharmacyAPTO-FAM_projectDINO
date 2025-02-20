package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import com.example.aptofam.Model.ApteksModel;
import com.example.aptofam.R;
import com.example.aptofam.databinding.ActivityMapApteksBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MapApteksActivity extends BaseActivity implements OnMapReadyCallback {
    private ActivityMapApteksBinding binding;
    private GoogleMap map;
    private DatabaseReference databaseReference;
    private List<ApteksModel> apteksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapApteksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apteksList = new ArrayList<>();
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        loadApteksFromFirebase();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadApteksFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Apteks");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                apteksList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ApteksModel aptek = dataSnapshot.getValue(ApteksModel.class);
                    if (aptek != null) {
                        apteksList.add(aptek);
                    }
                }
                displayMarkersOnMap();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void displayMarkersOnMap() {
        if (map != null) {
            for (ApteksModel apteka : apteksList) {
                LatLng location = new LatLng(apteka.getLatitude(), apteka.getLongitude());
                map.addMarker(new MarkerOptions().position(location).title(apteka.getAptekaName()));
            }
            if (!apteksList.isEmpty()) {
                LatLng firstLocation = new LatLng(apteksList.get(0).getLatitude(), apteksList.get(0).getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        displayMarkersOnMap();
        map.setOnMarkerClickListener(marker -> {
            for (ApteksModel apteka : apteksList) {
                if (marker.getTitle().equals(apteka.getAptekaName())) {
                    Intent intent = new Intent(MapApteksActivity.this, ProfileEditActivity.class);
                    intent.putExtra("selected_apteka_id", apteka.getAptekaId());
                    startActivity(intent);
                    break;
                }
            }
            return true; // Возвращаем true, чтобы событие было обработано
        });
    }
}