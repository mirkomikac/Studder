package com.studder.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.R;
import com.studder.SettingsActivity;
import com.studder.database.schema.UserTable;
import com.studder.holders.GalleryItemViewHolder;
import com.studder.model.Media;
import com.studder.model.Profile;
import com.studder.model.UserMatch;
import com.studder.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private static final int REQUEST_CHOOSE_IMAGE = 1;
    private static final int REQUEST_TAKE_IMAGE = 2;
    private static final int REQUEST_READ_IMAGE_EXTERNAL_STORAGE = 3;
    public static String IMAGE_PATH = "IMAGE_PATH";

    private ImageViewAdapeter mImageViewAdapter;
    private ImageRefreshTask mImageRefreshTask;
    private RecyclerView mImageViewRecyclerView;
    private TextView mNameSurnameTextView;
    private ImageButton mTakeImageNowImageButton;
    private ImageButton mChooseImageImageButton;
    private ImageView mProfileImageView;
    private Button mChangeProfileInfoButton;

    private File mImageFile;

    private Context mContext;
    private TextView mDescriptionTextView;

    private TextView mMatchedNumberTextView;

    private Integer numberOfMatches;

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

    private Intent newCaptureImageIntent(){

        Log.d(TAG, "newCaptureImageIntent()");

        PackageManager packageManager = getActivity().getPackageManager();

        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (captureImage.resolveActivity(packageManager) != null);

        mTakeImageNowImageButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".com.studder.provider", mImageFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            Log.d(TAG, "newCaptureImageIntent : canTakePhoto == false");
        }

        return captureImage;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if((mImageFile == null) && (!mImageFile.exists())){
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".com.studder.provider", mImageFile);
            outState.putString(IMAGE_PATH, getImagePath(uri));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (getArguments() != null) {}
        mImageFile = Profile.getPhotoFile(getActivity());

        if((savedInstanceState != null) && (savedInstanceState.getString(IMAGE_PATH) != null)){
            String path = savedInstanceState.getString(IMAGE_PATH);
            mImageFile = new File(path);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences pref = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        String name = pref.getString(UserTable.Cols.NAME, "Unknown");
        String surname = pref.getString(UserTable.Cols.SURNAME, "Unknown");
        final String description = pref.getString(UserTable.Cols.DESCRIPTION, "Unknown");

        mImageViewRecyclerView = view.findViewById(R.id.recycler_view_fragment_profile_images);
        mNameSurnameTextView = view.findViewById(R.id.text_view_fragment_profile_name_surname);
        mChooseImageImageButton = view.findViewById(R.id.image_button_fragment_profile_chose_photo);
        mTakeImageNowImageButton = view.findViewById(R.id.image_button_fragment_profile_take_photo_now);
        mProfileImageView = view.findViewById(R.id.image_view_fragment_profile_profile_image);
        mChangeProfileInfoButton = view.findViewById(R.id.button_fragment_profile_change_info);
        mDescriptionTextView = view.findViewById(R.id.text_view_fragment_profile_description);
        mMatchedNumberTextView = view.findViewById(R.id.fragment_profile_matched_number);

        String ipConfig = getResources().getString(R.string.ipconfig);

        Ion.with(mContext)
                .load("GET", "http://"+ipConfig+"/matches/getMatchesMe")
                .as(new TypeToken<List<UserMatch>>(){})
                .withResponse()
                .setCallback(new FutureCallback<Response<List<UserMatch>>>() {
                    @Override
                    public void onCompleted(Exception e, Response<List<UserMatch>> result) {
                        if(result.getHeaders().code() == 200){
                            if(result.getResult() != null){
                                numberOfMatches = result.getResult().size();
                            } else {
                                numberOfMatches = 0;
                            }
                            mMatchedNumberTextView.setText(numberOfMatches.toString());
                        }
                    }
                });


        mNameSurnameTextView.setText(name + " " + surname);

        mDescriptionTextView.setText(description);

        mDescriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Change description");

                final EditText input = new EditText(mContext);
                input.setText(description);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setSingleLine(false);
                builder.setView(input);

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDescriptionTextView.setText(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


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
                    startActivityForResult(Intent.createChooser(chooserIntent, "Select Image"), REQUEST_CHOOSE_IMAGE);
                }
            }
        });

        final Intent captureImageIntent = newCaptureImageIntent();

        mTakeImageNowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImageIntent, REQUEST_TAKE_IMAGE);
            }
        });

        mChangeProfileInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    private class ImageRefreshTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... booleans) {

            SharedPreferences pref = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            Integer id = pref.getInt(UserTable.Cols._ID, -1);
            String ipConfig = getResources().getString(R.string.ipconfig);
            if(id != -1) {
                Ion.with(getContext())
                        .load("GET", "http://"+ipConfig+"/media/me")
                        .as(new TypeToken<List<Media>>(){})
                        .withResponse()
                        .setCallback(new FutureCallback<Response<List<Media>>>() {
                            @Override
                            public void onCompleted(Exception e, Response<List<Media>> result) {
                                if(result.getHeaders().code() == 200){
                                    Log.d(TAG, "ImageRefreshTask -> onCompleted -> code == 200");

                                    List<Media> media = result.getResult();

                                    for(int i =0;i < media.size();i++) {
                                        byte[] bitmapBytes = Base64.decode(media.get(i).getEncodedImage(), Base64.DEFAULT);
                                        Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                        bmp = bmp.createScaledBitmap(bmp, 350, 350, false);
                                        media.get(i).setBitmap(bmp);
                                    }
                                    mImageViewAdapter = new ImageViewAdapeter(result.getResult());
                                    GridLayoutManager glm = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);

                                    mImageViewRecyclerView.setHasFixedSize(false);
                                    mImageViewRecyclerView.setLayoutManager(glm);
                                    mImageViewRecyclerView.setAdapter(mImageViewAdapter);
                                } else {
                                    Log.d(TAG, "ImageRefreshTask -> onCompleted -> code != 200");
                                }
                            }
                        });
            }

            Ion.with(getContext())
                    .load("GET", "http://"+ipConfig+"/media/getProfileImage")
                    .as(new TypeToken<Media>(){})
                    .withResponse()
                    .setCallback(new FutureCallback<Response<Media>>() {
                        @Override
                        public void onCompleted(Exception e, Response<Media> result) {
                            if(result.getHeaders().code() == 200){
                                Log.d(TAG, "ImageRefreshTask  profile pic -> onCompleted -> code == 200");
                                byte[] bitmapBytes = Base64.decode(result.getResult().getEncodedImage(), Base64.DEFAULT);
                                Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                bmp = bmp.createScaledBitmap(bmp, 350, 350, false);
                                mProfileImageView.setImageBitmap(bmp);
                            }
                            else {
                                Log.d(TAG, "ImageRefreshTask  profile pic -> onCompleted -> code != 200");
                            }
                        }
                    });

            return null;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_CHOOSE_IMAGE: {
                    if (data != null) {
                        Uri uri = data.getData();
                        File newFile = new File(getImagePath(uri));
                        uploadImage(newFile);
                        break;
                    }
                }
                case REQUEST_TAKE_IMAGE: {
                    if ((mImageFile != null) && (mImageFile.exists())) {
                        uploadImage(mImageFile);
                        break;
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(File newFile){
        String ipConfig = getResources().getString(R.string.ipconfig);
        Ion.with(getContext())
                .load("POST", "http://"+ipConfig+"/media/upload/someDescription")
                .setMultipartParameter("file", "file")
                .setMultipartFile("file", "image/jpg", newFile)
                .as(new TypeToken<Media>() {})
                .withResponse()
                .setCallback(new FutureCallback<Response<Media>>() {
                    @Override
                    public void onCompleted(Exception e, Response<Media> result) {
                        if (result.getHeaders().code() == 200) {
                            Toast.makeText(getContext(), R.string.login_register_activity_success, Toast.LENGTH_SHORT).show();
                            refreshContent(null);
                        } else {
                            Toast.makeText(getContext(), R.string.login_register_activity_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
            return new GalleryItemViewHolder(view, getActivity(), ProfileFragment.this);

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

        public void updateDataSet(Media item){
            this.media.add(item);
            notifyItemInserted(this.media.indexOf(item));
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

    public void refreshContent(Bitmap bmp) {
        super.onResume();
        SharedPreferences pref = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        Integer id = pref.getInt(UserTable.Cols._ID, -1);
        String ipConfig = getResources().getString(R.string.ipconfig);
        if (id != -1) {
            Ion.with(getContext())
                    .load("GET", "http://"+ipConfig+"/media/me")
                    .as(new TypeToken<List<Media>>() {
                    })
                    .withResponse()
                    .setCallback(new FutureCallback<Response<List<Media>>>() {
                        @Override
                        public void onCompleted(Exception e, Response<List<Media>> result) {
                            if (result.getHeaders().code() == 200) {
                                Log.d(TAG, "ImageRefreshTask -> onCompleted -> code == 200");

                                List<Media> media = result.getResult();

                                for (int i = 0; i < media.size(); i++) {
                                    byte[] bitmapBytes = Base64.decode(media.get(i).getEncodedImage(), Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                    bmp = bmp.createScaledBitmap(bmp, 350, 350, false);
                                    media.get(i).setBitmap(bmp);
                                }
                                ImageViewAdapeter imageAdapter = new ImageViewAdapeter(result.getResult());
                                GridLayoutManager glm = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);

                                mImageViewRecyclerView.setLayoutManager(glm);
                                mImageViewRecyclerView.setHasFixedSize(false);
                                mImageViewRecyclerView.setAdapter(imageAdapter);
                            } else {
                                Log.d(TAG, "ImageRefreshTask -> onCompleted -> code != 200");
                            }
                        }
                    });
        }

        if(bmp != null){
            mProfileImageView.setImageBitmap(bmp);
        }
    }
}
