package com.khair.upload_img;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText edName;
    ProgressBar progressBar;
    AppCompatButton uploadButton;
    Bitmap bitmap;
    String name;
    String encodeImage;
    int MyRequestCod=1;
    TextView textView;
    String API_URL="https://abulk77912.000webhostapp.com/apps/upload_img.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.imageView);
        edName=findViewById(R.id.edName);
        uploadButton=findViewById(R.id.uploadButton);
        progressBar=findViewById(R.id.progressBar);
        textView=findViewById(R.id.textView);

        imageView.setOnClickListener(v -> {


            Dexter.withContext(MainActivity.this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            Intent intent=new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent,"select Image"),MyRequestCod);


                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();

                        }
                    }).check();


        });

        uploadButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            name=edName.getText().toString();

            StringRequest StringRequest=new StringRequest(Request.Method.POST, API_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                    textView.setText(response);
                    progressBar.setVisibility(View.GONE);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  uploadButton.setText(""+error.getMessage());
                    progressBar.setVisibility(View.GONE);
                    textView.setText(error.getMessage());

                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>myParams=new HashMap<>();
                    myParams.put("images",encodeImage);
                    myParams.put("name",name);
                    return myParams;
                }
            };

            RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
            requestQueue.add(StringRequest);

        });





    }
    //=======onCreate end here=====================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==MyRequestCod&&resultCode==RESULT_OK&&data!=null){
            Uri filePath=data.getData();
            try {
                InputStream inputStream=getContentResolver().openInputStream(filePath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                ImageStore(bitmap);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap ImageStore( Bitmap bitmap1) {

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap1.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageByte=outputStream.toByteArray();
        encodeImage=android.util.Base64.encodeToString(imageByte, Base64.DEFAULT);

        return bitmap;
    }









//==================================================================
}