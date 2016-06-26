package pt.ulisboa.tecnico.basa.webViews;

/**
 * Created by Sampaio on 16/04/2016.
 */
public class HttpCameraView {


    public static String getView(String ip){

            return "<html>" +
                "<head>" +
                    "<meta name='viewport' content='target-densitydpi=device-dpi,initial-scale=1,minimum-scale=1,user-scalable=yes'/>" +
                "</head>" +
                "<body>" +
                    "<center><img src=\"http://"+ip+"/\" alt=\"Stream\" align=\"middle\"></center>" +
                "</body>" +
            "</html>";

    }

}
