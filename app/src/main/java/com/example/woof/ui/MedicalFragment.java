package com.example.woof.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.woof.Adapters.CalendarAdapter;
import com.example.woof.Model.DayEvent;
import com.example.woof.Model.Medicine;
import com.example.woof.Model.Vaccine;
import com.example.woof.R;
import com.example.woof.Singleton.CurrentDogManager;
import com.example.woof.WoofBackend.ApiController;
import com.example.woof.WoofBackend.DogApi;
import com.example.woof.databinding.FragmentMedicalBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalFragment extends Fragment {

    private FragmentMedicalBinding binding;
    private ShapeableImageView FM_SIV_previousMonth;
    private TextView FM_TV_monthYear;
    private ShapeableImageView FM_SIV_nextMonth;
    private RecyclerView GM_RV_calendarRecyclerView;
    private LocalDate selectedDate;
    private Button FM_Button_addSchedule;
    int year,month,day;
    DogApi dogApiService = ApiController.getRetrofitInstance().create(DogApi.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMedicalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        findViews();
        calendarSetup();
        FM_Button_addSchedule.setOnClickListener(v -> showAddScheduleBottomSheet());
        return root;
    }

    private void findViews() {
        FM_SIV_previousMonth = binding.getRoot().findViewById(R.id.FM_SIV_previousMonth);
        FM_TV_monthYear = binding.getRoot().findViewById(R.id.FM_TV_monthYear);
        FM_SIV_nextMonth = binding.getRoot().findViewById(R.id.FM_SIV_nextMonth);
        GM_RV_calendarRecyclerView = binding.getRoot().findViewById(R.id.GM_RV_calendarRecyclerView);
        FM_Button_addSchedule = binding.getRoot().findViewById(R.id.FM_Button_addSchedule);
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
        Button BSAS_Button_Save = view.findViewById(R.id.BSAS_Button_Save);

        String[] types = {"Medicine", "Vaccine"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BSAS_Spinner_Type.setAdapter(adapter);

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
            int amount = 0;
            int duration = 0;

            if(type.isEmpty() || description.isEmpty() || date.isEmpty()){
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else{
                Log.e("Medical fragment", "Type: " + type + " Description: " + description + " Date: " + date);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}