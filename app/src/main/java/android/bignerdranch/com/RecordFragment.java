package android.bignerdranch.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RecordFragment extends Fragment {
    private Record mRecord;
    private Button mDateButton;
    private EditText mPlace;
    private EditText mDetails;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private static final String ARG_RECORD_ID = "record_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO= 2;


    public static RecordFragment newInstance(UUID recordId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECORD_ID, recordId);
        RecordFragment fragment = new RecordFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record, container, false);
        mTitleField = (EditText) v.findViewById(R.id.record_title);
        mTitleField.setText(mRecord.getTitle());
        mDetails = (EditText) v.findViewById(R.id.record_details);
        mDetails.setText(mRecord.getDetails());
        mPlace = (EditText) v.findViewById(R.id.record_place);
        mPlace.setText(mRecord.getPlace());
        mPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mRecord.setPlace(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mRecord.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
        mDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }
            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mRecord.setDetails(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });
        mDateButton = (Button) v.findViewById(R.id.record_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mRecord.getDate());
                dialog.setTargetFragment(RecordFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            } });
        mPhotoButton = (ImageButton) v.findViewById(R.id.record_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.MyCheckins.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);}
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.record_photo);
        updatePhotoView();
        return v;
    }

    private void updateDate() {
        mDateButton.setText(mRecord.getDate().toString());
    }

    private EditText mTitleField;{

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return; }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mRecord.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.MyCheckins.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID recordId = (UUID) getArguments().getSerializable(ARG_RECORD_ID);
        mRecord = RecordLab.get(getActivity()).getRecord(recordId);
        mPhotoFile = RecordLab.get(getActivity()).getPhotoFile(mRecord);
        LocationRequest.create();
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mRecord, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mRecord.set
            }
        })

    }
    @Override
    public void onPause() {
        super.onPause();
        RecordLab.get(getActivity())
                .updateRecord(mRecord);
    }
}
