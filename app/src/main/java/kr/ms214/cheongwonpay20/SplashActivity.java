package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ms214 on 2018-08-19
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
