package iefx.testing.myapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;

public class pathManager extends Activity { 
	private static final int DARK_HALLWAY = Color.rgb(208,207,208);
	private static final int LIGHT_HALLWAY = Color.rgb(208,207,208);
	private static final int DARK_WALL = Color.rgb(104,104,104);
	private static final int LIGHT_WALL = Color.rgb(130,130,130);
	private static final int DARK_ELEVATOR = Color.rgb(0,0,254);
	private static final int LIGHT_ELEVATOR = Color.rgb(100,100,255);
	private static final int MIN_ELEVATOR_SIZE = 13;
	private static final int MAX_ELE_HEIGHT_DEFICIT = 2;
	private static final int IMG_HEIGHT = 306;
	private static final int IMG_WIDTH = 396;
	private static final byte HALLWAY_ERROR = 1;
	private static final int PATH_WIDTH = 8;
	private boolean isOnSameFloor;
	private byte[][] beginningMap;
	private byte[][] endMap;
	private ArrayList<Point> beginningElevators;
	private ArrayList<Byte> beginningElevatorIndices;
	private ArrayList<Point> endElevators;
	private ArrayList<Byte> endElevatorIndices;
	private Bitmap beginningFloor;
	private Bitmap beginningFloorDrawable;
	private Bitmap endFloor;
	private Bitmap endFloorDrawable;
	public boolean isPossible;
	private byte maxBeginningElevatorIndex;
	public String pathlength;
	public String pathorigi;
	public String pathend;
	
