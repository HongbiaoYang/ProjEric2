package com.ttsproj.projectericlite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bill on 4/27/2016.
 */
public class activity_setting extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "activity_setting";
    private static final int RC_SIGN_IN = 99;
    private TextView SpeedText;
    private SeekBar SpeedSlide;
    private float speed;
    private LinearLayout HomeHeader;
    private ImageView GoBack;
    private LinearLayout Emergency;
    private LoginButton fbLogin;
    private CallbackManager callbackManager;
    private SignInButton ggLogin;
    private Button ggSignOut;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private String server = "mydesk.desktops.utk.edu";
    private String port = "3009";
    private String collection = "eric";
    private  String dbName = "mongo-server";


    @Override
    public void onBackPressed() {
        writeSpeedToDatabase(speed);
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);

        SpeedText = (TextView) findViewById(R.id.speedText);
        SpeedSlide = (SeekBar) findViewById(R.id.speedSlide);
        HomeHeader = (LinearLayout)findViewById(R.id.home_header);
        GoBack = (ImageView)findViewById(R.id.head_home1);
        Emergency = (LinearLayout)findViewById(R.id.head_home3);


        // facebook stuff
        fbLogin = (LoginButton)findViewById(R.id.fbLogin);
        fbLogin.setReadPermissions("email");

        callbackManager = CallbackManager.Factory.create();

        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "fb success");
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d(TAG, response.toString());

                                // application code
                                try {
                                    String email =  object.getString("email");
                                    String name = object.getString("name");
                                    sendDataToServer(name, email, "Facebook");

                                } catch (JSONException e) {
                                    Log.d(TAG, "data read err:"+e.toString());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,name");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "fb cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "fb error");

            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        ggLogin = (SignInButton)findViewById(R.id.ggLogin);
        ggLogin.setSize(SignInButton.SIZE_STANDARD);

        ggSignOut = (Button)findViewById(R.id.sign_out_button);

        // Google Login clicked
        ggLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        }); // [End of google login clicked]



        // log out google
        ggSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                MyProperties.getInstance().updateGGLoginStatus(false);
                                updateUI(false);
                                Log.d(TAG, "gg logout");
                            }
                        }
                );
            }
        });
        // [End of logout google]

        // decide to show google login or google logout button
        if (MyProperties.getInstance().isGGLogged()) {
            updateUI(true);
        } else {
            updateUI(false);
        }


        // override and set listen for go back button
        GoBack.setBackgroundResource(R.drawable.gobacklong);
        GoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeSpeedToDatabase(speed);
                finish();
            }
        });// [End of go back button click]

        // set emergency button to hide
        Emergency.setVisibility(View.INVISIBLE);

        // default read from the database
        speed = Float.parseFloat(MyProperties.getInstance().database.getProp("speakSpeed"));
        SpeedText.setText("Talking Speed (" + speed +")");
        SpeedSlide.setProgress((int)(speed*100));
        // [End of speed setting initialization]

        // set the slider
        SpeedSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "progress="+progress);
                speed = (float) progress / 100.0f;

                SpeedText.setText("Talking Speed ("+speed+")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                // change setting and speak out welcome phase after release
                MyProperties.getInstance().gtts.setSpeechRate(speed*2.0f);
                MyProperties.getInstance().speakout(getResources().getString(R.string.welcomeSpeed));
            }
        }); //[End of speed slide listener implementation]

    }

   /* @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }


    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }
    */

    // on activity result for both facebook click and google click
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    } // [End of activity result ]

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
       /* if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            updateUI(true);

            // send data to server with mongodb
            sendDataToServer(acct.getDisplayName(), acct.getEmail(), "Google");
        } else {
            updateUI(false);
        }*/

        GoogleSignInAccount acct = result.getSignInAccount();
        updateUI(true);

        // send data to server with mongodb
        sendDataToServer(acct.getDisplayName(), acct.getEmail(), "Google");
        MyProperties.getInstance().updateGGLoginStatus(true);


    }

    // send login data to monggodb database
    private void sendDataToServer(String name, String email, String platform) {
        Log.d(TAG, "json data:"+name + " email:"+email + " platform:"+platform);

       /* String dbURI = String.format("mongodb://%s:%s/%s", server, port, dbName);

        MongoClientURI uri = new MongoClientURI(dbURI);
        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase db = mongoClient.getDatabase(uri.getDatabase());
        MongoCollection collection =  db.getCollection("eric");


   *//*     BasicDBObject document = new BasicDBObject();
        document.put("name", name);
        document.put("email", email);
        document.put("platform", platform);
        document.put("os","Android");*//*
        Document document = new Document("name", name)
                 .append("email", email)
                 .append("platform", platform)
                 .append("os","Android");


        collection.insertOne(document);*/

    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            ggLogin.setVisibility(View.GONE);
            ggSignOut.setVisibility(View.VISIBLE);
        } else {
            ggLogin.setVisibility(View.VISIBLE);
            ggSignOut.setVisibility(View.GONE);
        }
    }

    private void writeSpeedToDatabase(float speed) {
        // update the value in database after exit the page
        MyProperties.getInstance().database.updateProp("speakSpeed", Float.toString(speed));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}