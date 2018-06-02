package com.studder.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.R;
import com.studder.database.schema.UserTable;
import com.studder.holders.GalleryItemViewHolder;
import com.studder.model.Media;

import java.io.File;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private static final int REQUEST_TAKE_IMAGE = 2;
    private static final int REQUEST_READ_IMAGE_EXTERNAL_STORAGE = 3;

    private ImageViewAdapeter mImageViewAdapter;
    private ImageRefreshTask mImageRefreshTask;
    private RecyclerView mImageViewRecyclerView;
    private TextView mNameSurnameTextView;
    private ImageButton mTakeImageNowImageButton;
    private ImageButton mChooseImageImageButton;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Intent newImageSelectionIntent(){

        Log.d(TAG, "newImageSelectionIntent()");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences pref = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        String name = pref.getString(UserTable.Cols.NAME, "Unknown");
        String surname = pref.getString(UserTable.Cols.SURNAME, "Unknown");

        mImageViewRecyclerView = view.findViewById(R.id.recycler_view_fragment_profile_images);
        mNameSurnameTextView = view.findViewById(R.id.text_view_fragment_profile_name_surname);
        mChooseImageImageButton = view.findViewById(R.id.image_button_fragment_profile_chose_photo);
        mTakeImageNowImageButton = view.findViewById(R.id.image_button_fragment_profile_take_photo_now);

        mNameSurnameTextView.setText(name + " " + surname);

        mImageRefreshTask = new ImageRefreshTask();
        mImageRefreshTask.execute((Void) null);

        mChooseImageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_IMAGE_EXTERNAL_STORAGE);
                } else {
                    Intent chooserIntent = newImageSelectionIntent();
                    startActivityForResult(Intent.createChooser(chooserIntent, "Select Image"), REQUEST_TAKE_IMAGE);
                }
            }
        });

        return view;
    }


    private class ImageRefreshTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... booleans) {

            SharedPreferences pref = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            Integer id = pref.getInt(UserTable.Cols._ID, -1);

            if(id != -1) {
                Ion.with(getContext())
                        .load("GET", "http://10.0.2.2:8080/media/" + id)
                        .as(new TypeToken<List<Media>>(){})
                        .withResponse()
                        .setCallback(new FutureCallback<Response<List<Media>>>() {
                            @Override
                            public void onCompleted(Exception e, Response<List<Media>> result) {
                                if(result.getHeaders().code() == 200){
                                    Log.d(TAG, "ImageRefreshTask -> onCompleted -> code == 200");

                                    List<Media> media = result.getResult();

                                    for(int i =0;i < media.size();i++) {
                                        byte[] bitmapBytes = Base64.decode(media.get(i).getPath(), Base64.DEFAULT);
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                        bmp = bmp.createScaledBitmap(bmp, 200, 200, false);
                                        media.get(i).setBitmap(BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length));
                                    }

                                    ImageViewAdapeter imageAdapter = new ImageViewAdapeter(result.getResult());
                                    GridLayoutManager glm = new GridLayoutManager(getContext(), 3);

                                    mImageViewRecyclerView.setLayoutManager(glm);

                                    mImageViewRecyclerView.setAdapter(imageAdapter);
                                } else {
                                    Log.d(TAG, "ImageRefreshTask -> onCompleted -> code != 200");
                                }
                            }
                        });
            }
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case REQUEST_CHOOSE_IMAGE: {
                Uri uri = data.getData();
                File newFile = new File(getImagePath(uri));
                Ion.with(getContext())
                        .load("https://10.0.2.2/media/someDescription")
                        .setMultipartParameter("goop", "noop")
                        .setMultipartFile("archive", "application/zip", newFile)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject> result) {
                                if(result.getHeaders().code() == 200){
                                    Toast.makeText(getContext(), R.string.login_register_activity_success, Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(getContext(), R.string.login_register_activity_fail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            case REQUEST_TAKE_IMAGE: {

            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult(...)");

        if(requestCode == REQUEST_READ_IMAGE_EXTERNAL_STORAGE){
            Log.d(TAG, "onRequestPermissionsResult(...) : requestCode == REQUEST_READ_IMAGE_EXTERNAL_STORAGE");
            if((grantResults != null) && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Intent selectImage = newImageSelectionIntent();

                Log.d(TAG, "onRequestPermissionsResult(...) -> startActivityForResult -> REQUEST_PICK_IMAGE");
                startActivityForResult(Intent.createChooser(selectImage, "Select Photo"), REQUEST_CHOOSE_IMAGE);
            }
        }
    }

    private class ImageViewAdapeter extends RecyclerView.Adapter<GalleryItemViewHolder> {

        List<Media> media;

        public ImageViewAdapeter(List<Media> media) {
            this.media = media;
        }

        @NonNull
        @Override
        public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            Log.d(TAG, "ImageViewAdapeter -> onCreateViewHolder(...) -> start");

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_image_item, parent, false);

            Log.d(TAG, "ImageViewAdapeter -> onCreateViewHolder(...) -> success");
            return new GalleryItemViewHolder(view, getActivity());

        }

        @Override
        public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
            Log.d(TAG, "ImageViewAdapeter -> onBindViewHolder");
            holder.bind(media.get(position));
        }

        @Override
        public int getItemCount() {
            return media.size();
        }
    }

    private String getImagePath(Uri uri){

        Log.d(TAG, "getImagePath(Uri uri)");

        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContext().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        Log.d(TAG, "getImagePath(Uri uri) : return == " + path);

        return path;
    }
}
