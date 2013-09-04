package iefx.testing.myapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;


public class BitmapDisplay extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show the Up button in the action bar.
		setupActionBar();
		Intent intent = getIntent();
        String compilation = intent.getStringExtra(BuildingSearch.EXTRA_MESSAGE);
        int BIDnumber = compilation.indexOf("$");
        int SFIDnumber = compilation.indexOf("%");
        int EFIDnumber = compilation.indexOf("#");
        int SCnumber = compilation.indexOf("^");
        int StartBreak = compilation.indexOf("z");
        int EndBreak = compilation.indexOf("y");
        
        String buildingID = compilation.substring(0, BIDnumber);
        String StartFloorID = compilation.substring(BIDnumber+1, SFIDnumber);
        String EndFloorID = compilation.substring(SFIDnumber+1,EFIDnumber);
        String StartCoordx = compilation.substring(EFIDnumber+1, StartBreak);
        String StartCoordy = compilation.substring(StartBreak+1, SCnumber);
        String EndCoordx = compilation.substring(SCnumber+1, EndBreak);
        String EndCoordy = compilation.substring(EndBreak+1);
        
        Point StartCoordinates = new Point(Integer.parseInt(StartCoordx),Integer.parseInt(StartCoordy));
        Point EndCoordinates = new Point(Integer.parseInt(EndCoordx), Integer.parseInt(EndCoordy));
        
        ArrayList<Bitmap> floors = new ArrayList<Bitmap>();
        InputStream buildinginfo = null;
        Bitmap beginningFloorTemp = null;
        try {
			
			buildinginfo = getResources().getAssets().open("data/"+buildingID+"/"+StartFloorID+"_"+".png");
			beginningFloorTemp = BitmapFactory.decodeStream(buildinginfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        floors.add(beginningFloorTemp);
        try {
			
			buildinginfo = getResources().getAssets().open("data/"+buildingID+"/"+StartFloorID+".png");
			beginningFloorTemp = BitmapFactory.decodeStream(buildinginfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        floors.add(beginningFloorTemp);
        String floorName = "";
        if (StartFloorID.equals(EndFloorID)==false){
        	try {
    			
    			buildinginfo = getResources().getAssets().open("data/"+buildingID+"/"+EndFloorID+"_"+".png");
    			beginningFloorTemp = BitmapFactory.decodeStream(buildinginfo);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            floors.add(beginningFloorTemp);
            try {
    			
    			buildinginfo = getResources().getAssets().open("data/"+buildingID+"/"+EndFloorID+".png");
    			beginningFloorTemp = BitmapFactory.decodeStream(buildinginfo);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            floors.add(beginningFloorTemp);
            InputStream InPut = null;
            try {
				InPut = getResources().getAssets().open("data/" + buildingID + "/" + EndFloorID +".txt");
	    	
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(InPut!=null){
				BufferedReader Reader = new BufferedReader(new InputStreamReader(InPut));

				//you've now got an instance of BufferedReader called myDIS
				try {
					floorName = Reader.readLine();
					if (floorName.indexOf("(")!=-1){
					floorName = floorName.substring(0, floorName.indexOf("(")-1);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }
		pathManager path = null;
		try {
			path = new pathManager(floors, StartFloorID, EndFloorID, StartCoordinates, EndCoordinates);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final ArrayList<Bitmap> maps = path.getBitmaps();
		
		String Words="";
		
		if(path.isPossible){
			Words = path.pathlength + "@" + path.pathorigi + "@"+path.pathend;
		} else{
			Words = "Not Tru";
		}
		if(StartFloorID.equals(EndFloorID)){
			setContentView(R.layout.activity_bitmap_display);
			TouchImageView imageView = (TouchImageView) findViewById(R.id.touchImageView1);
			Bitmap bmp = maps.get(0);
			imageView.setImageBitmap(bmp);
		} else {
			setContentView(R.layout.activity_dual_image_display);
			TouchImageView imageView2 = (TouchImageView) findViewById(R.id.touchImageView1);
			TextView textView = (TextView) findViewById(R.id.textView4);
			textView.setText("Take the elevator to the "+floorName+ ".");
			Bitmap bmp = maps.get(0);
			imageView2.setImageBitmap(bmp);
			final Button button = (Button) findViewById(R.id.button1);
	        button.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	setContentView(R.layout.activity_bitmap_display);
	            	TouchImageView imageView = (TouchImageView) findViewById(R.id.touchImageView1);
	     			Bitmap bmp = maps.get(1);
	     			imageView.setImageBitmap(bmp);
	             }
	         });
		}
	
				

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bitmap_display, menu);
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
	
	public Dialog showDialog(String alertmessage, String posibutton) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(alertmessage)
		   .setCancelable(false)
		   .setPositiveButton(posibutton, new DialogInterface.OnClickListener() {
		       public void onClick(DialogInterface dialog, int id) {
		   		BuildingSearch building = new BuildingSearch();
				building.onCreate(null);
		    	 
		       }
		   });
		AlertDialog alert = builder.create();
		return alert;
	}


}
