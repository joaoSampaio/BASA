package pt.ulisboa.tecnico.basa.util;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sampaio on 26/08/2016.
 */
public class VideoMerge extends AsyncTask<String, Integer, String> {


    private List<String> allFiles = new ArrayList<>();

    @Override
    protected void onPreExecute() {

        // do initialization of required objects objects here
    };


    /*
    * Logic: if constant movement - merge all videos in the last minute
    *  if no movement detected last 20 seconds merge available videos
    *  Delete afterwards check if has been uploaded to firebase or if anyone is watching the stream
    * */


    @Override
    protected String doInBackground(String... params) {

//        try {
//            long time = System.currentTimeMillis();

            //"VID_"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4"

//            String path = Environment.getExternalStorageDirectory().toString()+"/myAssistant";
//            Log.d("Files", "Path: " + path);
//            File f = new File(path);
//            File file[] = f.listFiles();
//            Log.d("Files", "Size: "+ file.length);
//            for (int i=0; i < file.length; i++)
//            {
//                Log.d("Files", "FileName:" + file[i].getName());
//            }
//
//
//
//
//            String paths[] = new String[count];
//            Movie[] inMovies = new Movie[count];
//            for (int i = 0; i < count; i++) {
//                paths[i] = path + filename + String.valueOf(i + 1) + ".mp4";
//                inMovies[i] = MovieCreator.build(new FileInputStream(
//                        paths[i]).getChannel());
//            }
//
//
//
//            List<Track> videoTracks = new LinkedList<Track>();
//            List<Track> audioTracks = new LinkedList<Track>();
//            for (Movie m : inMovies) {
//                for (Track t : m.getTracks()) {
//                    if (t.getHandler().equals("soun")) {
//                        audioTracks.add(t);
//                    }
//                    if (t.getHandler().equals("vide")) {
//                        videoTracks.add(t);
//                    }
//                }
//            }
//
//            Movie result = new Movie();
//
//            if (audioTracks.size() > 0) {
//                result.addTrack(new AppendTrack(audioTracks
//                        .toArray(new Track[audioTracks.size()])));
//            }
//            if (videoTracks.size() > 0) {
//                result.addTrack(new AppendTrack(videoTracks
//                        .toArray(new Track[videoTracks.size()])));
//            }
//
//            BasicContainer out = (BasicContainer) new DefaultMp4Builder()
//                    .build(result);
//
//            @SuppressWarnings("resource")
//            FileChannel fc = new RandomAccessFile(String.format(Environment
//                    .getExternalStorageDirectory() + "/wishbyvideo.mp4"),
//                    "rw").getChannel();
//            out.writeContainer(fc);
//            fc.close();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        String mFileName = Environment.getExternalStorageDirectory()
//                .getAbsolutePath();
//        mFileName += "/wishbyvideo.mp4";
//        filename = mFileName;
        return "";
    }

    @Override
    protected void onPostExecute(String value) {
        super.onPostExecute(value);

    }


//    private List<String> getAllVideos(long time){
//
//    }


}