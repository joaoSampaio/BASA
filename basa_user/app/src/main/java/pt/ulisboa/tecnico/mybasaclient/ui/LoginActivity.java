package pt.ulisboa.tecnico.mybasaclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.rest.mail.WelcomeTemplate;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.SendEmailService;
import pt.ulisboa.tecnico.mybasaclient.util.FirebaseHelper;
import pt.ulisboa.tecnico.mybasaclient.util.QRCodeGenerator;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {


    private int position = 0;

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    private EditText mEmailView;
    private EditText mUsernameView;
    private View mProgressView;
    private View mLoginFormView;
    private View splash, header_container;
    private int SPLASH_DURATION = 2000;
    private ImageView img_animation;
    private TextView textViewLogo;
    Animation animHide, animShow;

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);
        // Set up the login form.


        Intent intent = getIntent();
        Log.d("login", "(intent != null):"+(intent != null));
        if(intent != null){
            Bundle bundle = intent.getExtras();
            Log.d("login", "(bundle != null):"+(bundle != null));
            if(bundle != null){
                String msgFromBrowserUrl = bundle.getString("msg_from_browser", "nada");
                Log.d("login", "msgFromBrowserUrl:"+msgFromBrowserUrl);
            }
        }


        Bundle b = getIntent().getExtras();
        boolean signOut = false; // or other values
        if(b != null)
            signOut = b.getBoolean("signout");
        splash = findViewById(R.id.splash);
        img_animation = (ImageView) findViewById(R.id.imageViewAnim);
        img_animation.setVisibility(!signOut? View.VISIBLE : View.GONE);
        splash.setVisibility(!signOut? View.VISIBLE : View.GONE);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);

        header_container = findViewById(R.id.header_container);

        if(!signOut) {
            Log.d("move", "moveAnimation1:");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fadeIn();

                }
            }, 100);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveAnimation();

                }
            }, 100 + 1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    slide();

                }
            }, SPLASH_DURATION + 1000 +100);
        }

        textViewLogo = (TextView)findViewById(R.id.textViewLogo);
        Typeface face= Typeface.createFromAsset(getAssets(), "Blacksword.otf");
        textViewLogo.setTypeface(face);
        ((TextView)findViewById(R.id.textView19)).setTypeface(face);
        mEmailView = (EditText) findViewById(R.id.email);
        mUsernameView = (EditText)findViewById(R.id.username);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if ( id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);



        User user = AppController.getInstance().getLoggedUser();
        if(user != null){
            mUsernameView.setText(user.getUserName());
            mEmailView.setText(user.getEmail());
        }


        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginGoogle();
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    if(mDatabase == null) {
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mHelper = new FirebaseHelper(mDatabase);
                        mHelper.registerUser(user.getUid(), user.getDisplayName(), user.getEmail());

                    }

                    User userBasa = new User();
                    userBasa.setUserName(user.getDisplayName());
                    userBasa.setEmail(user.getEmail());
                    userBasa.setUuid(user.getUid());
                    userBasa.setEnableFirebase(true);
                    User.saveUser(userBasa);
                    goToMainActivity();

                } else {
                    // User is signed out
                    Log.d("main", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void loginGoogle(){

        if(mAuth.getCurrentUser() == null){
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("main", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("main", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("main", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("main", "onActivityResult:" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d("main", "Google Sign In was successful, authenticate with Firebase");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d("main", "Google Sign In failed, update UI appropriately:");

                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void fadeIn(){
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img_animation.startAnimation(startAnimation);
    }

    private void slide(){
        splash.setVisibility(View.GONE);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );
        header_container.bringToFront();
        findViewById(R.id.title_container).bringToFront();
        ValueAnimator anim = ValueAnimator.ofInt(0, dm.widthPixels);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = header_container.getLayoutParams();
                layoutParams.width = val;
                header_container.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(1500);
        anim.start();

    }


    private void moveAnimation(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );

        int originalPos[] = new int[2];
        img_animation.getLocationOnScreen( originalPos );

        int xDelta = (dm.widthPixels - img_animation.getWidth())/2;
        int yDelta = (splash.getHeight() - img_animation.getHeight())/2;

        TranslateAnimation translate = new TranslateAnimation( 0, -xDelta , 0, -yDelta);
        translate.setDuration(SPLASH_DURATION);
        translate.setFillAfter(true);
        img_animation.startAnimation(translate);


    }

    private String generateUuidFromEmail(String email){

        return UUID.nameUUIDFromBytes(email.getBytes()).toString();

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(username) && !isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);


            String uuid = generateUuidFromEmail(email);

            User user = new User();
            user.setUserName(username);
            user.setEmail(email);
            user.setUuid(uuid);
            User.saveUser(user);

            goToMainActivity();

        }
    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



    private boolean isUserLoggedIn(){
        try {
            User user = AppController.getInstance().getLoggedUser();
            return user != null && user.getUuid() != null && !user.getUuid().isEmpty();
        }catch (Exception e){
            //if no user is saved an exception my the thrown
            return false;
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isUsernameValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }




    private void sendMailRegister(String uuid, String email) throws WriterException {

        Bitmap image = QRCodeGenerator.encodeAsBitmap(uuid);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        new SendEmailService(new CallbackFromService() {
            @Override
            public void success(Object response) {

            }

            @Override
            public void failed(Object error) {

            }
        }, email, "tema", WelcomeTemplate.getTemplate(), byteArray).execute();


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

