package iefx.testing.myapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;

public class DisplayMessageActivity extends Activity {
	
	public final static String EXTRA_BUILDINGINFO = "com.example.myfirstapp.MESSAGE";
	ArrayList<String> list = new ArrayList<String>();
	List<String> displayList = new ArrayList<String>();
	String something;
	
	int indexValue;
	
	public class SpinnerActivity extends Activity implements OnItemSelectedListener {
	    
	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    	indexValue = pos;
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	    }
	}

	@SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        //Here goes the code for the searching of the building in the database file.

        // Get the message from the intent
        Intent intent = getIntent();
        String building = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String message = "Search results for \"" + building + "\"";

        // Create the text view
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setTextSize(30);
        textView.setText(message);
       
        InputStream buildinginfo = null;
		try {
			buildinginfo = getResources().getAssets().open("namelist.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader myDIS = new BufferedReader(new InputStreamReader(buildinginfo));

        //you've now got an instance of BufferedReader called myDIS
        
        int x = -1;
        
        for(int i=0;i<101;i++){
        try {
        	something = myDIS.readLine();
			x = something.toLowerCase().indexOf(building.toLowerCase());
			if(x>-1){
	        	list.add(something);
	        }
		} catch (IOException e1) {
			//TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
        }
        
        
        

        for(int i=0 ; i < list.size() ; i++) {
        	displayList.add(list.get(i).substring(5));
        }
        Collections.sort(displayList);
        if(list.size()!=1){
              
        	Spinner spinner = (Spinner) findViewById(R.id.spinnerx);
        	// Create an ArrayAdapter using the string array and a default spinner layout
        	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, displayList);
        	// Specify the layout to use when the list of choices appears
        	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        	// Apply the adapter to the spinner
        	spinner.setAdapter(adapter);
        	spinner.setOnItemSelectedListener(new SpinnerActivity());
        } else {
        	Intent intentSingle = new Intent(this, BuildingSearch.class);
            String buildingStringSingle = list.get(0);
        	intentSingle.putExtra(EXTRA_BUILDINGINFO, buildingStringSingle);
        	startActivity(intentSingle);
        	
        }
        //Display results
        
        //If no, send building to Google Maps(walking directions).
        //If yes, ask if they want room directions (Pop-up, new function)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void sendMessage(View view){
   			Intent intent = new Intent(DisplayMessageActivity.this, BuildingSearch.class);
    	    String buildingString = displayList.get(indexValue);
    	    int startID = buildingString.indexOf("#");
    	    int endID = buildingString.indexOf(")");
    	    buildingString = buildingString.substring(startID+1, endID);
    	    while (buildingString.length()<4){
    	       	buildingString = "0"+buildingString;
    	    }
    	    String buildingStringNew = "";
    	    for (String s: list){
    	       	if (s.indexOf(buildingString)==0){
    	       		buildingStringNew=s;
    	       	}
    	    }
    	    intent.putExtra(EXTRA_BUILDINGINFO, buildingStringNew);
    	    startActivity(intent);
    }
    	
}