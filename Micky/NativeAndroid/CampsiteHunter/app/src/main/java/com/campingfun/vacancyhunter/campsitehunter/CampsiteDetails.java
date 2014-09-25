package com.campingfun.vacancyhunter.campsitehunter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.campingfun.vacancyhunter.campsitehunter.Utils.ToastHelper;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import com.campingfun.vacancyhunter.campsitehunter.Entity.ReserveInfo;
import com.microsoft.windowsazure.mobileservices.*;
import android.widget.GridView;

import com.campingfun.vacancyhunter.campsitehunter.Active.Async_FindCampgroundNearName;
import com.campingfun.vacancyhunter.campsitehunter.Active.Async_GetCampgroundDetails;
import com.campingfun.vacancyhunter.campsitehunter.Entity.Campground;
import com.campingfun.vacancyhunter.campsitehunter.Entity.GooglePlace;
import com.campingfun.vacancyhunter.campsitehunter.Entity.ShowCampgroundsEvent;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import de.greenrobot.event.EventBus;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;


public class CampsiteDetails extends Activity {
    private static final String TAG = CampsiteDetails.class.getSimpleName();
    private String name;
    private ProgressDialog progressDialog;
    public MobileServiceClient mClient;
    public static final String SENDER_ID = "940228291478";
    public static final String PARAM_CAMPGROUND = "PARAM_CAMPGROUND";
    private ExpandableHeightGridView gridView = null;
    ImageGridViewAdapter imageGridViewAdapter = null;
    private Campground campground;

    public CampsiteDetails() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        try {
            mClient = new MobileServiceClient(
                    "https://camphunter.azure-mobile.net/",
                    "tpwhypNcODenePPliEogupKtnJTaAw44",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);

        setContentView(R.layout.activity_campsite_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String parkId = extras.getString("ParkId");
            if (parkId != null) {
                // coming from notification hub
                campground = new Campground();
                campground.setContractId(extras.getString("ContractId"));
                campground.setId(parkId);
                campground.setName(extras.getString("Name"));
            }
            else
            {
                campground = (Campground) extras.get(PARAM_CAMPGROUND);
            }
        }

//        saveReserveInfo(campground, new GregorianCalendar());
        ArrayList<String> urls = new ArrayList<String>();
        urls.add("http://www.reserveamerica.com//webphotos/CO/pid50032/1/540x360.jpg");
        urls.add("http://www.reserveamerica.com//webphotos/CO/pid50032/2/540x360.jpg");
        urls.add("http://www.reserveamerica.com//webphotos/CO/pid50032/3/540x360.jpg");
        urls.add("http://www.reserveamerica.com//webphotos/CO/pid50032/4/540x360.jpg");
        urls.add("http://www.reserveamerica.com//webphotos/CO/pid50032/5/540x360.jpg");
        urls.add("http://www.reserveamerica.com//webphotos/CO/pid50032/6/540x360.jpg");

        progressDialog = new ProgressDialog(this);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imageGridViewAdapter.getItem(position)), "image/*");
                startActivity(intent);
            }
        });
        if (campground != null) {
            loadCampground(campground);
        }
    }

    private void saveReserveInfo(Campground campground, GregorianCalendar date) {
        ReserveInfo item = new ReserveInfo();
        item.ContractId = campground.getContractId();
//        item.ContractType = campground.getContractType();
        item.ParkId = campground.getId();
        item.Name = campground.getName();
        item.DateInterested = date.getTime();
//        item.setHandle(MyHandler.getHandle());

        mClient.getTable(ReserveInfo.class).insert(item, new TableOperationCallback<ReserveInfo>() {
            public void onCompleted(ReserveInfo entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    Log.i(TAG, "Insert successful");
                } else {
                    Log.i(TAG, "Insert failed with " + exception.toString());
                }
            }
        });

        ToastHelper.showToast(this, "Add availability alert for " + campground.getName() + " on " + DateFormat.getDateInstance().format(date.getTime()));
    }

    private void loadCampground(Campground campground) {
        progressDialog.show();
        new Async_GetCampgroundDetails().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, campground);
    }

    public void onEventMainThread(Event_ShowCampgroundDetails event) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        fillContent(event.getCampground());
    }

    public void onEvent(Event_AvailabilityAlertDateSelected event) {
        this.saveReserveInfo(campground, event.getDate());
    }

    private void fillContent(Campground campground)
    {
        addTextCard(campground.getName(), campground.getDescription(), R.id.details_overview);
        imageGridViewAdapter = new ImageGridViewAdapter(this, campground.getImgUrls_PhotoArray());
        gridView.setExpanded(true);
        gridView.setAdapter(imageGridViewAdapter);
    }

    private void addTextCard(String header, String content, int viewId) {

        //Create a Card
        Card card = new Card(this, R.layout.details_card_section);

        //Create a CardHeader
        CardHeader cardHeader = new CardHeader(this);
        card.addCardHeader(cardHeader);
        cardHeader.setTitle(header);
        card.setTitle(content);

        //Set card in the cardView
        CardView cardView = (CardView) this.findViewById(viewId);
        cardView.setCard(card);
    }

    private void init_overview() {

        //Create a Card
        Card card = new Card(this, R.layout.details_card_section);

        //Create a CardHeader
        CardHeader header = new CardHeader(this);

        //Set the header title
        header.setTitle(name);

        card.addCardHeader(header);

        //Set the card inner text
        card.setTitle("Mountain ranier is awuersomefdsfdsf dsfdsflkds fdsf jdslkfjdsf d fjdslkfjdks lfjds lf dsfdsfds");

        //Set card in the cardView
        CardView cardView = (CardView) this.findViewById(R.id.details_overview);
        cardView.setCard(card);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.campsite_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_alertAction) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