	public pathManager(ArrayList<Bitmap> bitmaps, String beginningFloorNum, String endFloorNum, Point beginningRoom, Point endRoom) throws FileNotFoundException{
		 //florin was here
		beginningRoom = new Point((beginningRoom.x/4),(beginningRoom.y/4));
		endRoom = new Point((endRoom.x/4),(endRoom.y/4));
		maxBeginningElevatorIndex = 1;
		beginningElevators = new ArrayList<Point>();
		beginningElevatorIndices = new ArrayList<Byte>();
		Bitmap beginningFloorTemp = bitmaps.get(0);
		beginningFloor = beginningFloorTemp.copy(beginningFloorTemp.getConfig(), true);
		bitmaps.get(0).recycle();
		beginningFloorTemp = bitmaps.get(1);
		beginningFloorDrawable = beginningFloorTemp.copy(beginningFloorTemp.getConfig(), true);
		bitmaps.get(1).recycle();
		beginningMap = new byte[IMG_WIDTH][IMG_HEIGHT];
		isPossible = false;	
		boolean existanceOfElevators = elevatorMapper(beginningFloor, beginningMap, beginningElevators, beginningElevatorIndices);
		isOnSameFloor = (beginningFloorNum.equals(endFloorNum));
		if ((!existanceOfElevators)&&(!isOnSameFloor)){
			isPossible = false;
			return;
		}
		if(isOnSameFloor){//must address halls not initialized by elevators
			ArrayList<Byte> originHalls = new ArrayList<Byte>();
			ArrayList<Point> originConnect = new ArrayList<Point>();
			ArrayList<Byte> endHalls = new ArrayList<Byte>();
			ArrayList<Point> endConnect = new ArrayList<Point>();
			int i = 0;
			while(areDisjoint(originHalls, endHalls)&&(i<10)){
				originConnect.add(colorSearch(beginningFloor, beginningMap, beginningRoom, DARK_HALLWAY, LIGHT_HALLWAY, originHalls));
				if (beginningMap[originConnect.get(i).x][originConnect.get(i).y]==(byte) 0){
					ArrayList<Point> newOrigin = new ArrayList<Point>();
					newOrigin.add(originConnect.get(i));
					ArrayList<Byte> newIndex = new ArrayList<Byte>();
					newIndex.add((byte) 0);
					beginningMap[originConnect.get(i).x][originConnect.get(i).y]=maxBeginningElevatorIndex;
					floodColor(beginningFloor, beginningMap, newOrigin, newIndex, HALLWAY_ERROR, DARK_HALLWAY, LIGHT_HALLWAY, maxBeginningElevatorIndex);
					maxBeginningElevatorIndex++;
				}
				originHalls.add(beginningMap[originConnect.get(i).x][originConnect.get(i).y]);
				endConnect.add(colorSearch(beginningFloor, beginningMap, endRoom, DARK_HALLWAY, LIGHT_HALLWAY, endHalls));
				if (beginningMap[endConnect.get(i).x][endConnect.get(i).y]==(byte) 0){
					ArrayList<Point> newEnd = new ArrayList<Point>();
					newEnd.add(endConnect.get(i));
					ArrayList<Byte> newIndexEnd = new ArrayList<Byte>();
					newIndexEnd.add((byte) 0);
					beginningMap[endConnect.get(i).x][endConnect.get(i).y]=maxBeginningElevatorIndex;
					floodColor(beginningFloor, beginningMap, newEnd, newIndexEnd, HALLWAY_ERROR, DARK_HALLWAY, LIGHT_HALLWAY, maxBeginningElevatorIndex);
					maxBeginningElevatorIndex++;
				}
				endHalls.add(beginningMap[endConnect.get(i).x][endConnect.get(i).y]);
				i++;
			}
			if (areDisjoint(originHalls, endHalls)==false){
				ArrayList<Byte> intersect = findIntersect(originHalls, endHalls);
				//
				//
				int z = intersect.get(0);
				ArrayList<Point> path = coordinatePath(beginningMap, originConnect.get(intersect.get(0).byteValue()), endConnect.get(intersect.get(1).byteValue()), endHalls.get(intersect.get(1).byteValue()).byteValue());
				pathlength = (new Integer(path.size())).toString();
				pathend = originConnect.get(intersect.get(0).byteValue()).x +" "+originConnect.get(intersect.get(0).byteValue()).y;
				pathorigi = endConnect.get(intersect.get(1).byteValue()).x +" "+endConnect.get(intersect.get(1).byteValue()).y;
				paintPath(path, beginningFloorDrawable);
				isPossible = true;
			} else {isPossible = false; return;}
		} else {
			endElevators = new ArrayList<Point>();
			endElevatorIndices = new ArrayList<Byte>();
			Bitmap endFloorTemp = bitmaps.get(2);
			endFloor = endFloorTemp.copy(endFloorTemp.getConfig(), true);
			bitmaps.get(2).recycle();
			endFloorTemp = bitmaps.get(3);
			endFloorDrawable = endFloorTemp.copy(endFloorTemp.getConfig(), true);
			bitmaps.get(3).recycle();
			endMap = new byte[IMG_WIDTH][IMG_HEIGHT];
			elevatorMapper(endFloor, endMap, endElevators, endElevatorIndices);
			ArrayList<Byte> originHalls = new ArrayList<Byte>();
			ArrayList<Point> originEles = new ArrayList<Point>();
			ArrayList<Point> originConnect = new ArrayList<Point>();
			ArrayList<Byte> endHallMatches = new ArrayList<Byte>();
			ArrayList<Byte> endHalls = new ArrayList<Byte>();
			ArrayList<Point> endEles = new ArrayList<Point>();
			ArrayList<Point> endConnect = new ArrayList<Point>();
			int i = 0;
			while(areDisjoint(originHalls, endHallMatches)&&(i<1)){
				Point start = colorSearch(beginningFloor, beginningMap, beginningRoom, DARK_HALLWAY, LIGHT_HALLWAY, originHalls);
				if (beginningMap[start.x][start.y]==(byte) 0){
					isPossible = false;
					return;
				}
				for (byte b = 0; b<beginningElevatorIndices.size(); b++){
					if (beginningElevatorIndices.get(b).byteValue()==beginningMap[start.x][start.y]){
						originConnect.add(start);
						originEles.add(beginningElevators.get(b));
						originHalls.add(beginningMap[start.x][start.y]);
					}
				}
				Point end = colorSearch(endFloor, endMap, endRoom, DARK_HALLWAY, LIGHT_HALLWAY, endHalls);
				if (endMap[end.x][end.y]==(byte) 0){
					isPossible = false;
					return;
				}
				for (byte b = 0; b<endElevatorIndices.size(); b++){
					if (endElevatorIndices.get(b).byteValue()==endMap[end.x][end.y]){
						endConnect.add(end);
						endEles.add(endElevators.get(b));
						Point nearestOnBeginningFloor = findClosest(endElevators.get(b), beginningElevators);
						int matchIndex = beginningElevators.indexOf(nearestOnBeginningFloor);
						endHallMatches.add(beginningElevatorIndices.get(matchIndex));
						endHalls.add(endMap[end.x][end.y]);
					}
				}
				i++;
			}
			if (areDisjoint(originHalls, endHallMatches)==false){
				ArrayList<Point> intersect = findAllIntersects(originHalls, endHallMatches);
				ArrayList<Point> pathBegin = null;
				ArrayList<Point> pathEnd = null;
				ArrayList<Point> tempathBegin = null;
				ArrayList<Point> tempathEnd = null;
				int minPathLength = Integer.MAX_VALUE;
				for (int p = 0;p<intersect.size();p++){
					int z = intersect.get(p).x;
					int q = intersect.get(p).y;
					tempathBegin = coordinatePath(beginningMap, originConnect.get(z), originEles.get(z), originHalls.get(z));
					tempathEnd = coordinatePath(endMap, endEles.get(q), endConnect.get(q), endHalls.get(q));
					boolean truePath = (originEles.get(z).equals(findClosest(endEles.get(q), originEles)))&&(endEles.get(q).equals(findClosest(originEles.get(z), endEles)));
					if ((tempathBegin.size()+tempathEnd.size())<minPathLength&&truePath){
							pathBegin = tempathBegin;
							pathEnd = tempathEnd;
							minPathLength = pathBegin.size()+pathEnd.size();
					}
				}
				
				paintPath(pathBegin, beginningFloorDrawable);
				paintPath(pathEnd, endFloorDrawable);
				isPossible = true;
			} else {isPossible = false; return;}
		}
		
	}
	
