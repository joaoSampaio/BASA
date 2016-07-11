package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.camera.RectangleView;
import pt.ulisboa.tecnico.basa.ui.MainActivity;


public class CameraSettingsDialogFragment extends DialogFragment {

    private View rootView;
    private ImageView img_camera;
    private RectangleView rect;
    private BitmapMotionTransfer transfer;


    public CameraSettingsDialogFragment() {
        // Required empty public constructor
    }



    public static CameraSettingsDialogFragment newInstance() {
        CameraSettingsDialogFragment fragment = new CameraSettingsDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_camera_settings, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){

        rect = (RectangleView)rootView.findViewById(R.id.rect);
        img_camera = (ImageView)rootView.findViewById(R.id.img_camera);
//        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
//
//        textViewDescription.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getDialog().dismiss();
//            }
//        });
        rootView.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        rootView.findViewById(R.id.action_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<RectangleView.ColorBall> balls = rect.getColorballs();

                if(balls.size() != 4){
                    Toast.makeText(getActivity(), "Plase select a area on the image.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int i=0;
                Log.d("Cam", "ballWidth:" + balls.get(0).getWidthOfBall());
                Log.d("Cam", "--img_camera-> width:"+img_camera.getWidth());
                Log.d("Cam", "--img_camera-> height:" + img_camera.getHeight());

                Log.d("Cam", "rec top:" + rect.topRec);
                Log.d("Cam", "rec bottom:" + rect.bottomRec);
                Log.d("Cam", "rec left:" + rect.leftRec);
                Log.d("Cam", "rec right:" + rect.rightRec);

                int startLine = rect.topRec * AppController.getInstance().heightPreview / img_camera.getHeight();
                int endLine = rect.bottomRec * AppController.getInstance().heightPreview / img_camera.getHeight();

                int startLeft = rect.leftRec * AppController.getInstance().widthPreview / img_camera.getWidth();
                int endRight = rect.rightRec * AppController.getInstance().widthPreview / img_camera.getWidth();


                if(startLine < 0)
                    startLine = 0;
                if(endLine >= AppController.getInstance().heightPreview)
                    endLine = AppController.getInstance().heightPreview-1;

                if(startLeft < 0)
                    startLeft = 0;
                if(endRight >= AppController.getInstance().widthPreview )
                    endRight = AppController.getInstance().widthPreview -1;

                //switch the values
                if(startLine > endLine){
                    int tmp = startLine;
                    startLine = endLine;
                    endLine = tmp;
                }

                if(startLeft > endRight){
                    int tmp = startLeft;
                    startLeft = endRight;
                    endRight = tmp;
                }


                AppController.getInstance().skipTop = endLine;
                AppController.getInstance().skipBottom = startLine;
                AppController.getInstance().skipLeft = startLeft;
                AppController.getInstance().skipRight = endRight;

                SharedPreferences sp = getActivity().getSharedPreferences("BASA", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(Global.skipTop, endLine);
                editor.putInt(Global.skipBottom, startLine);
                editor.putInt(Global.skipLeft, startLeft);
                editor.putInt(Global.skipRight, endRight);
                editor.commit();

                for(RectangleView.ColorBall b : balls){
                    Log.d("Cam", "balls("+i+")-> width:"+b.getX());
                    Log.d("Cam", "balls("+i+")-> height:"+ b.getY());
                    i++;
                }


//                int centerX = balls.get(3).getX() - balls.get(3).getWidthOfBall();
//                int centerY = balls.get(3).getY() - balls.get(3).getHeightOfBall();
//                Log.d("Cam", "centerX(0)-> width:"+centerX);
//                Log.d("Cam", "centerY(0)-> height:"+centerY);

            }
        });



//            IMotionDetection detection = activity.getmHelper().getDetector();
//            if(detection != null){
//
////                Log.d("dialog", "width: " + AppController.getInstance().width + " height: " + AppController.getInstance().height);
//                Camera.Size size = activity.getmCamera().getParameters().getPreviewSize();
//                final Bitmap b = ImageProcessing.rgbToBitmap(detection.getPrevious(),
//                        size.width, size.height);
//                img_camera.setImageBitmap(b);
//
//            }



    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }

        transfer = new BitmapMotionTransfer() {
            @Override
            public void onBitMapAvailable(Bitmap bitmap) {
                if(getActivity() != null && img_camera != null)
                img_camera.setImageBitmap(bitmap);
            }
        };

        MainActivity activity = (MainActivity)getActivity();
        if(activity.getmHelper() != null) {

            activity.getmHelper().addImageListener(transfer);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).getmHelper().removeImageListener(transfer);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface BitmapMotionTransfer{
        void onBitMapAvailable(Bitmap bitmap);
    }

}
