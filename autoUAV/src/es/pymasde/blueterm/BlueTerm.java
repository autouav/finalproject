
package es.pymasde.blueterm;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.location.Location;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import com.codeminders.ardrone.ARDrone;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.*;


public class BlueTerm extends Activity implements AdapterView.OnItemSelectedListener {

    // ======================================================== Our Variables ========================================================
    protected static ARDrone drone;                 // on this object we operate all functions of flying and get NavData.
    protected static TextView navDataText;          // in this TextView We update the information that received from the Drone and the GPS.
    protected boolean takeOff = false;              // flag variable.
    protected Button takeOffLand;                   // need to set the text of the Button - takeOff <-> Land.

    protected static String navData[];              // String Object - to display the NavData.
    protected getNavData getND;                     // in this Object we get and set all the NavData from the drone.

    protected Keyboard keyboard;                    // look in Keyboard class for more info.
    protected LinearLayout ll;                      // represents the layout of the Keyboard
    // Objects to set the speed of move function and tilt max angle.
    protected SeekBar speedBar;
    protected TextView speedNum;
    protected SeekBar tiltBar;
    protected TextView tiltNum;

    // Objects to set the sensor sensitivity
    protected SeekBar MaxSenBar;
    protected TextView MaxSenNum;
    protected SeekBar ImmSenBar;
    protected TextView ImmSenNum;

    protected static Thread modeThread;             // this Thread Switch modes according to the algorithm we built.
    protected static Thread moveThread;             // this is very simple Thread - move the drone according to four values

    // objects to show on the App screen - what the drone do now
    protected static TextView moveThreadDo;
    protected static String moveThreadDoString[] = new String[1];

    /*
        this array represents the four values to flight the ARDrone:
        0 ->    (-) left        |       (+) right
        1 ->    (-) front       |       (+) back
        2 ->    (-) down        |       (+) up
        3 ->    (-) yaw-left    |       (+) yaw-right
     */
    protected float move[] = new float[4];
    protected static String bluetooth[] = new String[1];                // An object that represents a stream from the Bluetooth

    /*
        this array represents a number of indicators:
        0 ->    the value that sent to the move function
        1 ->    the maximum tilt angle of the ARDrone
        2 ->    IR maximum value - to set the drone mode in "Immediately Danger"
        3 ->    Sonar maximum value - to identify an obstacle
     */
    protected float indicatArray[] = {5,10,390,200};

    protected Function.droneMode dMode[] = new Function.droneMode[1];   // An object that represents the mode of the ARDrone

    protected LatLng droneLocation;                                   // via GPS that connected to Teensy
    protected static GpsPointContainer gpc;                             // this object save all check-point and the drone location
    protected static GoogleMap map;                                     // map for the App
    protected static MarkerOptions myPosition;                          // represents the ARDrone on the map
    private Spinner mapTypeSpinner;

    // objects to create red line between two GPS points
    protected PolylineOptions rectOptions;
    protected Polyline polyline;
    protected static int marksCount = 0;                                // need to count the marks of the track

    // ===============================================================================================================================

    public static EmulatorView emulatorview;
    public static int connect = 0;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
	public static final int ORIENTATION_SENSOR    = 0;
	public static final int ORIENTATION_PORTRAIT  = 1;
	public static final int ORIENTATION_LANDSCAPE = 2;

    private static TextView mTitle;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    /**
     * Set to true to add debugging code and logging.
     */
    public static final boolean DEBUG = true;

    /**
     * Set to true to log each character received from the remote process to the
     * android log, which makes it easier to debug some kinds of problems with
     * emulating escape sequences and control codes.
     */
    public static final boolean LOG_CHARACTERS_FLAG = DEBUG && false;

    /**
     * Set to true to log unknown escape sequences.
     */
    public static final boolean LOG_UNKNOWN_ESCAPE_SEQUENCES = DEBUG && false;

    /**
     * The tag we use when logging, so that our messages can be distinguished
     * from other messages in the log. Public because it's used by several
     * classes.
     */
	public static final String LOG_TAG = "BlueTerm";

