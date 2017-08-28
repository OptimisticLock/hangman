import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

// https://alvinalexander.com/java/java-apache-httpclient-restful-client-examples

public class Hangman {

    DefaultHttpClient client;
    HttpResponse asdfadf;

    public static void main(String[] args) {
        DefaultHttpClient httpclient = new DefaultHttpClient();// TODO:deprecated
        try {
            // specify the host, protocol, and port
            HttpHost target = new HttpHost("hangman-api.herokuapp.com", 80, "http");

            // specify the get request
            HttpPost postRequest = new HttpPost("/hangman");

            System.out.println("executing request to " + target);

            HttpResponse httpResponse = httpclient.execute(target, postRequest);
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    public void newGame() {


    }
}
