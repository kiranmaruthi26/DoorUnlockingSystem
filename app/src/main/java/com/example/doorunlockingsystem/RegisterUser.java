package com.example.doorunlockingsystem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterUser extends AppCompatActivity {
    EditText pName, pAge;
    TextView sr;
    ImageView pImageView;
    String encode_string, image_name;
    Bitmap bitmap;
    File file;
    Uri uri;
    ProgressBar loader;
    boolean isImageTaken = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        pName = findViewById(R.id.name);
        pAge = findViewById(R.id.age);
        pImageView = findViewById(R.id.imageView);
        sr = findViewById(R.id.server_response);
        loader = findViewById(R.id.progressBar);



    }

    public void getImage(View view) {
        String permission = "Manifest.permission.READ_EXTERNAL_STORAGE";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
            Toast.makeText(this, "request", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            pickImage();

        }
    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK && data!=null){
            pImageView.setImageURI(data.getData());
            uri = data.getData();
            isImageTaken = true;
            String filepath = getRealPathFromURI(uri);



        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int file_name_num = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
                    //result = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    result = cursor.getString(file_name_num);
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    /*public void getImage(View view) {

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Random randomFileName = new Random();
        //image_name = "test123.jpg";
        image_name = Integer.toString(randomFileName.nextInt(9999-1000)+1000)+".jpg";
        //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name;
       file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + image_name);

        //uri = Uri.fromFile(file);
        //getFileUri();
        uri = FileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID+"."+getLocalClassName()+".provider",
                file);


        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        registerActivityResult.launch(i);

    }*/

    /*ActivityResultLauncher<Intent> registerActivityResult = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){

                        //Bitmap showImg = (Bitmap) result.getData().getExtras().get("data");

                        pImageView.setImageURI(uri);
                        isImageTaken = true;
                        //new Encode_image().execute();
                    }
                }
            });
*/


    private class Encode_image extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... Void){
            //BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            //bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bitmap = BitmapFactory.decodeFile(getRealPathFromURI(uri));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmap.recycle();
            byte[] array = stream.toByteArray();

            encode_string = Base64.encodeToString(array, 1);


            return null;
        }

       @Override
       protected void onPostExecute(Void aVoid) {
           Toast.makeText(RegisterUser.this, "Requesting Server", Toast.LENGTH_SHORT).show();
           try {
               Thread.sleep(1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           makeRequest();
       }

      private void makeRequest() {


           StringRequest request = new StringRequest(Request.Method.POST, "https://doorunlocking.getcleared.in/register.php",
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {
                           loader.setVisibility(ProgressBar.INVISIBLE);
                           sr.setText("Server Response  : "+response);

                       }
                   }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   loader.setVisibility(ProgressBar.INVISIBLE);
                   sr.setText("Error  : "+error.toString());
               }
           }){
               @Nullable
               @Override
               protected Map<String, String> getParams() {

                   HashMap<String, String> map = new HashMap<>();
                   map.put("encode_string",encode_string);
                   map.put("image_name", getFileName(uri));
                   map.put("name", pName.getText().toString());
                   map.put("age",pAge.getText().toString());
                   //map.put("name", "kiran123");
                  // map.put("age","45");

                   return map;
               }
           };
          RequestQueue requestQueue = Volley.newRequestQueue(RegisterUser.this);
           requestQueue.add(request);

       }



   }



    public void submit(View view) {

        String ppName = pName.getText().toString();
        String ppAge = pAge.getText().toString();
        if(ppName.isEmpty()){
            pName.setError("Name Required");
        }else if(ppAge.isEmpty()){
            pAge.setError("Age Required");
        }else if(!isImageTaken){
            Toast.makeText(this, "Photo Not Taken", Toast.LENGTH_SHORT).show();
        }else{
            if(isImageTaken){
                loader.setVisibility(ProgressBar.VISIBLE);
                sr.setText("Attempting connection request...");
                new Encode_image().execute();
                //isImageTaken = false;
            }

        }

    }
}