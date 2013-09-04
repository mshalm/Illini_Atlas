package iefx.testing.myapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

public class NoPath extends Activity {
	
	int indexValue;
	int buildingNumber;

	
public class SpinnerActivity extends Activity implements OnItemSelectedListener {
	    
	    public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	    	indexValue = pos;
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
		setContentView(R.layout.activity_no_path);
		
        Intent intent = getIntent();
        String buildingNumberString = intent.getStringExtra(BuildingSearch.EXTRA_MESSAGE);
        buildingNumber = Integer.parseInt(buildingNumberString);
        
		String[] floorChars = {"B","1","2","3", "4","5","6","7","8","9"};
	    InputStream InPutFinal = null;
	    String floorSelected;
	    ArrayList<String> spinnerPopulator = new ArrayList<String>();
	    
		

		
		for(int i=0; i<9; i++){
			try {
				InPutFinal = getResources().getAssets().open("data/" + buildingNumberString + "/" + floorChars[i] +".txt");
	    	
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(InPutFinal!=null){
				BufferedReader Reader = new BufferedReader(new InputStreamReader(InPutFinal));

				//you've now got an instance of BufferedReader called Reader
				try {
					floorSelected = Reader.readLine();
					spinnerPopulator.add(floorSelected);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				i = 9;
			}
		}
		
		Spinner spinner = (Spinner) findViewById(R.id.spinnerFloorNumber);
		// Create an ArrayAdapter using the string array and a default spinner layout
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerPopulator);
	    // Specify the layout to use when the list of choices appears
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    // Apply the adapter to the spinner
	    spinner.setAdapter(adapter);
	    spinner.setOnItemSelectedListener(new SpinnerActivity());
		
		
		
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
		getMenuInflater().inflate(R.menu.no_path, menu);
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
		setContentView(R.layout.activity_bitmap_display);
		String[] floorChars2 = {"B","1","2","3", "4","5","6","7","8","9"};
		
        InputStream buildinginfo = null;
        Bitmap beginningFloorTemp = null;
        try {
			
			buildinginfo = getResources().getAssets().open("data/"+buildingNumber+"/"+floorChars2[indexValue]+".png");
			beginningFloorTemp = BitmapFactory.decodeStream(buildinginfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        TouchImageView imageView = (TouchImageView) findViewById(R.id.touchImageView1);
		Bitmap bmp = beginningFloorTemp;
		imageView.setImageBitmap(bmp);
	}

}
