package iefx.testing.myapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;

public class BuildingSearch<A> extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	
    public ArrayList<String> floorIndices = new ArrayList<String>();
	public String buildingInfoSmall;
	public String buildingID;
	public int indexValue, indexValue2, indexValue3;
	ArrayList<String> rooms = new ArrayList<String>();
	ArrayList<String> roomsInBuilding = new ArrayList<String>();
	
	public class SpinnerActivity extends Activity implements OnItemSelectedListener {
	    
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	indexValue = pos;
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }
	}

public class SpinnerActivity2 extends Activity implements OnItemSelectedListener {
	    
	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	indexValue2 = pos;
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }
	}

public class SpinnerActivity3 extends Activity implements OnItemSelectedListener {
    
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    	indexValue3 = pos;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show the Up button in the action bar.
		setupActionBar();
		
        Intent intent = getIntent();
        String buildingInfo = intent.getStringExtra(DisplayMessageActivity.EXTRA_BUILDINGINFO);
        int x = buildingInfo.indexOf("(");
        buildingInfoSmall = buildingInfo.substring(5,x);
        buildingID = buildingInfo.substring(0,4);
		
		
        // Get the message from the intent
        setContentView(R.layout.activity_building_search);
        


	    String[] floorChars = {"B","1","2","3", "4","5","6","7","8","9"};
	    InputStream InPut = null;
	    String floorNames = "test";
	    ArrayList<String> floorsInBuilding = new ArrayList<String>();

		
		for(int i=0; i<9; i++){
			try {
				InPut = getResources().getAssets().open("data/" + buildingID + "/" + floorChars[i] +".txt");
	    	
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(InPut!=null){
				BufferedReader Reader = new BufferedReader(new InputStreamReader(InPut));

				//you've now got an instance of BufferedReader called myDIS
				try {
					floorNames = Reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (floorNames!=null){
				floorsInBuilding.add(floorNames);
				floorIndices.add(floorChars[i]);
				}
				TestForEmpty:
				while(true){
					try {
						String tempString = Reader.readLine();
						if((tempString == null)||(tempString.equals(""))||(tempString.equals(" "))){
							break TestForEmpty;
						}
						tempString = floorChars[i] + "&" + tempString;
						int g = tempString.indexOf(" ");
						String subSomething = tempString.substring(g+1);
						roomsInBuilding.add(subSomething);
						rooms.add(tempString);
					} catch (IOException e1) {
						//TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		Collections.sort(roomsInBuilding);
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner2);
		// Create an ArrayAdapter using the string array and a default spinner layout
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomsInBuilding);
	    // Specify the layout to use when the list of choices appears
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new SpinnerActivity());
	    
		Spinner spinner2 = (Spinner) findViewById(R.id.spinnerFloorNumber);
		// Create an ArrayAdapter using the string array and a default spinner layout
	    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomsInBuilding);
	    // Specify the layout to use when the list of choices appears
	    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    spinner2.setAdapter(adapter2);
	    spinner2.setOnItemSelectedListener(new SpinnerActivity2());
	    
	    Spinner spinner3 = (Spinner) findViewById(R.id.spinnerAllFloors);
		// Create an ArrayAdapter using the string array and a default spinner layout
	    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, floorsInBuilding);
	    // Specify the layout to use when the list of choices appears
	    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    spinner3.setAdapter(adapter3);
	    spinner3.setOnItemSelectedListener(new SpinnerActivity3());
        
        
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


public void sendMessage(View view){
	String RoomStart = roomsInBuilding.get(indexValue);
	String RoomEnd = roomsInBuilding.get(indexValue2);
	for (String s: rooms){
		if(s.substring(s.indexOf(" ")+1).equals(RoomStart)){
			RoomStart=s;
		}
		if(s.substring(s.indexOf(" ")+1).equals(RoomEnd)){
			RoomEnd=s;
		}
	}
	
	
	
	String StartFloorID = RoomStart.substring(0,1);
	String EndFloorID = RoomEnd.substring(0,1);
	
	int comma = RoomStart.indexOf(",");
	String Starta = RoomStart.substring(2,comma);
	RoomStart = RoomStart.substring(comma+1);
	comma = RoomStart.indexOf(",");
	String Startb = RoomStart.substring(0,comma);
	RoomStart = RoomStart.substring(comma+1);
	comma = RoomStart.indexOf(",");
	String Startc = RoomStart.substring(0,comma);
	RoomStart = RoomStart.substring(comma+1);
	comma = RoomStart.indexOf(" ");
	String Startd = RoomStart.substring(0,comma);
	RoomStart = RoomStart.substring(comma+1);
	
	int a = Integer.parseInt(Starta);
	int b = Integer.parseInt(Startb);
	int c = Integer.parseInt(Startc);
	int d = Integer.parseInt(Startd);
	
	Point StartCoordinates = new Point((a+c),(1224-(b+d)));
	
	comma = RoomEnd.indexOf(",");
	String Enda = RoomEnd.substring(2,comma);
	RoomEnd = RoomEnd.substring(comma+1);
	comma = RoomEnd.indexOf(",");
	String Endb = RoomEnd.substring(0,comma);
	RoomEnd = RoomEnd.substring(comma+1);
	comma = RoomEnd.indexOf(",");
	String Endc = RoomEnd.substring(0,comma);
	RoomEnd = RoomEnd.substring(comma+1);
	comma = RoomEnd.indexOf(" ");
	String Endd = RoomEnd.substring(0,comma);
	RoomEnd = RoomEnd.substring(comma+1);
	
	int a2 = Integer.parseInt(Enda);
	int b2 = Integer.parseInt(Endb);
	int c2 = Integer.parseInt(Endc);
	int d2 = Integer.parseInt(Endd);
	
	Point EndCoordinates = new Point((a2+c2),(1224-(b2+d2)));
	
	String StartCood = StartCoordinates.x + "z" + StartCoordinates.y;
	String EndCood = EndCoordinates.x + "y" + EndCoordinates.y;
	
	String compilation = buildingID + "$" + StartFloorID + "%" + EndFloorID + "#" + StartCood + "^" + EndCood; 
	
	Intent myIntent = new Intent(BuildingSearch.this, BitmapDisplay.class);
	myIntent.putExtra(EXTRA_MESSAGE, compilation);
	startActivity(myIntent);
	
	}

	public void getFloorPlans(View view){
		setContentView(R.layout.activity_bitmap_display);
		String index = floorIndices.get(indexValue3);
		InputStream buildinginfo = null;
		Bitmap beginningFloorTemp = null;
		try {
			buildinginfo = getResources().getAssets().open("data/"+buildingID+"/"+index+".png");
			beginningFloorTemp = BitmapFactory.decodeStream(buildinginfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TouchImageView imageView = (TouchImageView) findViewById(R.id.touchImageView1);
		imageView.setImageBitmap(beginningFloorTemp);
		
	}
	
	public void MapNav(View view){
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+buildingInfoSmall+ "&mode=w"));
		startActivity(intent);
	}

}