package android.bignerdranch.com;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.UUID;

public class RecordPagerActivity extends AppCompatActivity {
    private static final String EXTRA_RECORD_ID =
            "com.bignerdranch.android.mycheckins.record_id";
    private static final int REQUEST_ERROR = 0;
    private ViewPager mViewPager;
    private List<Record> mRecords;
    private GoogleApiClient mClient;

    public static Intent newIntent(Context packageContext, UUID recordId) {
        Intent intent = new Intent(packageContext, RecordPagerActivity.class);
        intent.putExtra(EXTRA_RECORD_ID, recordId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_pager);
        UUID recordId = (UUID) getIntent().getSerializableExtra(EXTRA_RECORD_ID);
        mClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                    } })
                .build();


        mViewPager = (ViewPager) findViewById(R.id.record_view_pager);
        mRecords = RecordLab.get(this).getRecords();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Record record = mRecords.get(position);
                return RecordFragment.newInstance(record.getId());
            }
            @Override
            public int getCount() {
                return mRecords.size();
            }
        });
        for (int i = 0; i < mRecords.size(); i++) {
            if (mRecords.get(i).getId().equals(recordId)) {
                mViewPager.setCurrentItem(i);
                break; }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability
                    .getErrorDialog(this, errorCode, REQUEST_ERROR,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    // Leave if services are unavailable.
                                    finish(); }
                            });
            errorDialog.show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }
    public void sendMessage(View view)
    {
        Intent intent = new Intent(RecordPagerActivity.this, MapsActivity.class);
        startActivity(intent);
    }


}
