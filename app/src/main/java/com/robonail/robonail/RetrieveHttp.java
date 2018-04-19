package com.robonail.robonail;

import android.os.AsyncTask;
import android.util.Log;

class RetrieveHttp extends AsyncTask<String, Void, String> {

    public static final String Tag = "RetrieveHttp";
    public static int threadsRunning = 0;
    private Exception exception;

    @Override
    protected String doInBackground(String... urls) {
        String response="";

        if (urls[urls.length-1]==RobonailApplication.LOW_PRIORITY_HTTP && threadsRunning>1)
        {
            Log.d(Tag,"Discarding low priority message - Queue "+threadsRunning);
            response = "";
        }
        else
            response = new HttpRequest().getResponse(urls[0]);


        return response; //URL(urls[0]);
    }

    @Override
    protected void onPreExecute() {
        threadsRunning++;
    }

    @Override
    protected void onPostExecute(String resp) {
        // TODO: check this.exception
        // TODO: do something with the feed
        if(resp.length()>1){
            RobonailApplication.response = resp;
            ControlCenterFragment.txtArduino_addline(resp);
        }
        threadsRunning--;
    }
}