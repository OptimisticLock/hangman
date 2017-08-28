// http://hangman-api.herokuapp.com/api
// TODO: cache all token ID's.

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// https://alvinalexander.com/java/java-apache-httpclient-restful-client-examples

public class Hangman {

    DefaultHttpClient httpClient = new DefaultHttpClient();// TODO:deprecated; TODO:manage reconnects
    HttpHost host = new HttpHost("hangman-api.herokuapp.com", 80, "http");
    private String[] words;
    private String token;

    public static void main(String[] args) {
        new Hangman().run();
    }

    public String[] readWords() throws FileNotFoundException { // TODO exception

        Scanner sc = new Scanner(new File("words"));
        List<String> lines = new ArrayList<String>();

        while (sc.hasNextLine())
            lines.add(sc.nextLine());

        String[] result = lines.toArray(new String[0]);
        return result;

        //    FileUtils.readLines(new File("/path/filename"));
    }

    /**
     * @return Game token   // curl hangman-api.herokuapp.com/hangman -X POST
     * @throws Exception
     */
    public void newGame() throws Exception {  // TODO
        // specify the get request
        HttpPost postRequest = new HttpPost("/hangman");

  //      System.out.println("executing request to " + host);

        HttpResponse httpResponse = httpClient.execute(host, postRequest);
        JSONObject json = toJson(httpResponse);

        token = (String) json.get("token");
    }

    JSONObject toJson(HttpResponse response) throws IOException, JSONException {
        HttpEntity entity = response.getEntity();

        if (entity == null)
            return null; // TODO

        String retSrc = EntityUtils.toString(entity);
        System.out.println("Entity:" + retSrc);
        JSONObject result = new JSONObject(retSrc);
        return result;
    }

    // Good:curl -X PUT -d token=eyJzb2x1dGlvbiI6InNwaXJvY2hldGljIiwiY29ycmVjdF9ndWVzc2VzIjpbImUiLCJyIl0sIndyb25nX2d1ZXNzZXMiOltdfQ== -d letter=o http://hangman-api.herokuapp.com/hangman
    // Bad: curl -X PUT -d token=eyJzb2x1dGlvbiI6InJoeXRobWljIiwiY29ycmVjdF9ndWVzc2VzIjpbXScyI6W119 -d letter=a http://hangman-api.herokuapp.com/hangman

    private void guessNextMove(char letter) throws IOException, JSONException {
        // PUT /hangman { token: game token, letter: guess }

        //     String data = "{ token: '" + gameId + "', letter: letter}";
        HttpPut request = new HttpPut("/hangman");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("token", token));
        params.add(new BasicNameValuePair("letter", "" + letter));
        request.setEntity(new UrlEncodedFormEntity(params));
 //       System.out.println("executing request to " + request);

        HttpResponse httpResponse = httpClient.execute(host, request);

      //  {"hangman":"____a_","correct":true,"token":"eyJzb2x1dGlvbiI6ImNlbnRhbCIsImNvcnJlY3RfZ3Vlc3NlcyI6WyJhIl0sIndyb25nX2d1ZXNzZXMiOltdfQ=="}
        JSONObject json = toJson(httpResponse);
        token = (String) json.get("token"); // TODO DRY
    }

    char letter = 'a'; // TODO

    private char guess() {
        System.out.println("Guessing letter " + letter);
        return letter++; // TODO assert that <= 'z', also check for case sensitivity
    }

    private void run() {
        try {
            words = readWords();
            newGame();

            while (true) {
                char letter = guess();
                guessNextMove(letter);
            }

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

