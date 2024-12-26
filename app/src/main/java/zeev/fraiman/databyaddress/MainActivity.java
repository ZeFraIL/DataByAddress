package zeev.fraiman.databyaddress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Context context;
    EditText etStreet, etCity, etCountry;
    TextView tvData;
    String stStreet="", stCity="", stCountry="", geoUriString;
    Button bByAdr, bAdrData;
    Uri geoUri;
    Intent map;
    HelperDB helperDB;
    SQLiteDatabase db;
    double lon, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        bByAdr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stStreet=etStreet.getText().toString();
                stCity=etCity.getText().toString();
                stCountry=etCountry.getText().toString();
                geoUriString = "geo:0,0?q="+stStreet+", "+stCity+", "+stCountry+"&z=10";
                geoUri = Uri.parse(geoUriString);
                map = new Intent(Intent.ACTION_VIEW, geoUri);
                startActivity(map);
            }
        });

        bAdrData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stStreet = etStreet.getText().toString();
                stCity = etCity.getText().toString();
                stCountry = etCountry.getText().toString();
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String address = stStreet + ", " + stCity + ", " + stCountry; // Ваш адрес
                List<Address> addresses;

                try {
                    addresses = geocoder.getFromLocationName(address, 1);
                    if (addresses.size() > 0) {
                        lat = addresses.get(0).getLatitude();
                        lon = addresses.get(0).getLongitude();
                        tvData.setText("" + lat + "\n" + lon);
                    } else {
                        tvData.setText("Адрес не найден");
                    }
                    db=helperDB.getReadableDatabase();
                    ContentValues cv=new ContentValues();
                    cv.put(helperDB.DATA_LAT, ""+lat);
                    cv.put(helperDB.DATA_LON, ""+lon);
                    db.insert(helperDB.DATA_TABLE, null, cv);
                    db.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Получите ссылку на вашу базу данных
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                // Получите ссылку на таблицу, в которую вы хотите записать данные
                DatabaseReference myRef = database.getReference("geo_info");

                // Создайте объект с данными, которые вы хотите записать
                LatLng myLatLon = new LatLng(lat,lon);

                // Запишите данные в таблицу
                String stDateTime=date_and_time();
                myRef.child(stDateTime).setValue(myLatLon);
                //myRef.push().setValue(myLatLon);
                Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private String date_and_time() {
        Calendar calendar=Calendar.getInstance();
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int month=calendar.get(Calendar.MONTH)+1;
        int year=calendar.get(Calendar.YEAR);
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);
        return day+"_"+month+"_"+year+"_"+hour+"_"+minute+"_"+second;
    }

    private void initComponents() {
        context=this;
        etStreet=findViewById(R.id.etStreet);
        etCity=findViewById(R.id.etCity);
        etCountry=findViewById(R.id.etCountry);
        bByAdr=findViewById(R.id.bAdr);
        bAdrData= (Button) findViewById(R.id.bAdrData);
        tvData= (TextView) findViewById(R.id.tvData);

        helperDB=new HelperDB(context);
        db=helperDB.getWritableDatabase();
        db.close();
    }
}