package com.robonail.robonail;

/**
 * Created by huggy on 12/9/2017.
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by huggy on 12/8/2017.
 */

public class HttpRequest {
    private static final int delayMillis = 5000;
    public String getResponse(String myUrl){
        RobonailApplication.httpMessagesTotalCount++;
        String serverResponse="";

       // synchronized(this){
        try {
            // set the connection timeout value to 30 seconds (30000 milliseconds)
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, delayMillis); //how long to wait for a TCP/IP connection
            HttpConnectionParams.setSoTimeout(httpParams, delayMillis); //how long to wait for a susequent byte of data

            HttpClient client = new DefaultHttpClient(httpParams);
            //HttpClient client = new DefaultHttpClient();
            HttpGet getRequest = new HttpGet();

            URL url = new URL(myUrl);
            String nullFragment = null;
            URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            System.out.println("URI " + uri.toString() + " is OK");

            getRequest.setURI(uri); //new URI(url));
            HttpResponse response = client.execute(getRequest);

            InputStream inputStream = null;
            inputStream = response.getEntity().getContent();
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                serverResponse+=line+"\n";
            }
            inputStream.close();
            RobonailApplication.httpMessagesSuccessfulCount++;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            serverResponse = e.getMessage();
            RobonailApplication.httpMessagesFailedCount++;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            serverResponse = e.getMessage();
            RobonailApplication.httpMessagesFailedCount++;
        } catch (IOException e) {
            e.printStackTrace();
            serverResponse = e.getMessage();
            RobonailApplication.httpMessagesFailedCount++;
        }

       // }
        return serverResponse;
    }
}