	public ArrayList<Bitmap> getBitmaps(){
		ArrayList<Bitmap> maps = new ArrayList<Bitmap>();
		maps.add(beginningFloorDrawable);
		if (isOnSameFloor==false){
			maps.add(endFloorDrawable);
		}
		return maps;
	}
	public ArrayList<Byte> findIntersect(ArrayList<Byte> a, ArrayList<Byte> b){
		for(byte i=0;i<a.size();i++){
			for (byte j=0;j<b.size();j++){
				if (a.get(i).equals(b.get(j))){
					ArrayList<Byte> indices = new ArrayList<Byte>();
					indices.add(i);
					indices.add(j);
					return indices;
				}
			}
		}
		return null;
	}
	
	public ArrayList<Point> findAllIntersects(ArrayList<Byte> a, ArrayList<Byte> b){
		ArrayList<Point> indices = new ArrayList<Point>();
		for(byte i=0;i<a.size();i++){
			for (byte j=0;j<b.size();j++){
				if (a.get(i).equals(b.get(j))){
					indices.add(new Point(i,j));
				}
			}
		}
		return indices;
	}
	
	public boolean areDisjoint(ArrayList<Byte> a, ArrayList<Byte> b){
		for(Byte aByte: a){
			for(Byte bByte: b){
				if (aByte.equals(bByte)){
					return false;
				}
			}
		}
		return true;
	}
	
	public void floodColor(Bitmap floorMap, byte floor[][], List<Point> origins, List<Byte> indices, byte errorAllowance, int darkColor, int lightColor, byte fillNum) {
		List<Point> newOrigins = new ArrayList<Point>();
		while (origins.isEmpty()==false) {
			for(int j=0; j<origins.size(); j++){
				floor[origins.get(j).x][origins.get(j).y]=fillNum;//fills in fillNum to all of the origins
			}
			ArrayList<Point> adjacents = new ArrayList<Point>();//list of points adjacent to the member of origins
			for(int i=0; i<origins.size(); i++){
					 
					adjacents.add(new Point((origins.get(i).x) + 1, origins.get(i).y));
					adjacents.add(new Point((origins.get(i).x) - 1, origins.get(i).y));
					adjacents.add(new Point((origins.get(i).x), (origins.get(i).y)+1));
					adjacents.add(new Point((origins.get(i).x), (origins.get(i).y)-1));
			}
				for (Point adj : adjacents) {
					if ((floor[adj.x][adj.y]!=fillNum)&&(floor[adj.x][adj.y]>=0)){ //adds adjacent point iff it has not already been initialized and adjacent point is not a negative marker
						if (isIntermediateColor(floorMap.getPixel(adj.x, adj.y), darkColor, lightColor)){
							newOrigins.add(adj);
						}// else{
							//newIndices.add((byte) (indices.get(i).byteValue()+1));
						//}
					}	
				}
			origins = newOrigins;
			newOrigins = new ArrayList<Point>();
		}
	}
	