    // Message types sent from the BluetoothReadService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;	

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	private BluetoothAdapter mBluetoothAdapter = null;
	
    /**
     * Our main view. Displays the emulated terminal screen.
     */
    private EmulatorView mEmulatorView;

    /**
     * A key listener that tracks the modifier keys and allows the full ASCII
     * character set to be entered.
     */
    private TermKeyListener mKeyListener;
    private static BluetoothSerialService mSerialService = null;
	private static InputMethodManager mInputManager;
	
	private boolean mEnablingBT;
    private boolean mLocalEcho = false;
    private int mFontSize = 9;
    private int mColorId = 2;
    private int mControlKeyId = 0;
    private boolean mAllowInsecureConnections = true;
    private int mIncomingEoL_0D = 0x0D;
    private int mIncomingEoL_0A = 0x0A;
    private int mOutgoingEoL_0D = 0x0D;
    private int mOutgoingEoL_0A = 0x0A;

    private int mScreenOrientation = 0;

    private static final String LOCALECHO_KEY = "localecho";
    private static final String FONTSIZE_KEY = "fontsize";
    private static final String COLOR_KEY = "color";
    private static final String CONTROLKEY_KEY = "controlkey";
    private static final String ALLOW_INSECURE_CONNECTIONS_KEY = "allowinsecureconnections";
    private static final String INCOMING_EOL_0D_KEY = "incoming_eol_0D";
    private static final String INCOMING_EOL_0A_KEY = "incoming_eol_0A";
    private static final String OUTGOING_EOL_0D_KEY = "outgoing_eol_0D";
    private static final String OUTGOING_EOL_0A_KEY = "outgoing_eol_0A";
    private static final String SCREENORIENTATION_KEY = "screenorientation";

    public static final int WHITE = 0xffffffff;
    public static final int BLACK = 0xff000000;
    public static final int BLUE = 0xff344ebd;

    private static final int[][] COLOR_SCHEMES = {
        {BLACK, WHITE}, {WHITE, BLACK}, {WHITE, BLUE}};

    private static final int[] CONTROL_KEY_SCHEMES = {
        KeyEvent.KEYCODE_DPAD_CENTER,
        KeyEvent.KEYCODE_AT,
        KeyEvent.KEYCODE_ALT_LEFT,
        KeyEvent.KEYCODE_ALT_RIGHT,
        KeyEvent.KEYCODE_VOLUME_DOWN,
        KeyEvent.KEYCODE_VOLUME_UP
    };
//    private static final String[] CONTROL_KEY_NAME = {
//        "Ball", "@", "Left-Alt", "Right-Alt"
//    };
    private static String[] CONTROL_KEY_NAME;
    private int mControlKeyCode;
    private SharedPreferences mPrefs;
    private MenuItem mMenuItemConnect;
    private MenuItem mMenuItemStartStopRecording;
    
    private Dialog         mAboutDialog;


    // ======================================================== Our function =========================================================

