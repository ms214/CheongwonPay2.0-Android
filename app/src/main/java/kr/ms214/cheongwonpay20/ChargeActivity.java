package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 재민 on 2018-08-17
 * Edited by Cheongwon-SW-Club on 2018-08-17*/

public class ChargeActivity extends Activity {
    EditText req_charge;
    TextView bar, User_name;
    Button submit, cancel;

    //자금추가시 실행되는 액티비티

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        /**
         * OP_Code = OP_BALANCE_CHARGE
         * '바코드:충전요청금액' 형식의 OBJ
         */

        final String barst = getIntent().getStringExtra("userBar");
        final String namest = getIntent().getStringExtra("name");
        
        bar = (TextView)findViewById(R.id.bartv);
        User_name = (TextView)findViewById(R.id.nametv);

        req_charge = (EditText)findViewById(R.id.reqchargeET);

        submit = (Button)findViewById(R.id.submit);
        cancel = (Button)findViewById(R.id.cancel);

        bar.setText("바코드:"+barst);
        User_name.setText("이름:"+namest);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reqcharge = req_charge.getText().toString();

                if(reqcharge!=null){
                    String data = barst+":"+reqcharge;
                    sendNetworkThread(NetworkThread.OP_BALANCE_CHARGE, data);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Toast.makeText(getApplicationContext(), "충전이 완료되었습니다! 바코드 인식 버튼을 동해 다시 바코드를 인식해 주세요!!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "충전요청금액이 없습니다. 다시 한번 확인해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
          cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                  Toast.makeText(getApplicationContext(),"충전을 취소하였습니다. 메인으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                  startActivity(intent);
                  finish();

              }
          });
    }

    private void sendNetworkThread(int OP_Code, String OBJ){
        Message msg = new Message();
        msg.what = OP_Code;
        msg.obj = OBJ;
        NetworkThread.instance.networkHandler.sendMessage(msg);
    }
}
