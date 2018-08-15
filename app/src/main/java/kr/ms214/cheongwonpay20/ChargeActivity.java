package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    }

    private void sendNetworkThread(int OP_Code, String OBJ){
        Message msg = new Message();
        msg.what = OP_Code;
        msg.obj = OBJ;
        NetworkThread.instance.networkHandler.sendMessage(msg);
    }
}
