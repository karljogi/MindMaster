package net.hoccob.mindmaster.server;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.google.android.gms.common.util.Hex;

import net.hoccob.mindmaster.Encryption;
import net.hoccob.mindmaster.Equation;
import net.hoccob.mindmaster.Player;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class LogIn extends AsyncTask<String, String, String> {

    private String url;
    private Player player;
    public LogIn.AsyncResponse delegate;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public LogIn(Player player, AsyncResponse delegate){
        this.delegate = delegate;
        this.player = player;
    }

    @Override
    protected String doInBackground(String... params) throws RuntimeException{
        this.player.setUserName(params[0]);
        url =  "https://mindmaster.ee:8443/api/users/{eid}";
        String result = "";

        //Create template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        //GET player by userName
        try {
            Encryption encryption = new Encryption();
            result = restTemplate.getForObject(url, String.class, encryption.encrypt(player.getUserName()));
            //result = restTemplate.getForObject(url, String.class, player.getUserName());
            JSONObject jsonPlayer = new JSONObject(result);
            player.setId(jsonPlayer.getInt("id"));
            //player.setUserName(jsonPlayer.getString("userName"));
            player.setPoints(jsonPlayer.getInt("points"));
        }catch(RuntimeException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("LOGIN DONE!!");
        return result;
    }


    @Override
    protected void onPostExecute(String result){
        delegate.processFinish(result);
        //System.out.println("ONPOSTEXEC");
    }
}
