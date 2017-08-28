import com.sun.org.glassfish.gmbal.NameValue;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// https://alvinalexander.com/java/java-apache-httpclient-restful-client-examples

public class Hangman {

    DefaultHttpClient httpClient = new DefaultHttpClient();// TODO:deprecated; TODO:manage reconnects
    HttpHost host = new HttpHost("hangman-api.herokuapp.com", 80, "http");

    public static void main(String[] args) {
        new Hangman().run();
    }


    /**
     * @return Game token   // curl hangman-api.herokuapp.com/hangman -X POST
     * @throws Exception
     */
    public String newGame() throws Exception {  // TODO
        // specify the get request
        HttpPost postRequest = new HttpPost("/hangman");

        System.out.println("executing request to " + host);

        HttpResponse httpResponse = httpClient.execute(host, postRequest);
        HttpEntity entity = httpResponse.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(httpResponse.getStatusLine());
        Header[] headers = httpResponse.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i]);
        }
        System.out.println("----------------------------------------");


        if (entity != null) {

            String retSrc = EntityUtils.toString(entity);
            System.out.println("Entity:" + retSrc);
            JSONObject result = new JSONObject(retSrc); //Convert String to JSON Object
            String gameId = (String) result.get("token");
            return gameId;
        }
        return null; // TODO
    }

    // Good:curl -X PUT -d token=eyJzb2x1dGlvbiI6InNwaXJvY2hldGljIiwiY29ycmVjdF9ndWVzc2VzIjpbImUiLCJyIl0sIndyb25nX2d1ZXNzZXMiOltdfQ== -d letter=o http://hangman-api.herokuapp.com/hangman
    // Bad: curl -X PUT -d token=eyJzb2x1dGlvbiI6InJoeXRobWljIiwiY29ycmVjdF9ndWVzc2VzIjpbXScyI6W119 -d letter=a http://hangman-api.herokuapp.com/hangman

    private void guessNextMove(String gameId) throws IOException {
        // PUT /hangman { token: game token, letter: guess }

        String data = "{ token: '" + gameId + "', letter: 'a'}";
        HttpPut request = new HttpPut("/hangman");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("token", gameId));
        params.add(new BasicNameValuePair("letter", "a"));
        request.setEntity(new UrlEncodedFormEntity(params));
   //     request.setHeader("Content-type", "multipart/form-data");

        // TODO are these necessary?Review them
    //    StringEntity params = new StringEntity(data, "UTF-8");
    //    params.setContentType("application/json");
     //   request.addHeader("content-type", "application/json");
    //    request.addHeader("Accept", "*/*");
//        request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
//        request.addHeader("Accept-Language", "en-US,en;q=0.8");
     //   request.setEntity(params);

        System.out.println("executing request to " + request);

        HttpResponse httpResponse = httpClient.execute(host, request);
        HttpEntity entity = httpResponse.getEntity();

        System.out.println("----------------------------------------");
        System.out.println(httpResponse.getStatusLine());
        Header[] headers = httpResponse.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i]);
        }
        System.out.println("----------------------------------------");

        if (entity != null) {
            System.out.println(EntityUtils.toString(entity));
        }
    }


    private void run() {
        try {
            String gameId = newGame();
            guessNextMove(gameId);

        } catch (Exception e) { // TODO be specific
            e.printStackTrace();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpClient.getConnectionManager().shutdown();
        }
    }


}