    /**
     * This function (Button) makes the ARDrone can fly, if there are problems - This function attempts to solve them
     */
    public void Bready(View v) {
        try {
            drone.playLED(2,10,2);
            Thread.sleep(300);
            drone.trim();
            Thread.sleep(300);
            if (drone.isEmergencyMode()) drone.clearEmergencySignal();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Bready -> problem");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Bready -> problem");
        }
    }

    /**
     * This function (Button) causes the ARDrone to takeOff.
     * beyond that -    (1) take the NavData from the ARDrone
     *                  (2) start the Threads: Move and Mode that we created.
     */
    public void takeOff(View v) {
        try {
            if (!takeOff) {
                drone.takeOff();
                takeOff = true;
                takeOffLand.setText("Land");
                Thread.sleep(300);
                getND.getNavdata();
                Thread.sleep(100);
                if (!modeThread.isAlive()) {
                    modeThread.start();
                    moveThread.start();
                }
            }
            else {
                drone.land();
                takeOff = false;
                takeOffLand.setText("takeOff");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function (Button) run the function "setVis" to displaying or hiding the keyboard that control the ARDrone
     */
    public void keyboard(View v) {
        keyboard.setVis();
    }

    /**
     * the Button "front" is pressed
     */
    public void goFront(View v) {
        moveThreadDoString[0] = "-> GO FRONT ->";
        Function.fillMoveArray(move, 0, (int) ((-1) * indicatArray[0]), 0, 0);
    }

    /**
     * the Button "back" is pressed
     */
    public void goBack(View v) {
        moveThreadDoString[0] = "-> GO BACK ->";
        Function.fillMoveArray(move, 0, (int) indicatArray[0], 0, 0);
    }

    /**
     * the Button "right" is pressed
     */
    public void goRight(View v) {
        moveThreadDoString[0] = "-> GO Right ->";
        Function.fillMoveArray(move, (int) indicatArray[0], 0, 0, 0);
    }

    /**
     * the Button "left" is pressed
     */
    public void goLeft(View v) {
        moveThreadDoString[0] = "-> GO Left ->";
        Function.fillMoveArray(move, (int) ((-1) * indicatArray[0]), 0, 0, 0);
    }

    /**
     * the Button "hover" is pressed
     */
    public void hover(View v) {
        moveThreadDoString[0] = "-> HOVER <-";
        Function.fillMoveArray(move, 0, 0, 0, 0);
    }

    /**
     * the Button "up" is pressed
     */
    public void goUp(View v) {
        moveThreadDoString[0] = "-> GO UP <-";
        Function.fillMoveArray(move, 0, 0, (int) indicatArray[0], 0);
    }


    /**
     * the Button "down" is pressed
     */
    public void goDown(View v) {
        moveThreadDoString[0] = "-> GO DOWN <-";
        Function.fillMoveArray(move, 0, 0, (int) ((-1) * indicatArray[0]), 0);
    }

    /**
     * the Button "YawRight" is pressed
     */
    public void goYawRight(View v) {
        moveThreadDoString[0] = "-> GO YAW RIGHT <-";
        Function.fillMoveArray(move, 0, 0, 0, (int) ((-1) * indicatArray[0]));
    }

    /**
     * the Button "YawLeft" is pressed
     */
    public void goYawLeft(View v) {
        moveThreadDoString[0] = "-> GO YAW LEFT <-";
        Function.fillMoveArray(move, 0, 0, 0, (int) indicatArray[0]);
    }

    /**
     * the Button "findAzi" is pressed
     * this function just set the drone Mode - to find the azimut to the next point
     */
    public void findAzi(View v) {
        dMode[0] = Function.droneMode.Find_Azimuth;
    }

    /**
     * clean the map form check-points
     * Activates the function that empty the GPS lists
     */
    public void clearMap(View v) {
        marksCount = 0;
        map.clear();
        gpc.emptyList();
        rectOptions = null;
        polyline = null;
    }

    public void addListenerOnSpinnerItemSelection() {
        mapTypeSpinner = (Spinner) findViewById(R.id.map_type_spinner);
        mapTypeSpinner.setOnItemSelectedListener(this);
        mapTypeSpinner.setSelection(4);
    }

    // ===============================================================================================================================

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (DEBUG)
			Log.e(LOG_TAG, "+++ ON CREATE +++");

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        readPrefs();

        CONTROL_KEY_NAME = getResources().getStringArray(R.array.entries_controlkey_preference);

    	mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);		
		
        // Set up the window layout
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        // Set up the custom title
        mTitle = (TextView) findViewById(R.id.title_left_text);
        mTitle.setText(R.string.app_name);
        mTitle = (TextView) findViewById(R.id.title_right_text);
        

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
            finishDialogNoBluetooth();
			return;
		}
		
        setContentView(R.layout.term_activity);

        mEmulatorView = (EmulatorView) findViewById(R.id.emulatorView);

        mEmulatorView.initialize( this );

        mKeyListener = new TermKeyListener();

        mEmulatorView.setFocusable(true);
        mEmulatorView.setFocusableInTouchMode(true);
        mEmulatorView.requestFocus();
        mEmulatorView.register(mKeyListener);
        mSerialService = new BluetoothSerialService(this, mHandlerBT, mEmulatorView);
        
		if (DEBUG)
			Log.e(LOG_TAG, "+++ DONE IN ON CREATE +++");

        // ================================================== Our setting -> onCreate ====================================================
        emulatorview = (EmulatorView) findViewById(R.id.emulatorView);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMyLocationEnabled(true);
        addListenerOnSpinnerItemSelection();

        myPosition = new MarkerOptions();                                                   // create marker that represents the ARDrone.
        myPosition.icon(BitmapDescriptorFactory.fromResource(R.drawable.ardrone_new));      // Changing marker icon

        // trying to connect to the ARDrone. very important - Smartphone must be connected to WIFI before running the App.
        try {
            drone = new ARDrone();
            Thread.sleep(500);
            drone.connect();
            Thread.sleep(300);
            drone.playLED(2,10,2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("\"create drone -> problem\"");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // ===================== findViewById =====================
        takeOffLand = (Button) findViewById(R.id.TakeOff);
        navDataText = (TextView) findViewById(R.id.NavData);
        ll = (LinearLayout) findViewById(R.id.keyboard);
        speedBar = (SeekBar) findViewById(R.id.speedBar);
        speedNum = (TextView) findViewById(R.id.speedNum);
        tiltBar = (SeekBar) findViewById(R.id.tiltBar);
        tiltNum = (TextView) findViewById(R.id.tiltNum);
        MaxSenBar = (SeekBar) findViewById(R.id.MaxSenBar);
        MaxSenNum = (TextView) findViewById(R.id.MaxSenNum);
        ImmSenBar = (SeekBar) findViewById(R.id.ImmSenBar);
        ImmSenNum = (TextView) findViewById(R.id.ImSenNum);
        moveThreadDo = (TextView) findViewById(R.id.moveThreadDo);
        // ========================================================

        // set object to get the NavData:
        navData = new String[1];
        navData[0] = "";
        getND = new getNavData(drone,navData);

        // this arrays sent to Keyboard object
        SeekBar[] seekBars = {speedBar, tiltBar, MaxSenBar, ImmSenBar};
        TextView[] textBars = {speedNum, tiltNum, MaxSenNum, ImmSenNum};
        keyboard = new Keyboard(drone,ll,seekBars,textBars,indicatArray);

        // set the values of the move-array to zero -> the Mode of the ARDrone is Stay And Warn Dynamic
        Function.fillMoveArray(move, 0, 0, 0, 0);
        dMode[0] = Function.droneMode.Stay_And_Warn_Dynamic;

        // default Stream bluetooth - until the Bluetooth will be sent information.
        bluetooth[0] = "0,0,0,0,0,0";

        // default GPS point - until the GPS will be calibration
        // Home =>  32.167515,  35.085283
        // KCG  =>  32.102935,  35.209722
        droneLocation = new LatLng(32.167515,35.085283);

        gpc = new GpsPointContainer(droneLocation);         // look in GpsPointContainer class for more info.

        // set Threads
        modeThread = new ModeThread(drone,move,bluetooth,indicatArray,dMode,moveThreadDoString,gpc,getND, map, myPosition);
        moveThread = new MoveThread(drone,move,indicatArray);

        // set ARDrone location on map
        myPosition.position(gpc.getDroneLocation());


        // update drone location UI when the phone detects a change in his location
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                map.clear();
                updateUI();
                // the yaw from the chip
                if (ModeThread.bluetoothData != null) {
                    float yaw = ModeThread.bluetoothData[6];
                    if (yaw < 0) yaw = yaw + 360;
                    myPosition.rotation(yaw);
                    map.addMarker(myPosition);
                }
            }
        });

        // Add new Marker when you press and hold (longClick)
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng location) {
                marksCount++;

                // Generate the Marker
                MarkerOptions m = new MarkerOptions();
                m.position(location)
                        .draggable(true)
                        .title(location.toString() + " " + marksCount)
                        .snippet("Tap here to remove this marker");

                // Add the marker to the map and to the GpsPointContainer
                map.addMarker(m);
                gpc.add(location);

                // if the size of GpsPointContainer equal to 1 we create new PolylineOptions
                if (gpc.getListPointSize() == 1)
                    rectOptions = new PolylineOptions().add(location);
                else { // else we add the new location to PolylineOptions
                    rectOptions.add(location);
                    rectOptions.color(Color.RED);
                    polyline = map.addPolyline(rectOptions);
                }
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            int indexOfOldPosition = 0;

            // When you begin to drag maintain index point
            @Override
            public void onMarkerDragStart(Marker m) {
                String index = m.getTitle().split(" ")[2];
                indexOfOldPosition = Integer.parseInt(index) - 1;
            }

            // When we finished dragging we will update the new location in GpsPointContainer
            @Override
            public void onMarkerDragEnd(Marker m) {
                m.setTitle(m.getPosition().toString() + " " + indexOfOldPosition);
                if (indexOfOldPosition > -1) {
                    gpc.getLatLngList().set(indexOfOldPosition, m.getPosition());

                    // update UI
                    updateUI();
                }
            }

            @Override
            public void onMarkerDrag(Marker m) {
                // TODO Auto-generated method stub
            }
        });

        // Remove the Marker from the UI and from the GpsPointContainer object
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // Remove the marker
                marker.remove();
                gpc.remove(marker.getPosition());
                marksCount--;
                updateUI();
            }
        });

	}

    protected void updateUI() {
        map.clear();

        for (int i = 0; i < gpc.getListPointSize(); i++) {
            MarkerOptions m = new MarkerOptions();
            m.position(gpc.getLatLngList().get(i))
                    .draggable(true)
                    .title(gpc.getLatLngList().get(i).toString() + " " + (i + 1))
                    .snippet("Tap here to remove this marker");
            map.addMarker(m);
        }

        rectOptions = new PolylineOptions().addAll(gpc.getLatLngList());
        rectOptions.color(Color.RED);
        polyline = map.addPolyline(rectOptions);

        // ===============================================================================================================================
    }

	@Override
	public void onStart() {
		super.onStart();
		if (DEBUG)
			Log.e(LOG_TAG, "++ ON START ++");
		
		mEnablingBT = false;
	}

	@Override
	public synchronized void onResume() {
		super.onResume();

		if (DEBUG) {
			Log.e(LOG_TAG, "+ ON RESUME +");
		}
		
		if (!mEnablingBT) { // If we are turning on the BT we cannot check if it's enable
		    if ( (mBluetoothAdapter != null)  && (!mBluetoothAdapter.isEnabled()) ) {
			
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.alert_dialog_turn_on_bt)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.alert_dialog_warning_title)
                    .setCancelable( false )
                    .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                    	public void onClick(DialogInterface dialog, int id) {
                    		mEnablingBT = true;
                    		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);			
                    	}
                    })
                    .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                    	public void onClick(DialogInterface dialog, int id) {
                    		finishDialogNoBluetooth();            	
                    	}
                    });
                AlertDialog alert = builder.create();
                alert.show();
		    }		
		
		    if (mSerialService != null) {
		    	// Only if the state is STATE_NONE, do we know that we haven't started already
		    	if (mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
		    		// Start the Bluetooth chat services
		    		mSerialService.start();
		    	}
		    }

		    if (mBluetoothAdapter != null) {
		    	readPrefs();
		    	updatePrefs();

		    	mEmulatorView.onResume();
		    }
		}
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mEmulatorView.updateSize();
    }

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (DEBUG)
			Log.e(LOG_TAG, "- ON PAUSE -");

		if (mEmulatorView != null) {
			mInputManager.hideSoftInputFromWindow(mEmulatorView.getWindowToken(), 0);
			mEmulatorView.onPause();
		}
	}

    @Override
    public void onStop() {
        super.onStop();
        try {
            drone.land();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(DEBUG)
        	Log.e(LOG_TAG, "-- ON STOP --");
    }


	@Override
	public void onDestroy() {
		super.onDestroy();
        try {
            drone.land();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (DEBUG)
			Log.e(LOG_TAG, "--- ON DESTROY ---");
		
        if (mSerialService != null)
        	mSerialService.stop();
        
	}

    private void readPrefs() {
        mLocalEcho = mPrefs.getBoolean(LOCALECHO_KEY, mLocalEcho);
        mFontSize = readIntPref(FONTSIZE_KEY, mFontSize, 20);
        mColorId = readIntPref(COLOR_KEY, mColorId, COLOR_SCHEMES.length - 1);
        mControlKeyId = readIntPref(CONTROLKEY_KEY, mControlKeyId, CONTROL_KEY_SCHEMES.length - 1);
        mAllowInsecureConnections = mPrefs.getBoolean( ALLOW_INSECURE_CONNECTIONS_KEY, mAllowInsecureConnections);

        mIncomingEoL_0D = readIntPref(INCOMING_EOL_0D_KEY, mIncomingEoL_0D, 0x0D0A);
        mIncomingEoL_0A = readIntPref(INCOMING_EOL_0A_KEY, mIncomingEoL_0A, 0x0D0A);
        mOutgoingEoL_0D = readIntPref(OUTGOING_EOL_0D_KEY, mOutgoingEoL_0D, 0x0D0A);
        mOutgoingEoL_0A = readIntPref(OUTGOING_EOL_0A_KEY, mOutgoingEoL_0A, 0x0D0A);
        
		mScreenOrientation = readIntPref(SCREENORIENTATION_KEY, mScreenOrientation, 2);
    }

    private void updatePrefs() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mEmulatorView.setTextSize((int) (mFontSize * metrics.density));
        setColors();
        mControlKeyCode = CONTROL_KEY_SCHEMES[mControlKeyId];
        mSerialService.setAllowInsecureConnections( mAllowInsecureConnections );
        
        if (mEmulatorView != null) {
            mEmulatorView.setIncomingEoL_0D( mIncomingEoL_0D );
            mEmulatorView.setIncomingEoL_0A( mIncomingEoL_0A );
        }
        
		switch (mScreenOrientation) {
		case ORIENTATION_PORTRAIT:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case ORIENTATION_LANDSCAPE:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		default:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
    }

    private int readIntPref(String key, int defaultValue, int maxValue) {
        int val;
        try {
            val = Integer.parseInt( mPrefs.getString(key, Integer.toString(defaultValue)) );
        } catch (NumberFormatException e) {
            val = defaultValue;
        }
        val = Math.max(0, Math.min(val, maxValue));
        return val;
    }
    
	public int getConnectionState() {
		return mSerialService.getState();
	}

	private byte[] handleEndOfLineChars( int outgoingEoL ) {
		byte[] out;
		
	    if ( outgoingEoL == 0x0D0A ) {
	    	out = new byte[2];
	    	out[0] = 0x0D;
	    	out[1] = 0x0A;
		}
	    else {
		    if ( outgoingEoL == 0x00 ) {
		    	out = new byte[0];
		    }
		    else {
		    	out = new byte[1];
		    	out[0] = (byte)outgoingEoL;
		    }
	    }
		
		return out;
	}

    public void send(byte[] out) {
    	
    	if ( out.length == 1 ) {
    		
    		if ( out[0] == 0x0D ) {
    			out = handleEndOfLineChars( mOutgoingEoL_0D );
    		}
    		else {
        		if ( out[0] == 0x0A ) {
        			out = handleEndOfLineChars( mOutgoingEoL_0A );
        		}
    		}
    	}
    	
    	if ( out.length > 0 ) {
    		mSerialService.write( out );
    	}
    }
    
    public void toggleKeyboard() {
  		mInputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    
    public int getTitleHeight() {
    	return mTitle.getHeight();
    }
    
    // The Handler that gets information back from the BluetoothService
    private final Handler mHandlerBT = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {        	
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(DEBUG) Log.i(LOG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothSerialService.STATE_CONNECTED:
                	if (mMenuItemConnect != null) {
                		mMenuItemConnect.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                		mMenuItemConnect.setTitle(R.string.disconnect);
                	}
                	
                	mInputManager.showSoftInput(mEmulatorView, InputMethodManager.SHOW_IMPLICIT);
                	
                    mTitle.setText( R.string.title_connected_to );
                    mTitle.append(" " + mConnectedDeviceName);
                    break;
                    
                case BluetoothSerialService.STATE_CONNECTING:
                    mTitle.setText(R.string.title_connecting);
                    break;
                    
                case BluetoothSerialService.STATE_LISTEN:
                case BluetoothSerialService.STATE_NONE:
                	if (mMenuItemConnect != null) {
                		mMenuItemConnect.setIcon(android.R.drawable.ic_menu_search);
                		mMenuItemConnect.setTitle(R.string.connect);
                	}

            		mInputManager.hideSoftInputFromWindow(mEmulatorView.getWindowToken(), 0);
                	
                    mTitle.setText(R.string.title_not_connected);

                    break;
                }
                break;
            case MESSAGE_WRITE:
            	if (mLocalEcho) {
            		byte[] writeBuf = (byte[]) msg.obj;
            		mEmulatorView.write(writeBuf, msg.arg1);
            	}
                
                break;
/*                
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;              
                mEmulatorView.write(readBuf, msg.arg1);
                
                break;
*/                
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), getString(R.string.toast_connected_to) + " "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };    

    
    public void finishDialogNoBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_no_bt)
        .setIcon(android.R.drawable.ic_dialog_info)
        .setTitle(R.string.app_name)
        .setCancelable( false )
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       finish();            	
                   }
               });
        AlertDialog alert = builder.create();
        alert.show(); 
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(DEBUG) Log.d(LOG_TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        
        case REQUEST_CONNECT_DEVICE:

            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // Get the BLuetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                // Attempt to connect to the device
                mSerialService.connect(device);                
            }
            break;

        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode != Activity.RESULT_OK) {
                Log.d(LOG_TAG, "BT not enabled");
                
                finishDialogNoBluetooth();                
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (handleControlKey(keyCode, true)) {
            return true;
        } else if (isSystemKey(keyCode, event)) {
            // Don't intercept the system keys
            return super.onKeyDown(keyCode, event);
        } else if (handleDPad(keyCode, true)) {
            return true;
        }

        // Translate the keyCode into an ASCII character.
        int letter = mKeyListener.keyDown(keyCode, event);

        if (letter >= 0) {
        	byte[] buffer = new byte[1];
        	buffer[0] = (byte)letter;
        	
        	send( buffer );
        	//mSerialService.write(buffer);
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (handleControlKey(keyCode, false)) {
            return true;
        } else if (isSystemKey(keyCode, event)) {
            // Don't intercept the system keys
            return super.onKeyUp(keyCode, event);
        } else if (handleDPad(keyCode, false)) {
            return true;
        }

        mKeyListener.keyUp(keyCode);
        return true;
    }

    private boolean handleControlKey(int keyCode, boolean down) {
        if (keyCode == mControlKeyCode) {
            mKeyListener.handleControlKey(down);
            return true;
        }
        return false;
    }

    /**
     * Handle dpad left-right-up-down events. Don't handle
     * dpad-center, that's our control key.
     * @param keyCode
     * @param down
     */
    private boolean handleDPad(int keyCode, boolean down) {
    	byte[] buffer = new byte[1];

        if (keyCode < KeyEvent.KEYCODE_DPAD_UP ||
                keyCode > KeyEvent.KEYCODE_DPAD_CENTER) {
            return false;
        }

        if (down) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            	buffer[0] = '\r';
            	//mSerialService.write( buffer );
            	send( buffer );
            } else {
                char code;
                switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    code = 'A';
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    code = 'B';
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    code = 'D';
                    break;
                default:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    code = 'C';
                    break;
                }
            	buffer[0] = 27; // ESC
            	//mSerialService.write( buffer );                    
            	send( buffer );
                if (mEmulatorView.getKeypadApplicationMode()) {
                	buffer[0] = 'O';
                	//mSerialService.write( buffer );
                	send( buffer );
                } else {
                	buffer[0] = '[';
                	//mSerialService.write( buffer );
                	send( buffer );
                }
            	buffer[0] = (byte)code;
            	//mSerialService.write( buffer );
            	send( buffer );
            }
        }
        return true;
    }

    private boolean isSystemKey(int keyCode, KeyEvent event) {
        return event.isSystem();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        mMenuItemConnect = menu.getItem(0);
        mMenuItemStartStopRecording = menu.getItem(3);        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.connect:
        	
        	if (getConnectionState() == BluetoothSerialService.STATE_NONE) {
        		// Launch the DeviceListActivity to see devices and do scan
        		Intent serverIntent = new Intent(this, DeviceListActivity.class);
        		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        	}
        	else
            	if (getConnectionState() == BluetoothSerialService.STATE_CONNECTED) {
            		mSerialService.stop();
		    		mSerialService.start();
            	}
            return true;
        case R.id.preferences:
            doPreferences();
            return true;
        case R.id.menu_special_keys:
            doDocumentKeys();
            return true;
        case R.id.menu_start_stop_save:
        	if (mMenuItemStartStopRecording.getTitle() == getString(R.string.menu_stop_logging) ) {
        		doStopRecording();
        	}	
        	else {
        		doStartRecording();
        	}
            return true;
            
        case R.id.menu_about:
        	showAboutDialog();
            return true;
        }
        return false;
    }
    
    private void doPreferences() {
        startActivity(new Intent(this, TermPreferences.class));
    }
    
    public void doOpenOptionsMenu() {
        openOptionsMenu();
    }

    private void setColors() {
        int[] scheme = COLOR_SCHEMES[mColorId];
        mEmulatorView.setColors(scheme[0], scheme[1]);
    }

    private void doDocumentKeys() {
        String controlKey = CONTROL_KEY_NAME[mControlKeyId];
        new AlertDialog.Builder(this).
            setTitle( getString(R.string.title_document_key_press) + " \"" + controlKey + "\" "+ getString(R.string.title_document_key_rest)).
            setMessage(" Space ==> Control-@ (NUL)\n"
                    + " A..Z ==> Control-A..Z\n"
                    + " I ==> Control-I (TAB)\n"
                    + " 1 ==> Control-[ (ESC)\n"
                    + " 5 ==> Control-_\n"
                    + " . ==> Control-\\\n"
                    + " 0 ==> Control-]\n"
                    + " 6 ==> Control-^").
            show();
     }

    private void doStartRecording() {
    	File sdCard = Environment.getExternalStorageDirectory();
    	
    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
    	String currentDateTimeString = format.format(new Date());
    	String fileName = sdCard.getAbsolutePath() + "/blueTerm_" + currentDateTimeString + ".log";
    	
    	mEmulatorView.setFileNameLog( fileName );
    	mEmulatorView.startRecording();

    	mMenuItemStartStopRecording.setTitle(R.string.menu_stop_logging);
        Toast.makeText(getApplicationContext(), getString(R.string.menu_logging_started) + "\n\n" + fileName, Toast.LENGTH_LONG).show();
    }
    
    private void doStopRecording() {
    	mEmulatorView.stopRecording();
    	mMenuItemStartStopRecording.setTitle(R.string.menu_start_logging);    	
        Toast.makeText(getApplicationContext(), getString(R.string.menu_logging_stopped), Toast.LENGTH_SHORT).show();
    }

	private void showAboutDialog() {
		mAboutDialog = new Dialog(BlueTerm.this);
		mAboutDialog.setContentView(R.layout.about);
		mAboutDialog.setTitle( getString( R.string.app_name ) + " " + getString( R.string.app_version ));
		
		Button buttonOpen = (Button) mAboutDialog.findViewById(R.id.buttonDialog);
		buttonOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 
				mAboutDialog.dismiss();
			}
		});		
		
		mAboutDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                map.setMapType(0);
                break;
            case 1:
                map.setMapType(1);
                break;
            case 2:
                map.setMapType(2);
                break;
            case 3:
                map.setMapType(3);
                break;
            case 4:
                map.setMapType(4);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}


