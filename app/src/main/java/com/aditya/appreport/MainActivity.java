package com.aditya.appreport;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button GetImageFromGalleryButton;

    Button UploadImageOnServerButton;

    ImageView ShowSelectedImage;

    EditText pelanggaran;

    EditText nip;

    EditText namaPegawai;

    EditText dateTime;

    EditText divisi;

    EditText kat_pel;

    EditText pel_stat;

    Button cek;


    Bitmap FixBitmap;


    String ImageTag = "image_tag" ;

    String ImageName = "image_data" ;

    String NipPegawai = "NIP";

    String NamaPegawai = "NAMA";

    String Tanggal = "tanggal";

    String Divisi = "NAMA_DIVISI";

    String  Kategori= "kat_pelanggaran";

    String Status = "pelanggaran_status";



    ProgressDialog progressDialog ;

    ByteArrayOutputStream byteArrayOutputStream ;

    byte[] byteArray ;

    String ConvertImage ;

    String GetImageNameFromEditText;

    String GetNameFromEditText;

    String GetNipFromEditText;

    String GetDateFromEditText;

    String GetDivisiFromEditText;

    String GetKategoriFromEditText;

    String GetStatusFromEditText;

    HttpURLConnection httpURLConnection ;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter ;

    int RC ;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;

    boolean check = true;

    private int GALLERY = 1, CAMERA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetImageFromGalleryButton = (Button)findViewById(R.id.buttonSelect);

        UploadImageOnServerButton = (Button)findViewById(R.id.buttonUpload);

        ShowSelectedImage = (ImageView)findViewById(R.id.imageView);

        pelanggaran = (EditText)findViewById(R.id.pelanggaran);

        namaPegawai=(EditText)findViewById(R.id.nama);


        nip = (EditText)findViewById(R.id.NIP);


        dateTime = (EditText)findViewById(R.id.editTextDate);

        divisi=(EditText)findViewById(R.id.divisi);


        kat_pel=(EditText)findViewById(R.id.kat_pelanggaran);

        pel_stat = (EditText)findViewById(R.id.pelanggaran_status);

        cek = (Button) findViewById(R.id.cekk);


        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);

        final int month = calendar.get(Calendar.MONTH);

        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        cek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        dateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = dayOfMonth + "/" + month + "/" + year;
                        dateTime.setText(date);
                    }

                }, year, month, day);
                datePickerDialog.show();
            }
        });



        byteArrayOutputStream = new ByteArrayOutputStream();

        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();


            }
        });


        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = pelanggaran.getText().toString();

                GetNameFromEditText = namaPegawai.getText().toString();

                GetNipFromEditText = nip.getText().toString();

                GetDateFromEditText = dateTime.getText().toString();

                GetDivisiFromEditText = divisi.getText().toString();

                GetKategoriFromEditText = kat_pel.getText().toString();

                GetStatusFromEditText = pel_stat.getText().toString();


                UploadImageToServer();

                Intent exit = new Intent(getApplicationContext(),ExitActivity.class);
                startActivity(exit);
            }
        });


        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }
    }

    private void getData() {

        String value = nip.getText().toString().trim();

        if (value.equals("")) {
            StyleableToast.makeText(this, "NIP TIDAK BOLEH KOSONG", R.style.gagal).show();
            return;
        }

        String url = Config.DATA_URL + nip.getText().toString().trim();



        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void showJSON(String response) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String NAMA = jo.getString(Config.KEY_NAMA);
                String NAMA_DIVISI = jo.getString(Config.KEY_NAMA_DIVISI);
                namaPegawai.setText(NAMA);
                divisi.setText(NAMA_DIVISI);

                final HashMap<String, String> employees = new HashMap<>();
                employees.put(Config.KEY_NAMA, NAMA);
                employees.put(Config.KEY_NAMA_DIVISI, NAMA_DIVISI);

                list.add(employees);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    ShowSelectedImage.setImageBitmap(FixBitmap);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    StyleableToast.makeText(MainActivity.this, "Failed!", R.style.gagal).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBitmap);
            UploadImageOnServerButton.setVisibility(View.VISIBLE);
        }
    }


    public void UploadImageToServer(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(MainActivity.this,string1,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);

                HashMapParams.put(ImageName, ConvertImage);

                HashMapParams.put(NipPegawai, GetNipFromEditText);

                HashMapParams.put(NamaPegawai,GetNameFromEditText );

                HashMapParams.put(Tanggal,GetDateFromEditText );

                HashMapParams.put(Divisi, GetDivisiFromEditText);

                HashMapParams.put(Kategori,GetKategoriFromEditText);

                HashMapParams.put(Status,GetStatusFromEditText );

                String FinalData = imageProcessClass.ImageHttpRequest("http://192.168.0.4/AndroidUploadImage/upload-image-to-server.php", HashMapParams);



                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(MainActivity.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }
        }
    }
}
