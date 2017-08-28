import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

// https://alvinalexander.com/java/java-apache-httpclient-restful-client-examples

public class Hangman {

    DefaultHttpClient httpClient = new DefaultHttpClient();// TODO:deprecated; TODO:manage reconnects
    HttpHost request = new HttpHost("hangman-api.herokuapp.com", 80, "http");

    public static void main(String[] args) {
        new Hangman().run();
    }


    /**
     * @return Game token
     * @throws Exception
     */
    public String newGame() throws Exception {  // TODO
        // specify the get request
        HttpPost postRequest = new HttpPost("/hangman");

        System.out.println("executing request to " + request);

        HttpResponse httpResponse = httpClient.execute(request, postRequest);
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

            // parsing JSON

            JSONObject result = new JSONObject(retSrc); //Convert String to JSON Object
            String t = (String) result.get("token");
            return t;
        }
        return null; // TODO
    }

    private void guess() throws IOException {
        // PUT /hangman { token: game token, letter: guess }

        String data = "{ token: 'game token', letter: 'a'}";
        HttpPut request = new HttpPut("/hangman");

        StringEntity params = new StringEntity(data, "UTF-8");
        params.setContentType("application/json");
        request.addHeader("content-type", "application/json");
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Encoding", "gzip,deflate,sdch");
        request.addHeader("Accept-Language", "en-US,en;q=0.8");
        request.setEntity(params);

        System.out.println("executing request to " + request);

        HttpResponse httpResponse = httpClient.execute(request);
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
            String token = newGame();
            guess();

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

