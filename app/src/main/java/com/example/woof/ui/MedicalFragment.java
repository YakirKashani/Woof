package com.example.woof.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dogbreedslib.DogData;
import com.example.dogbreedslib.DogModel;
import com.example.woof.Adapters.CalendarAdapter;
import com.example.woof.Adapters.MedicinesViewAdapter;
import com.example.woof.Adapters.VaccinesViewAdapter;
import com.example.woof.Model.DayEvent;
import com.example.woof.Model.Dog;
import com.example.woof.Model.Medicine;
import com.example.woof.Model.Vaccine;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.Utils.DogWeightScaleView;
import com.example.woof.View.MapBottomSheet;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.databinding.FragmentMedicalBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalFragment extends Fragment{

    private FragmentMedicalBinding binding;
    private ShapeableImageView FM_SIV_previousMonth;
    private TextView FM_TV_monthYear;
    private ShapeableImageView FM_SIV_nextMonth;
    private RecyclerView GM_RV_calendarRecyclerView;
    private LocalDate selectedDate;
    private Button FM_Button_addSchedule;
    int year,month,day;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);
    DogWeightScaleView dogWeightScaleView;
    private MaterialTextView FM_MTV_StatusText;
    private ShapeableImageView FM_SIV_StatusIcon;
    private Button FM_Button_updateWeight;
    private TextInputEditText BSAS_TIET_SensorId;
    private Button FM_BTN_UpdateGpsSensorId;
    private Button FM_Button_findDog;
    private ShapeableImageView FM_SIV_foodAmount;
    private MaterialTextView FM_MTV_foodAmount;
    private ShapeableImageView FM_SIV_waterAmount;
    private MaterialTextView FM_MTV_WaterAmount;
    private TextInputEditText BSAS_TIET_NutritionSensorId;
    private Button FM_BTN_UpdateNutritionSensorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMedicalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseApp.initializeApp(getContext());
        Call<Dog> dogCall = dogApiService.findDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
        dogCall.enqueue(new Callback<Dog>() {
            @Override
            public void onResponse(Call<Dog> call, Response<Dog> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshDog(response.body());
                }
            }

            @Override
            public void onFailure(Call<Dog> call, Throwable throwable) {

            }
        });
        findViews();
        calendarSetup();
        initViews();
        FM_Button_addSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());
        FM_Button_updateWeight.setOnClickListener(v -> showUpdateWeightBottomSheet());
        setWeightScale();
        return root;
    }

    private void findViews() {
        FM_SIV_previousMonth = binding.getRoot().findViewById(R.id.FM_SIV_previousMonth);
        FM_TV_monthYear = binding.getRoot().findViewById(R.id.FM_TV_monthYear);
        FM_SIV_nextMonth = binding.getRoot().findViewById(R.id.FM_SIV_nextMonth);
        GM_RV_calendarRecyclerView = binding.getRoot().findViewById(R.id.GM_RV_calendarRecyclerView);
        FM_Button_addSchedule = binding.getRoot().findViewById(R.id.FM_Button_addSchedule);
        dogWeightScaleView = binding.getRoot().findViewById(R.id.dogWeightScaleView);
        FM_MTV_StatusText = binding.getRoot().findViewById(R.id.FM_MTV_StatusText);
        FM_SIV_StatusIcon = binding.getRoot().findViewById(R.id.FM_SIV_StatusIcon);
        FM_Button_updateWeight = binding.getRoot().findViewById(R.id.FM_Button_updateWeight);
        BSAS_TIET_SensorId = binding.getRoot().findViewById(R.id.BSAS_TIET_SensorId);
        FM_BTN_UpdateGpsSensorId = binding.getRoot().findViewById(R.id.FM_BTN_UpdateGpsSensorId);
        FM_Button_findDog = binding.getRoot().findViewById(R.id.FM_Button_findDog);
        FM_SIV_foodAmount = binding.getRoot().findViewById(R.id.FM_SIV_foodAmount);
        FM_MTV_foodAmount = binding.getRoot().findViewById(R.id.FM_MTV_foodAmount);
        FM_SIV_waterAmount = binding.getRoot().findViewById(R.id.FM_SIV_waterAmount);
        FM_MTV_WaterAmount = binding.getRoot().findViewById(R.id.FM_MTV_WaterAmount);
        BSAS_TIET_NutritionSensorId = binding.getRoot().findViewById(R.id.BSAS_TIET_NutritionSensorId);
        FM_BTN_UpdateNutritionSensorId = binding.getRoot().findViewById(R.id.FM_BTN_UpdateNutritionSensorId);
    }

    private void initViews() {
        if (CurrentDogManager.getInstance().getDog().getCollarGpsId() != null)
            BSAS_TIET_SensorId.setText(CurrentDogManager.getInstance().getDog().getCollarGpsId());

        FM_BTN_UpdateGpsSensorId.setOnClickListener(v -> {
            String sensorId = BSAS_TIET_SensorId.getText().toString();
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), sensorId);
            if (sensorId.isEmpty())
                Toast.makeText(getContext(), "Please enter a sensor id", Toast.LENGTH_SHORT).show();
            else {
                Call<Void> updateGpsCollarCall = dogApiService.updateCollar(CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName(), body);
                updateGpsCollarCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "GPS sensor id updated successfully", Toast.LENGTH_SHORT).show();
                            Call<Dog> dogCall = dogApiService.findDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
                            dogCall.enqueue(new Callback<Dog>() {
                                @Override
                                public void onResponse(Call<Dog> call, Response<Dog> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        CurrentDogManager.getInstance().setDog(response.body(),getContext());
                                    }
                                }

                                @Override
                                public void onFailure(Call<Dog> call, Throwable throwable) {

                                }
                            });
                        } else
                            Toast.makeText(getContext(), "Error updating gps sensor id", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {
                        Toast.makeText(getContext(), "Error updating gps sensor id", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        FM_Button_findDog.setOnClickListener(v -> {
            openMapBottomSheet();
        });

        if (CurrentDogManager.getInstance().getDog().getNutritionSensorsId() != null) {
            Log.e("CurrentDogManager",CurrentDogManager.getInstance().getDog().toString());
            BSAS_TIET_NutritionSensorId.setText(CurrentDogManager.getInstance().getDog().getNutritionSensorsId());
        }

        FM_BTN_UpdateNutritionSensorId.setOnClickListener(v -> {
            String sensorId = BSAS_TIET_NutritionSensorId.getText().toString();
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), sensorId);
            if (sensorId.isEmpty())
                Toast.makeText(getContext(), "Please enter a sensor id", Toast.LENGTH_SHORT).show();
            else {
                Call<Void> updateNutritionSensorCall = dogApiService.updateNutritionSensor(CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName(), body);
                updateNutritionSensorCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Nutrition sensor id updated successfully", Toast.LENGTH_SHORT).show();
                            Call<Dog> dogCall = dogApiService.findDog(CurrentDogManager.getInstance().getDog().getOwnerEmail(),CurrentDogManager.getInstance().getDog().getName());
                            dogCall.enqueue(new Callback<Dog>() {
                                @Override
                                public void onResponse(Call<Dog> call, Response<Dog> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        CurrentDogManager.getInstance().setDog(response.body(),getContext());
                                        setBowls();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Dog> call, Throwable throwable) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(getContext(), "Error updating nutrition sensor id {1}", Toast.LENGTH_SHORT).show();
                            Log.e("Medical fragment", "Error updating nutrition sensor id: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {
                        Toast.makeText(getContext(), "Error updating nutrition sensor id {2}", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        setBowls();
    }

    private void setBowls(){
        if(CurrentDogManager.getInstance().getDog().getNutritionSensorsId() != null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(CurrentDogManager.getInstance().getDog().getNutritionSensorsId());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Double foodWeight = snapshot.child("food").child("weight").getValue(Double.class);
                    Double waterWeight = snapshot.child("water").child("weight").getValue(Double.class);
                    Log.e("Nutrition", "Food weight: " + foodWeight + " Water weight: " + waterWeight);
                    FM_MTV_foodAmount.setText(String.valueOf(foodWeight) + " g");
                    FM_MTV_WaterAmount.setText(String.valueOf(waterWeight) + " g");
                    if(foodWeight != null){
                        if(foodWeight < 10)
                            FM_SIV_foodAmount.setImageResource(R.drawable.empty);
                        else if(foodWeight < 20)
                            FM_SIV_foodAmount.setImageResource(R.drawable.quarterfood);
                        else if(foodWeight < 30)
                            FM_SIV_foodAmount.setImageResource(R.drawable.halffood);
                        else
                            FM_SIV_foodAmount.setImageResource(R.drawable.fullfood);
                    }

                    if(waterWeight != null){
                        if(waterWeight < 10)
                            FM_SIV_waterAmount.setImageResource(R.drawable.empty);
                        else if(waterWeight < 20)
                            FM_SIV_waterAmount.setImageResource(R.drawable.waterquarter);
                        else if(waterWeight < 30)
                            FM_SIV_waterAmount.setImageResource(R.drawable.waterhalf);
                        else
                            FM_SIV_waterAmount.setImageResource(R.drawable.waterfull);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void openMapBottomSheet(){
        if(CurrentDogManager.getInstance().getDog().getCollarGpsId() != null) {
     //       FirebaseApp.initializeApp(getContext());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("location/" + CurrentDogManager.getInstance().getDog().getCollarGpsId());
            ref.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot child : snapshot.getChildren()){
                        double lat = Double.parseDouble(child.child("lat").getValue(String.class));
                        double lon = Double.parseDouble(child.child("lon").getValue(String.class));
                        String timestamp = child.child("timestamp").getValue(String.class);
                        MapBottomSheet mapBottomSheet = new MapBottomSheet(new LatLng(lat,lon),timestamp);
                        mapBottomSheet.show(getParentFragmentManager(), mapBottomSheet.getTag());

                        Log.e("GPS", "Lat: " + lat + " Lon: " + lon + " Timestamp: " + timestamp);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("GPS", "Firebase error: " + error.getMessage());
                }
            });
        }
    }

    private void calendarSetup(){
        FM_SIV_previousMonth.setOnClickListener(v -> previousMonthAction());
        FM_SIV_nextMonth.setOnClickListener(v -> nextMonthAction());
        selectedDate = LocalDate.now();
   //     setMonthView();
        setMonthView(new HashMap<>());
        fetchEventsForMonth(selectedDate);
    }

    private void showBottomSheetForDate(LocalDate date){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_view_schedule, null);
        bottomSheetDialog.setContentView(view);

        MaterialTextView BSVS_MTV_Date = view.findViewById(R.id.BSVS_MTV_Date);
        RecyclerView BSVS_RV_Vaccines = view.findViewById(R.id.BSVS_RV_Vaccines);
        RecyclerView BSVS_RV_Medicines = view.findViewById(R.id.BSVS_RV_Medicines);

        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        BSVS_MTV_Date.setText(formattedDate);

        BSVS_RV_Vaccines.setLayoutManager(new LinearLayoutManager(getContext()));
        BSVS_RV_Medicines.setLayoutManager(new LinearLayoutManager(getContext()));

        String day = String.valueOf(date.getDayOfMonth());
        String month = String.format("%02d", date.getMonthValue());
        String year = String.valueOf(date.getYear());

        String email = CurrentDogManager.getInstance().getDog().getOwnerEmail();
        String name = CurrentDogManager.getInstance().getDog().getName();

        VaccinesViewAdapter vaccinesViewAdapter = new VaccinesViewAdapter(getContext(), new ArrayList<>());
        MedicinesViewAdapter medicinesViewAdapter = new MedicinesViewAdapter(getContext(), new ArrayList<>());

        BSVS_RV_Vaccines.setAdapter(vaccinesViewAdapter);
        BSVS_RV_Medicines.setAdapter(medicinesViewAdapter);

        Call<List<Vaccine>> VaccineCall = dogApiService.getVaccinesByDate(email, name, day, month, year);
        VaccineCall.enqueue(new Callback<List<Vaccine>>() {
            @Override
            public void onResponse(Call<List<Vaccine>> call, Response<List<Vaccine>> response) {
                if(response.isSuccessful()){
                    List<Vaccine> vaccines = response.body();
                    if(vaccines != null){
                      //  BSVS_RV_Vaccines.setAdapter(new VaccinesViewAdapter(getContext(), vaccines));
                        vaccinesViewAdapter.setVaccines(vaccines);
                       // vaccinesViewAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    Log.e("Medical fragment", "Error getting vaccines: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Vaccine>> call, Throwable throwable) {
                Log.e("Medical fragment", "Error getting vaccines: " + throwable.getMessage());
            }
        });

        Call<List<Medicine>> MedicineCall = dogApiService.getMedicinesByDate(email, name, day, month, year);
        MedicineCall.enqueue(new Callback<List<Medicine>>() {
            @Override
            public void onResponse(Call<List<Medicine>> call, Response<List<Medicine>> response) {
                if(response.isSuccessful()) {
                    List<Medicine> medicines = response.body();
                    if (medicines != null) {
                        medicinesViewAdapter.setMedicines(medicines);
                    //    medicinesViewAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Medicine>> call, Throwable throwable) {
                Log.e("Medical fragment", "Error getting medicines: " + throwable.getMessage());
            }
        });
        bottomSheetDialog.show();
    }

    private void setMonthView(Map<LocalDate, DayEvent> eventMap){
        FM_TV_monthYear.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, new CalendarAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position, String dayText, LocalDate date) {
                showBottomSheetForDate(date);
            }
        }, eventMap, selectedDate);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),7);
        GM_RV_calendarRecyclerView.setLayoutManager(layoutManager);
        GM_RV_calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date){
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i=1;i<=42;i++){
            if(i<=dayOfWeek || i>daysInMonth+dayOfWeek)
                daysInMonthArray.add("");
            else
                daysInMonthArray.add(String.valueOf(i-dayOfWeek));
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    private void previousMonthAction(){
        selectedDate = selectedDate.minusMonths(1);
       // setMonthView();
        setMonthView(new HashMap<>());
        fetchEventsForMonth(selectedDate);
    }

    private void nextMonthAction(){
        selectedDate = selectedDate.plusMonths(1);
     //   setMonthView();
        setMonthView(new HashMap<>());
        fetchEventsForMonth(selectedDate);
    }

    private void showAddScheduleBottomSheet(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_add_schedule, null);
        bottomSheetDialog.setContentView(view);

        Spinner BSAS_Spinner_Type = view.findViewById(R.id.BSAS_Spinner_Type);
        TextInputEditText BSAS_TIET_Description = view.findViewById(R.id.BSAS_TIET_Description);
        TextInputEditText BSAS_TIET_Date = view.findViewById(R.id.BSAS_TIET_Date);
        TextInputEditText BSAS_TIET_Amount = view.findViewById(R.id.BSAS_TIET_Amount);
        Button BSAS_Button_Save = view.findViewById(R.id.BSAS_Button_Save);

        String[] types = {"Medicine", "Vaccine"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BSAS_Spinner_Type.setAdapter(adapter);

        BSAS_Spinner_Type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = types [position];
                if(selectedType.equals("Vaccine")){
                    BSAS_TIET_Amount.setVisibility(View.GONE);
                } else{
                    BSAS_TIET_Amount.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BSAS_TIET_Date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format("%02d/%02d/%d", selectedDay, (selectedMonth+1), selectedYear);
                        BSAS_TIET_Date.setText(selectedDate);
                    },year,month,day);
            datePickerDialog.show();
        });

        BSAS_Button_Save.setOnClickListener(v -> {
            String type = BSAS_Spinner_Type.getSelectedItem().toString();
            String description = BSAS_TIET_Description.getText().toString();
            String date = BSAS_TIET_Date.getText().toString();
            String amountStr= BSAS_TIET_Amount.getText() != null ? BSAS_TIET_Amount.getText().toString() : "";
            int amount = 0;
            int duration = 0;

            if(type.isEmpty() || description.isEmpty() || date.isEmpty() || (type.equals("Medicine") && amountStr.isEmpty())){
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else{
                if(type.equals("Vaccine")) {
                    Call<Void> call = dogApiService.addNewVaccine(CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName(), new Vaccine(description, date));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(getContext(), "Vaccine added successfully", Toast.LENGTH_SHORT).show();
                                fetchEventsForMonth(selectedDate);
                            } else{
                                Toast.makeText(getContext(), "Error adding vaccine", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable throwable) {
                            Toast.makeText(getContext(), "Error adding vaccine", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try {
                        amount = Integer.parseInt(amountStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.e("Medical fragment", "Type: " + type + " Description: " + description + " Date: " + date);
                    Call<Void> call = dogApiService.addNewMedicine(CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName(), new Medicine(description,amount, date));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(getContext(), "Medicine added successfully", Toast.LENGTH_SHORT).show();
                                fetchEventsForMonth(selectedDate);
                            } else{
                                Toast.makeText(getContext(), "Error adding medicine", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable throwable) {
                            Toast.makeText(getContext(), "Error adding medicine", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    private void fetchEventsForMonth(LocalDate monthDate) {
        YearMonth yearMonth = YearMonth.from(monthDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        Map<LocalDate, DayEvent> eventsMap = new HashMap<>();


        String month = String.format("%02d", monthDate.getMonthValue());
        String year = String.valueOf(monthDate.getYear());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        dogApiService.getVaccinesByMonthAndYear(CurrentDogManager.getInstance().getDog().getOwnerEmail(),
                CurrentDogManager.getInstance().getDog().getName(),
                month, year).enqueue(new Callback<List<Vaccine>>() {
            @Override
            public void onResponse(Call<List<Vaccine>> call, Response<List<Vaccine>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    for (Vaccine vaccine : response.body()) {
                        try {
                            LocalDate vaccineLocalDate = LocalDate.parse(vaccine.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            DayEvent event = eventsMap.getOrDefault(vaccineLocalDate, new DayEvent(false, false));
                            event.setHasVaccine(true);
                            eventsMap.put(vaccineLocalDate, event);
                        } catch (Exception e) {
                            Log.e("Calendar", "Error parsing date: " + vaccine.getDate());
                        }
                    }
                    setMonthView(eventsMap);
                }
            }

            @Override
            public void onFailure(Call<List<Vaccine>> call, Throwable throwable) {
                Log.e("Calendar", "Failed to load vaccines for month: " + monthYearFromDate(monthDate) + " " + throwable.getMessage());
            }
        });

        dogApiService.getMedicinesByMonthAndYear(CurrentDogManager.getInstance().getDog().getOwnerEmail(),
                CurrentDogManager.getInstance().getDog().getName(),
                month, year).enqueue(new Callback<List<Medicine>>() {
            @Override
            public void onResponse(Call<List<Medicine>> call, Response<List<Medicine>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    for (Medicine medicine : response.body()) {
                        try{
                            LocalDate medicineLocalDate = LocalDate.parse(medicine.getDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            DayEvent event = eventsMap.getOrDefault(medicineLocalDate, new DayEvent(false, false));
                            event.setHasMedicine(true);
                            eventsMap.put(medicineLocalDate, event);
                        } catch (Exception e){
                            Log.e("Calendar", "Error parsing date: " + medicine.getDate());
                        }
                    }
                    setMonthView(eventsMap);
                }
            }
            @Override
            public void onFailure(Call<List<Medicine>> call, Throwable throwable) {

            }
        });
    }

    private void setWeightScale(){
        int dogAge = calculateAge();
        if(dogAge!= -1) {
            String dogAgeStr = String.valueOf(dogAge);
            String dogBreed =  CurrentDogManager.getInstance().getDog().getBreed();
            String dogGender = CurrentDogManager.getInstance().getDog().getGender();
            Log.e("Weight scale", dogBreed + " " + dogGender + " " + dogAgeStr);
            DogData.getDogByBreedGenderAge(getContext(), dogBreed, dogGender, dogAgeStr, new DogData.Callback_Data<DogModel>() {
                @Override
                public void data(DogModel value) {
                    if(value!=null) {
                        Log.e("Weight scale", value.getAvg_weight_min() + " " + value.getAvg_weight_max());
                        if(CurrentDogManager.getInstance().getDog().getWeight() <= Float.parseFloat(value.getAvg_weight_max()))
                            dogWeightScaleView.setWeights(Float.parseFloat(value.getAvg_weight_min()),
                                    Float.parseFloat(value.getAvg_weight_max()),
                                    CurrentDogManager.getInstance().getDog().getWeight(),
                                    Float.parseFloat(value.getAvg_weight_max()) + 5f);
                        else
                            dogWeightScaleView.setWeights(Float.parseFloat(value.getAvg_weight_min()),
                                    Float.parseFloat(value.getAvg_weight_max()),
                                    CurrentDogManager.getInstance().getDog().getWeight(),
                                    CurrentDogManager.getInstance().getDog().getWeight() + 5f);

                        if(CurrentDogManager.getInstance().getDog().getWeight() <= Float.parseFloat(value.getAvg_weight_max()) && CurrentDogManager.getInstance().getDog().getWeight() >= Float.parseFloat(value.getAvg_weight_min())){
                            FM_MTV_StatusText.setText(CurrentDogManager.getInstance().getDog().getName() + " weight is ok!");
                            FM_MTV_StatusText.setTextColor(ContextCompat.getColor(requireContext(),R.color.green));
                            FM_SIV_StatusIcon.setImageResource(R.drawable.good);
                            FM_SIV_StatusIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green));
                        } else if (CurrentDogManager.getInstance().getDog().getWeight() > Float.parseFloat(value.getAvg_weight_max())) {
                            FM_MTV_StatusText.setText(CurrentDogManager.getInstance().getDog().getName() + " is overweighted!");
                            FM_MTV_StatusText.setTextColor(ContextCompat.getColor(requireContext(),R.color.green));
                            FM_SIV_StatusIcon.setImageResource(R.drawable.danger);
                            FM_SIV_StatusIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.red));
                        } else{
                            FM_MTV_StatusText.setText(CurrentDogManager.getInstance().getDog().getName() + " is underweighted!");
                            FM_MTV_StatusText.setTextColor(ContextCompat.getColor(requireContext(),R.color.green));
                            FM_SIV_StatusIcon.setImageResource(R.drawable.danger);
                            FM_SIV_StatusIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.red));
                        }

                    }
                    else{
                        FM_MTV_StatusText.setText("Unable to retrieve breed data");
                        FM_MTV_StatusText.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
                        FM_SIV_StatusIcon.setImageResource(R.drawable.exclamation);
                        FM_SIV_StatusIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.red));
                    }
                }
            });
        }
        else{
            FM_MTV_StatusText.setText("Unable to retrieve dog's age");
            FM_MTV_StatusText.setTextColor(ContextCompat.getColor(requireContext(),R.color.red));
            FM_SIV_StatusIcon.setImageResource(R.drawable.exclamation);
            FM_SIV_StatusIcon.setColorFilter(ContextCompat.getColor(requireContext(),R.color.red));
        }
    }

    private int calculateAge(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate dob = LocalDate.parse(CurrentDogManager.getInstance().getDog().getDob(), formatter);
        LocalDate today = LocalDate.now();
        if(dob!=null && today!=null) {
            Period period = Period.between(dob, today);
            return period.getYears();
        }
        else
            return -1;
    }

    private void showUpdateWeightBottomSheet(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.update_weight_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        ShapeableImageView UWBS_SIV_DogPic = view.findViewById(R.id.UWBS_SIV_DogPic);
        MaterialTextView UWBS_MTV_DogName = view.findViewById(R.id.UWBS_MTV_DogName);
        MaterialTextView UWBS_MTV_CurrentWeight = view.findViewById(R.id.UWBS_MTV_CurrentWeight);
        TextInputEditText UWBS_TIET_NewWeight = view.findViewById(R.id.UWBS_TIET_NewWeight);
        Button UWBS_Button_Update = view.findViewById(R.id.UWBS_Button_Update);

        if(CurrentDogManager.getInstance().getDog().getPhotoURL() == null)
            UWBS_SIV_DogPic.setImageResource(R.drawable.default_dog_picture);
        else
            Glide.with(this).load(CurrentDogManager.getInstance().getDog().getPhotoURL()).error(R.drawable.default_dog_picture).into(UWBS_SIV_DogPic);

        UWBS_MTV_DogName.setText(CurrentDogManager.getInstance().getDog().getName());
        UWBS_MTV_CurrentWeight.setText(String.valueOf(CurrentDogManager.getInstance().getDog().getWeight()) + " Kg");

        UWBS_Button_Update.setOnClickListener(v -> {
            String newWeightStr = UWBS_TIET_NewWeight.getText().toString();
            if(newWeightStr.isEmpty()){
                Toast.makeText(getContext(), "Please enter a new weight", Toast.LENGTH_SHORT).show();
            } else{
                float newWeight = Float.parseFloat(newWeightStr);
                Call<Void> call = dogApiService.updateWeight(CurrentDogManager.getInstance().getDog().getOwnerEmail(), CurrentDogManager.getInstance().getDog().getName(), newWeight);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getContext(), "Weight updated successfully", Toast.LENGTH_SHORT).show();
                            CurrentDogManager.getInstance().getDog().setWeight(newWeight);
                            setWeightScale();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {

                    }
                });
                bottomSheetDialog.dismiss();
            }

        });

        bottomSheetDialog.show();
    }

private void refreshDog(Dog dog){
        CurrentDogManager.getInstance().setDog(dog,getContext());
}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}