package com.ta.slk.sistemlayanankegiatan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dd.CircularProgressButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ta.slk.sistemlayanankegiatan.Fragments.ActivitiesFragment;
import com.ta.slk.sistemlayanankegiatan.Method.FilePath;
import com.ta.slk.sistemlayanankegiatan.Method.FileUtil;
import com.ta.slk.sistemlayanankegiatan.Model.PostData;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiClient;
import com.ta.slk.sistemlayanankegiatan.Rest.ApiInterface;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddInvitation extends AppCompatActivity {
    DatePickerDialog datePickerDialog;
    SimpleDateFormat simpleDateFormat;
    CircularProgressButton button;
    String action, idActivity;
    TextInputEditText name,contact,description,day,location,clock;
    TextInputEditText upload;
    File originalFile,fileCompressed,fileDocuments;
    Uri documentUri,selectedImage;
    Toolbar toolbar;
    String imagePath = "";
    Bundle activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_invitation);

        initComponents();
        if (activities != null) {
            updateComponents();
        }
        button.setText("SUBMIT");
        button.setIdleText("SUBMIT");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().show();

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTime();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                Intent intentChoice = Intent.createChooser(
                        intent,"Pilih Dokumen untuk di upload");
                startActivityForResult(intentChoice,1);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final CharSequence[] dialogitem = {"Tampilkan","Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddInvitation.this);
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Dialog dialog1=new Dialog(v.getContext(),R.style.ZoomImageDialog);
                                dialog1.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                dialog1.setContentView(R.layout.zoom_image);
                                ImageView imageView = dialog1.findViewById(R.id.zoom_image);
                                if (action == null) {
                                    Glide.with(v.getContext()).load(new File(imagePath)).into(imageView);
                                } else {
                                    Glide.with(v.getContext()).load(ApiClient.BASE_URL + "uploads/" + upload.getText().toString()).into(imageView);
                                }

                                dialog1.show();
                                break;
                            case 1:
                                final Intent intent = new Intent();
                                intent.setType("image/jpeg");
                                intent.setAction(Intent.ACTION_PICK);
                                Intent intentChoice = Intent.createChooser(
                                        intent,"Pilih Gambar untuk di upload");
                                startActivityForResult(intentChoice,2);
                                break;
                        }
                    }
                }).create().show();
            }
        });
    }

    private void updateComponents() {
        if (!activities.getString("action").equals("")) {
            String[] date = activities.getString("date").split(" ");
            idActivity = activities.getString("id_activity");
            action = activities.getString("action");
            upload.setText(activities.getString("picture"));
            day.setText(date[0]);
            clock.setText(date[1]);
            name.setText(activities.getString("name"));
            location.setText(activities.getString("location"));
            description.setText(activities.getString("description"));
            contact.setText(activities.getString("file"));
        }
    }

    public void showDialog(){
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year,month,dayOfMonth);
                day.setText(simpleDateFormat.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void showTime(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        final int mSecond = c.get(Calendar.SECOND);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        clock.setText(" "+hourOfDay + ":" + minute+":"+mSecond);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 1){
            if(data == null){
                Toast.makeText(getApplicationContext(), "Dokumen gagal di-load", Toast.LENGTH_LONG).show();
            }
            documentUri = data.getData();
            String path = FilePath.getPath(this,documentUri);
            fileDocuments = new File(path);
            contact.setText(path);
        }

        if (resultCode == RESULT_OK && requestCode == 2){
            if (data==null){
                Toast.makeText(getApplicationContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imagePath = cursor.getString(columnIndex);
                upload.setText(imagePath);
                try {
                    originalFile = FileUtil.from(getApplicationContext(),data.getData());
                    fileCompressed = new Compressor(getApplicationContext())
                            .setMaxHeight(480).setMaxWidth(480).setQuality(75)
                            .compressToFile(originalFile);
                    if (getContentResolver().getType(data.getData()).equals("image/png")) {
                        fileCompressed = originalFile;
                    }
                }catch (Exception e){

                }
                cursor.close();
            }else{
                Toast.makeText(getApplicationContext(), "Foto gagal di-load", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initComponents(){
        toolbar = findViewById(R.id.toolbar);
        day = findViewById(R.id.name_day);
        name = findViewById(R.id.name_act);
        location = findViewById(R.id.name_location);
        contact = findViewById(R.id.contact_person);
        description = findViewById(R.id.name_description);
        upload = findViewById(R.id.insert_image);
        button = findViewById(R.id.btn_add);
        clock = findViewById(R.id.name_clock);
        action = null;
        activities = getIntent().getExtras();
        setSupportActionBar(toolbar);
    }

    private void postData(){
        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError("judul kegiatan belum di isi");
        }else if(TextUtils.isEmpty(day.getText().toString())){
            day.setError("tanggal kegiatan belum ter isi");
        }else if(TextUtils.isEmpty(clock.getText().toString())){
            clock.setError("jam kegiatan belum ter isi");
        }else if(TextUtils.isEmpty(description.getText().toString())){
            description.setError("deskripsi kegiatan belum ter isi");
        } else if (TextUtils.isEmpty(upload.getText().toString())) {
            upload.setError("gambar belum dipilih");
        }else {
            button.setIndeterminateProgressMode(true);
            button.setProgress(1);
            ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);

            MultipartBody.Part body,files;
                try {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileCompressed);
                    body = MultipartBody.Part.createFormData("picture", fileCompressed.getName(),
                            requestFile);
                }catch (Exception e){
                    body = null;
                }

                try {
                    files = prepareFilePart("files",documentUri);
                }catch (Exception e){
                    files = null;
                }



            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot();
            final String key = reference.push().getKey();

            String dateTime = day.getText().toString() + " " + clock.getText().toString();

            RequestBody reqName = MultipartBody.create(MediaType.parse("multipart/form-data"),
                    (name.getText().toString().isEmpty()) ? "" : name.getText().toString());
            RequestBody reqDate = MultipartBody.create(MediaType.parse("multipart/form-data"),
                    dateTime);
            RequestBody reqLocation = MultipartBody.create(MediaType.parse("multipart/form-data"),
                    (location.getText().toString().isEmpty()) ? "" : location.getText().toString());
            RequestBody reqDesription = MultipartBody.create(MediaType.parse("multipart/form-data"),
                    (description.getText().toString().isEmpty()) ? "" : description.getText().toString());
            RequestBody reqKey = MultipartBody.create(MediaType.parse("multipart/form-data"),
                    key);

            Call<PostData> mPostActivity;
            mPostActivity = mApiInterface.postActivity(body, files, reqName, reqLocation,reqDate, reqDesription, reqKey);
            if (action != null) {
                RequestBody reqId = MultipartBody.create(MediaType.parse("multipart/form-data"),
                        idActivity);
                mPostActivity = mApiInterface.updateActivity(body, files, reqId, reqName, reqLocation, reqDate, reqDesription);
            }
            mPostActivity.enqueue(new Callback<PostData>() {
                @Override
                public void onResponse(Call<PostData> call, Response<PostData> response) {
                    if(response.code()==200){
                        if (response.body().getStatus().equals("success")) {
                            Toast.makeText(getApplicationContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            button.setProgress(100);
                            if (action != null) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                Map<String, Object> map = new HashMap<>();
                                map.put(key, "");
                                reference.updateChildren(map);
                            }
                            finish();
                            ((ActivitiesFragment) ActivitiesFragment.activityFragment).loadData();
                        }
                    }else{
                        buttonError();
                    }
                }

                @Override
                public void onFailure(Call<PostData> call, Throwable t) {
                    button.setProgress(-1);
                    button.setProgress(0);
                    Toast.makeText(getApplicationContext(),"Cek koneksi internet",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void buttonError(){
        button.setProgress(-1);
        button.setErrorText("Failed");
        button.setProgress(0);
        button.setIndeterminateProgressMode(false);
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = new File(FilePath.getPath(getApplicationContext(),fileUri));

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @Override
    public boolean onSupportNavigateUp() {
        ((ActivitiesFragment) ActivitiesFragment.activityFragment).loadData();
        finish();
        return true;
    }
}