package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChargeInfoActivity extends Activity {
    int OP_Code = NetworkThread.OP_CHANGEINFO;
    String Data;
    TextView userBar;
    EditText nameET, schoolET, gradeET, classET, numberET;
    Button submit, cancel;

    //사용자 정보를 받을 액티비티
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargeinfo);
        /**
         *  OP Code = OP_CHANGEINFO
         *  처리완료후 Toast메세지를 통해
         *  '회원정보 수정이 완료되었습니다. 바코드 인식 버튼을 통해 다시 바코드를 인식해 주세요' 메시지 출력
         */


        sendNetworkThread(OP_Code, Data);//NetworkThread로 OP코드와 DATA 보냄
    }
    private void sendNetworkThread(int OP_Code, String OBJ){
        Message msg = new Message();
        msg.what = OP_Code;
        msg.obj = OBJ;
        NetworkThread.instance.networkHandler.sendMessage(msg);
    }
}
