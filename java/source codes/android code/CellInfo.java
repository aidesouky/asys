package tech.twin.CellInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import android.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    Button infoButton;
    TextView infoText;
    String locationValue="";
    private TelephonyManager telephonyManager;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoButton = findViewById(R.id.infoButton);
        infoText =  findViewById(R.id.infoText);
        //requestLocationUpdates();
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestSingleLocation();
                infoText.setText(getCellInfo()+locationValue);
            }
        });
    }

    private String getCellInfo(){

        String result = "";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return "";
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
            for (Object obj : cellInfos.toArray()){
                CellInfo cellInfo = (CellInfo)obj;
                if (cellInfo.isRegistered()) {

                    result = "DeviceId: "+getIMEIDeviceId(this)+"\n";
                    try{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            result = result+"SubscriptionId: "+telephonyManager.getSubscriptionId()+"\n";
                        }
                    }catch(Exception e){

                    }
                    result = result+"NetworkAccessIdentifier(Nai): "+telephonyManager.getNai()+"\n";
                    result = result+"NetworkSpecifier: "+telephonyManager.getNetworkSpecifier()+"\n";
                    result = result+"NetworkCountryIso: "+telephonyManager.getNetworkCountryIso()+"\n";
                    result = result+"NetworkOperator: "+telephonyManager.getNetworkOperator()+"\n";
                    result = result+"NetworkOperatorName: "+telephonyManager.getNetworkOperatorName()+"\n";
                    result = result+"SimCountryIso: "+telephonyManager.getSimCountryIso()+"\n";
                    result = result+"SimOperator: "+telephonyManager.getSimOperator()+"\n";
                    result = result+"SimOperatorName: "+telephonyManager.getSimOperatorName()+"\n";
                    result = result+"SimCarrierId: "+telephonyManager.getSimCarrierId()+"\n";
                    result = result+"SimCarrierIdName: "+telephonyManager.getSimCarrierIdName()+"\n";
                    result = result+"TimeStamp: "+cellInfo.getTimeStamp()+"\n";
                    Timestamp timestamp = new Timestamp(cellInfo.getTimeStamp());
                    result = result+"Time: "+timestamp.toString()+"\n";
                    try{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            result = result+"TimestampMillis: "+cellInfo.getTimestampMillis()+"\n";
                            Timestamp timestamp2 = new Timestamp(cellInfo.getTimestampMillis());
                            result = result+"Time: "+timestamp2.toString()+"\n";
                        }
                    }catch(Exception e){

                    }

                    if (cellInfo instanceof CellInfoWcdma) {

                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                        CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        result = result+"SignalType: Wcdma\n";
                        result = result+"CellIdentity(Cid): "+cellIdentityWcdma.getCid()+"\n";
                        result = result+"PhysicalScramblingCode(Psc): "+cellIdentityWcdma.getPsc()+"\n";
                        result = result+"MobileCountryCode(Mcc): "+cellIdentityWcdma.getMccString()+"\n";
                        result = result+"MobileNetworkCode(Mnc): "+cellIdentityWcdma.getMncString()+"\n";
                        result = result+"LocationAreaCode(Lac): "+cellIdentityWcdma.getLac()+"\n";
                        result = result+"Uarfcn: "+cellIdentityWcdma.getUarfcn()+"\n";
                        result = result+"SignalStrength(Dbm): "+cellSignalStrengthWcdma.getDbm()+"\n";
                        result = result+"AsuLevel: "+cellSignalStrengthWcdma.getAsuLevel()+"\n";
                        result = result+"SignalLevel: "+cellSignalStrengthWcdma.getLevel()+"\n";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            result = result+"EnergyPerChipOverNoiseDestiny(EcNo): "+cellSignalStrengthWcdma.getEcNo()+"\n";
                        }

                    } else if (cellInfo instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                        CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        result = result+"SignalType: Lte\n";
                        result = result+"CellIdentity(Ci): "+cellIdentityLte.getCi()+"\n";
                        result = result+"PhysicalCellIdentity(Pci): "+cellIdentityLte.getPci()+"\n";
                        result = result+"MobileCountryCode(Mcc): "+cellIdentityLte.getMccString()+"\n";
                        result = result+"MobileNetworkCode(Mnc): "+cellIdentityLte.getMncString()+"\n";
                        result = result+"TrackingAreaCode(Tac): "+cellIdentityLte.getTac()+"\n";
                        result = result+"Earfcn: "+cellIdentityLte.getEarfcn()+"\n";
                        result = result+"AsuLevel: "+cellSignalStrengthLte.getAsuLevel()+"\n";
                        result = result+"SignalLevel: "+cellSignalStrengthLte.getLevel()+"\n";
                        result = result+"SignalStrength(Dbm): "+cellSignalStrengthLte.getDbm()+"\n";
                        result = result+"ChannelQualityIndicator(Cqi): "+cellSignalStrengthLte.getCqi()+"\n";
                        result = result+"ReferenceSignalReceivedPower(Rsrp): " +cellSignalStrengthLte.getRsrp()+"\n";
                        result = result+"ReferenceSignalReceivedQuality(Rsrq): " +cellSignalStrengthLte.getRsrq()+"\n";
                        result = result+"ReferenceSignalToNoiseRadio(Rssnr): " +cellSignalStrengthLte.getRssnr()+"\n";
                        try{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                result = result+"ReceivedSignalStrengthIndicator(Rssi): "+cellSignalStrengthLte.getRssi()+"\n";
                            }
                        }catch(Exception e){

                        }
                        result = result+"TimingAdvance: " +cellSignalStrengthLte.getTimingAdvance()+"\n";
                    } else if (cellInfo instanceof CellInfoGsm) {
                        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
                        CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                        result = result+"SignalType: Gsm\n";
                        result = result+"CellIdentity(Cid): "+cellIdentityGsm.getCid()+"\n";
                        result = result+"BaseStationIdentityCode(Bsic): "+cellIdentityGsm.getBsic()+"\n";
                        result = result+"MobileCountryCode(Mcc): "+cellIdentityGsm.getMccString()+"\n";
                        result = result+"MobileNetworkCode(Mnc): "+cellIdentityGsm.getMncString()+"\n";
                        result = result+"LocationAreaCode(Lac): "+cellIdentityGsm.getLac()+"\n";
                        result = result+"Aarfcn: "+cellIdentityGsm.getArfcn()+"\n";
                        result = result+"SignalStrength(Dbm): "+cellSignalStrengthGsm.getDbm()+"\n";
                        result = result+"AsuLevel: "+cellSignalStrengthGsm.getAsuLevel()+"\n";
                        result = result+"SignalLevel: "+cellSignalStrengthGsm.getLevel()+"\n";
                        try{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                result = result+"ReceivedSignalStrengthIndicator(Rssi): "+cellSignalStrengthGsm.getRssi()+"\n";
                            }
                        }catch(Exception e){

                        }
                        result = result+"TimingAdvance: "+cellSignalStrengthGsm.getTimingAdvance()+"\n";
                    }
                }
            }

            return result;
        }catch(Exception e){
            return e.getMessage();
        }
    }

    private  String getIMEIDeviceId(Context context) {

        String deviceId;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
            }
            assert telephonyManager != null;
            if (telephonyManager.getDeviceId() != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    deviceId = telephonyManager.getImei();
                }else {
                    deviceId = telephonyManager.getDeviceId();
                }
            } else {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }
        Log.d("deviceId", deviceId);
        return deviceId;
    }

    private void requestLocationUpdates() {

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            return ;
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                MainActivity.this.locationValue = "Lat: "+location.getLatitude()+"\nLng: "+location.getLongitude();
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,5, locationListener,Looper.getMainLooper());
    }


    private void requestSingleLocation() {


        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            return ;
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                //MainActivity.this.locationValue = "Lat: "+location.getLatitude()+"\nLng: "+location.getLongitude();
            }
        };

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,locationListener,Looper.getMainLooper());
        Location location = locationManager.getLastKnownLocation("gps");
        locationValue = "Lat: "+location.getLatitude()+"\nLng: "+location.getLongitude();
        //Toast.makeText(this, "location"+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_LONG).show();

    }

    private String getCurrentLocation() {
        String locationValue="";
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            return "";
        }

        Executor executor = new Executor() {
            @Override
            public void execute(Runnable runnable) {

            }
        };
        Consumer<Location> locationConsumer = new Consumer<Location>() {
            @Override
            public void accept(Location location) {

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.getCurrentLocation("gps", null, executor, locationConsumer);
            Location location2 = locationManager.getLastKnownLocation("gps");
            locationValue = "Lat: " + location2.getLatitude() + "\nLng: " + location2.getLongitude();
            return locationValue;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocationRequest locationRequest = new LocationRequest.Builder(LocationRequest.QUALITY_HIGH_ACCURACY).build();
            locationManager.getCurrentLocation("gps", locationRequest, null, executor, locationConsumer);
            Location location2 = locationManager.getLastKnownLocation("gps");
            locationValue = "Lat: " + location2.getLatitude() + "\nLng: " + location2.getLongitude();
            return locationValue;
        }
        return locationValue;
    }
}