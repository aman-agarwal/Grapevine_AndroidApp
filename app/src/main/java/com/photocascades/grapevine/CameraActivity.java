package com.photocascades.grapevine;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class CameraActivity extends ActionBarActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView mImageView;
    ParseUser currentUser = ParseUser.getCurrentUser();

    TextView tUser;
    EditText tTitle, tDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setContentView(R.layout.activity_camera);
            mImageView = (ImageView) findViewById(R.id.photo);
            tUser = (TextView) findViewById(R.id.photo_uploader);
            tTitle = (EditText) findViewById(R.id.photo_title);
            tDesc = (EditText) findViewById(R.id.photo_desc);

            tUser.setText(currentUser.getUsername());

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
        else
        {
            Intent feedIntent = new Intent(this, PhotoFeed.class);
            startActivity(feedIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void uploadBtn(View v) {
        AlertDialog.Builder builderFail = new AlertDialog.Builder(this);
        builderFail.setTitle("Image Upload Failure")
                   .setNegativeButton("OK", null);

        Bitmap bm = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final ParseFile file = new ParseFile(currentUser.getUsername() + ".png", byteArray);

        Log.d("Upload", "Going to save");
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("UploadImg", "Success");
                    PhotoObj photo = new PhotoObj(currentUser, file,
                                                  tTitle.getText().toString(),
                                                  tDesc.getText().toString());
                    photo.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("UploadPhotoFeed", "Success");
                            } else {
                                Log.e("UploadPhotoFeed", e.toString());
                            }
                        }
                    });
                    onBackPressed();
                } else {
                    Log.e("UploadImgERROR", e.toString());
                }
            }
        });
    }
}