	public Point colorSearch(Bitmap floorMap, byte map[][], Point origin, int darkColor, int lightColor, ArrayList<Byte> unacceptable) {
		ArrayList<Point> colorMatches = new ArrayList<Point>();
		int radius = 1; //radius of the search diamond
		while(radius<=floorMap.getHeight()){
			for (int i=1;i<=radius;i++){
				int j = radius - i;
				ArrayList<Point> four = new ArrayList<Point>();
				four.add(new Point(origin.x-i,origin.y+j));
				four.add(new Point(origin.x+j,origin.y+i));
				four.add(new Point(origin.x+i,origin.y-j));
				four.add(new Point(origin.x-j,origin.y-i));
				for (Point z: four){
					if ((z.x>=0)&&(z.y>=0)&&(z.x<IMG_WIDTH)&&(z.y<IMG_HEIGHT)){
						if (isIntermediateColor(floorMap.getPixel(z.x,z.y), darkColor, lightColor)){
							colorMatches.add(z);
						}
					}
				}
				boolean isUnacceptable = false;
				for (Point p : colorMatches){//cycles through points of this part of the wave
					for (Byte b : unacceptable){//checks if each point has a value equal to any of the unacceptable ones
						if (map[p.x][p.y] == b.byteValue()){
							isUnacceptable = true;
						}
						
					}
					if (isUnacceptable==false){
						return p; //returns the point of its value is not unacceptable
					}
					isUnacceptable=false;
					
				}
				colorMatches = new ArrayList<Point>();//clears colorMatches for next part of the wave
			}
			radius++;
		}
		return null;
	}
	
	public ArrayList<Point> coordinatePath(byte map[][], Point origin, Point desired, byte medium) {
		//adapted Lee's Algorithm: cycles labels -1,-2,-3,-1,-2,-3....
		boolean desiredReached = false;
		ArrayList<Point> affectedPoints = new ArrayList<Point>();
		ArrayList<Point> wave = new ArrayList<Point>();
		ArrayList<Point> newWave = new ArrayList<Point>();
		int i=1;
		map[origin.x][origin.y] = -1;
		wave.add(origin);
		affectedPoints.add(origin);
		while ((desiredReached==false)&&(i<1300)){ //on each iteration, replaces wave with newWave and assigns a new wave of markers
			if (wave.isEmpty()){
				break;
			}
			for (Point p: wave){
				Point[] adjacents = new Point[4]; //list of points adjacent to origin
				adjacents[0] = new Point((p.x) + 1, p.y);
				adjacents[1] = new Point((p.x) - 1, p.y);
				adjacents[2] = new Point((p.x), (p.y)+1);
				adjacents[3] = new Point((p.x), (p.y)-1);
				for (Point s: adjacents){
					if (map[s.x][s.y]==medium) {
						newWave.add(s);
						affectedPoints.add(s);
						map[s.x][s.y]=(byte) (-1-(i%3));
					}
				}
			}
			for(Point q: newWave){
				if (q.equals(desired)){
					desiredReached=true;
				}
			}
			wave=newWave;
			newWave=null;
			newWave=new ArrayList<Point>();
			i++;
		}
		i--;
		i--;
		ArrayList<Point> path = new ArrayList<Point>();
		path.add(desired);
		int z = i + 5;
		while ((!path.get(path.size()-1).equals(origin))&&(z>=0)){
			Point[] adjacents = new Point[4];
			Point p = path.get(path.size()-1);
			adjacents[0] = new Point((p.x) + 1, p.y);
			adjacents[1] = new Point((p.x) - 1, p.y);
			adjacents[2] = new Point((p.x), (p.y)+1);
			adjacents[3] = new Point((p.x), (p.y)-1);
			findNextNode:
			for (Point r: adjacents){
				if (map[r.x][r.y]==(byte) (-1-(i%3))){
					path.add(r);
					i--;
					break findNextNode;
				}
			}
			z--;
		}
		for(Point p: affectedPoints){
			map[p.x][p.y]=medium;
		}
		return path;
		
	}
	
