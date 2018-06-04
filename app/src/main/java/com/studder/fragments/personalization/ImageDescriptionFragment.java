package com.studder.fragments.personalization;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.studder.R;
import com.studder.model.Profile;
import com.studder.utils.ImageUtils;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageDescriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageDescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageDescriptionFragment extends Fragment {

    public static final int REQUEST_PICK_IMAGE = 1;
    public static final int REQUEST_TAKE_IMAGE = 2;
    public static final int REQUEST_READ_IMAGE_EXTERNAL_STORAGE = 3;

    private OnFragmentInteractionListener mListener;

    private static final String MPOSITION = "POSITION";
    public static String IMAGE_PATH = "IMAGE_PATH";
    public static String TAG = "ImageDescriptionFragment";

    private Integer position;

    private Profile mProfile;

    private ImageView mSelectedImageView;
    private ImageButton mTakeImageButton;
    private Button mSelectImageButton;
    private EditText mEnterDescriptionButton;

    private File mImageFile;

    // Implicit intent for image capture
    private Intent newCaptureImageIntent(){

        Log.d(TAG, "newCaptureImageIntent()");

        PackageManager packageManager = getActivity().getPackageManager();

        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (mImageFile != null) && (captureImage.resolveActivity(packageManager) != null);

        mTakeImageButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".com.studder.provider", mImageFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            Log.d(TAG, "newCaptureImageIntent : canTakePhoto == false");
        }

        return captureImage;
    }

    // Implicit intent for image selection
    private Intent newImageSelectionIntent(){

        Log.d(TAG, "newImageSelectionIntent()");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        return intent;
    }

    public ImageDescriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImageDescriptionFragment.
     */
    public static ImageDescriptionFragment newInstance(int position) {

        Log.d(TAG, "newInstance(int position) -> " + position);

        ImageDescriptionFragment fragment = new ImageDescriptionFragment();

        Bundle args = new Bundle();
        args.putInt(MPOSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState(@NonNull Bundle)");

        if((mImageFile == null) && (!mImageFile.exists())){
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".com.studder.provider", mImageFile);
            outState.putString(IMAGE_PATH, getImagePath(uri));
        }

    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate(@Nullable Bundle)");


        Log.d(TAG, "newCaptureImageIntent() : (defaultImage || savedImage)");

        mImageFile = Profile.getPhotoFile(getActivity());

        if((savedInstanceState != null) && (savedInstanceState.getString(IMAGE_PATH) != null)){
            String path = savedInstanceState.getString(IMAGE_PATH);
            mImageFile = new File(path);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView(LayoutInflater, ViewGroup, Bundle)");

        // Inflate the layout for this fragment
        position = getArguments().getInt(MPOSITION);

        View view = inflater.inflate(R.layout.fragment_image_description, container, false);

        // Restrict to devices with camera - PackageManager
        mSelectedImageView = view.findViewById(R.id.image_view_personalize_image_description_selected_image_display);
        mTakeImageButton = view.findViewById(R.id.image_button_personalize_image_description_take_image);
        mSelectImageButton = view.findViewById(R.id.image_button_personalize_image_description_select_image_from_galery);


        Log.d(TAG, "onCreateView -> newCaptureImageIntent");
        final Intent captureImage = newCaptureImageIntent();

        Log.d(TAG, "onCreateView -> mTakeImageButton.setOnClickListener");
        mTakeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mTakeImageButton onClick(...) -> startActivityForResult -> REQUEST_TAKE_IMAGE");
                startActivityForResult(captureImage, REQUEST_TAKE_IMAGE);
            }
        });


        Log.d(TAG, "onCreateView -> mSelectImageButton.setOnClickListener");
        mSelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_IMAGE_EXTERNAL_STORAGE);
                } else {
                    Intent selectImage = newImageSelectionIntent();
                    Log.d(TAG, "mSelectImageButton onClick(...) -> startActivityForResult -> REQUEST_PICK_IMAGE");
                    startActivityForResult(Intent.createChooser(selectImage, "Select Photo"), REQUEST_PICK_IMAGE);
                }
            }
        });

        updateSelectedImageView();

        return view;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateSelectedImageView(){

        if((mImageFile == null) && (!mImageFile.exists())){
            mSelectedImageView.setImageDrawable(null);
        } else {
            Bitmap bitmap = ImageUtils.getScaledBitmap(mImageFile.getPath(), getActivity());
            mSelectedImageView.setImageBitmap(bitmap);
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
                startActivityForResult(Intent.createChooser(selectImage, "Select Photo"), REQUEST_PICK_IMAGE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult(...)");

        if(requestCode == REQUEST_PICK_IMAGE){
            Log.d(TAG, "onActivityResult(...) : requestCode == REQUEST_PICK_IMAGE, resultCode == " + resultCode);
            Uri uri = data.getData();
            mImageFile = new File(getImagePath(uri));
            updateSelectedImageView();
        }
        if(requestCode == REQUEST_TAKE_IMAGE){
            Log.d(TAG, "onActivityResult(...) : requestCode == REQUEST_TAKE_IMAGE, resultCode == " + resultCode);
            updateSelectedImageView();
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
