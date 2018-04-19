package com.robonail.robonail;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by huggy on 12/8/2017.
 */

public class RobonailApplication extends Application {

    public static final String Tag = "RobonailApplication";
    private Handler handler = new Handler();
    private Handler handlerSaveStats = new Handler();

    public static String cmdURL = "http://192.168.4.100:80/cmd";
    public static String infoURL = "http://192.168.4.100:80/i";
   // private String infoURL = "http://192.168.4.100:80/info";
            //+ "?c=U&v=5.95"; //loop:1000{c=Cmd1:v=v1; c=Cmd2:v=v2;};";
    public static String response;
    private static int delayMillis = 350; //new SharedPreferences().getInt("delay_http_requests_millis");
    private static final int httpTimeoutMillis=2000;
    public static long httpMessagesTotalCount = 0;
    public static long httpMessagesFailedCount = 0;
    public static long httpMessagesSuccessfulCount=0;


    public static int menu_item_selected_id = 0;

    public static final String LOW_PRIORITY_HTTP = "LOW_PRIORITY";
    public static SharedPreferences preferences;

    @Override
    public void onCreate()
    {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        httpMessagesTotalCount = preferences.getLong("httpMessagesTotalCount",0);
        httpMessagesFailedCount = preferences.getLong("httpMessagesFailedCount",0);
        httpMessagesSuccessfulCount=preferences.getLong("httpMessagesSuccessfulCount",0);


        handler.postDelayed(runnable, Integer.parseInt(preferences.getString("delay_http_requests_millis","350")));
        handlerSaveStats.postDelayed(runnableSaveStats,60000);
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            /*HttpRequest request = new HttpRequest();
            String response = request.getResponse(myURL);
            Log.d(Tag,"url:"+response);
            */

            //new RetrieveHttp().execute(cmdURL+"?c=I&v=999999&f=mobile;","LOW_PRIORITY");
            new RetrieveHttp().execute(infoURL,"LOW_PRIORITY");
            //Log.i(Tag, "received:" + response);
            /* and here comes the "trick" */
            if(RetrieveHttp.threadsRunning>1) { //2 threads running
                handler.postDelayed(this, httpTimeoutMillis ); //wait longer than usual for threads to clear up
            }else {
                handler.postDelayed(this, Integer.parseInt(preferences.getString("delay_http_requests_millis","350")) );
            }
        }
    };

    private Runnable runnableSaveStats = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            SharedPreferences.Editor editor = preferences.edit();
            if(PerformanceMonitorFragment.checkBoxSaveData!=null &&
                    PerformanceMonitorFragment.checkBoxSaveData.isChecked()) {
                editor.putLong("httpMessagesTotalCount", httpMessagesTotalCount);
                editor.putLong("httpMessagesSuccessfulCount", httpMessagesSuccessfulCount);
                editor.putLong("httpMessagesFailedCount", httpMessagesFailedCount);
                editor.commit();
            }
            else{
                editor.putLong("httpMessagesTotalCount", 0);
                editor.putLong("httpMessagesSuccessfulCount", 0);
                editor.putLong("httpMessagesFailedCount", 0);
            }
        }
    };

    public MainActivity mainActivity;
}