	public boolean elevatorMapper(Bitmap floorMap, byte[][] floorArray, ArrayList<Point> elevators, ArrayList<Byte> elevatorIndices) {
		int i, j, left, right;
		for (i=0;i<IMG_HEIGHT;i++){
			for (j=0;j<IMG_WIDTH;j+=MIN_ELEVATOR_SIZE){
				//check if color of point is an elevator color
				if ((isIntermediateColor(floorMap.getPixel(j,i), DARK_ELEVATOR, LIGHT_ELEVATOR))&&((floorArray[j][i])!=(byte) -77)) {
					//get the end points of the horizontal line of elevator color
					left = 0;
					right = 0;
					while(isIntermediateColor(floorMap.getPixel(j-left-1,i), DARK_ELEVATOR, LIGHT_ELEVATOR)){
						left++;
					}
					while(isIntermediateColor(floorMap.getPixel(j+right+1,i), DARK_ELEVATOR, LIGHT_ELEVATOR)){
						right++;
					}
					//check if the bottom corners of supposed elevator box are elevator colored	
					if ((left+right+1)>=MIN_ELEVATOR_SIZE){
						if(isIntermediateColor(floorMap.getPixel(j-left,i-(left+right-MAX_ELE_HEIGHT_DEFICIT)), DARK_ELEVATOR, LIGHT_ELEVATOR)){
							if(isIntermediateColor(floorMap.getPixel(j+right,i-(left+right-MAX_ELE_HEIGHT_DEFICIT)), DARK_ELEVATOR, LIGHT_ELEVATOR)){
								//elevator status confirmed
								//proceed to register as elevator and register available floor area
								Point elevatorOrigin = new Point(j,i);
								ArrayList<Byte> a = new ArrayList<Byte>();
								Point connectedHallway = colorSearch(floorMap, floorArray, elevatorOrigin, DARK_HALLWAY, LIGHT_HALLWAY, a);
								if (floorArray[connectedHallway.x][connectedHallway.y]==(byte) 0){
									//flood hallway
									List<Point> ele = new ArrayList<Point>();
									ele.add(connectedHallway);
									List<Byte> eleIndices = new ArrayList<Byte>();
									eleIndices.add((byte) 0);
									floodColor(floorMap, floorArray, ele, eleIndices, HALLWAY_ERROR, DARK_HALLWAY, LIGHT_HALLWAY, maxBeginningElevatorIndex);
									maxBeginningElevatorIndex++;
								}
								elevators.add(connectedHallway);
								elevatorIndices.add(floorArray[connectedHallway.x][connectedHallway.y]);
								//floorArray[connectedHallway.x][connectedHallway.y] = -76;
								List<Point> blue = new ArrayList<Point>();
								blue.add(elevatorOrigin);
								List<Byte> blueIndices = new ArrayList<Byte>();
								blueIndices.add((byte) 0);
								floodColor(floorMap, floorArray, blue, blueIndices, (byte) 0, DARK_ELEVATOR, LIGHT_ELEVATOR, (byte) -77);
							}
						}
					}	
				}
			}	
		}				
		if (elevators.size() == 0){
			return false;
		} else {
			return true;
		}
	}
	
	public void paintPath(ArrayList<Point> path, Bitmap map){
		for (int p =0;p<path.size();p++){	
			//for ();
			int x = 4*path.get(p).x;
			int y = 4*path.get(p).y;
			int shader = (int) (255*((double)p/(path.size()-1)));
			int color = Color.rgb(0+shader,0+shader/2,255-shader);
			for (int i = (0-PATH_WIDTH);i<=PATH_WIDTH;i++){
				for(int j = (0-PATH_WIDTH);j<=PATH_WIDTH;j++){
					map.setPixel(x+i, y+j, color);
				}
			}
			
		}
	}
	
	public Point findClosest(Point origin, ArrayList<Point> targets) {
		int distances[]= new int[targets.size()];
			for (int i=0;i<distances.length;i++){
				int x = (targets.get(i).x-origin.x);
				int y = (targets.get(i).y-origin.y);
				distances[i] = x*x + y*y;
			}
		int shortestDistance = Integer.MAX_VALUE;
		int shortestIndex = 0;
		for (int i=0;i<distances.length;i++) {
			if (distances[i]<shortestDistance){
				shortestDistance = distances[i];
				shortestIndex = i;
			}
		}
		return targets.get(shortestIndex);
	}
	
	public boolean isIntermediateColor(int testColor, int darkColor, int lightColor){
		if ((Color.blue(testColor)>=Color.blue(darkColor))&&(Color.blue(testColor)<=Color.blue(lightColor))){
			if ((Color.red(testColor)>=Color.red(darkColor))&&(Color.red(testColor)<=Color.red(lightColor))){
				if ((Color.green(testColor)>=Color.green(darkColor))&&(Color.green(testColor)<=Color.green(lightColor))){
					if ((Color.blue(darkColor)==Color.red(darkColor))&&(Color.green(darkColor)==Color.red(darkColor))){
						if ((Color.blue(testColor)==Color.red(testColor))&&(Color.green(testColor)==Color.red(testColor))){
							return true;
						} else {return false;}
					}
					return true;
				} else {return false;}
			} else {return false;}
		} else {return false;}
		
	}
}