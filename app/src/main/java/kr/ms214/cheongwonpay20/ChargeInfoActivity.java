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

public class ChargeInfoActivity extends Activity {
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
         *  바코드:이름:학교:학년:반:번호
         */

        final String bar = getIntent().getStringExtra("barcode");

        //뷰 연결 하기
        /*userBar = (TextView)findViewById(R.id.);

        nameET = (EditText)findViewById(R.id.);
        schoolET = (EditText)findViewById(R.id.);
        gradeET = (EditText)findViewById(R.id.);
        classET = (EditText)findViewById(R.id.);
        numberET = (EditText)findViewById(R.id.);

        submit = (Button)findViewById(R.id.);
        cancel = (Button)findViewById(R.id.);*/

        userBar.setText(bar);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText의 글자 가져오기
                String etname= nameET.getText().toString();
                String etSchool = schoolET.getText().toString();
                String etGrade = gradeET.getText().toString();
                String etClass = classET.getText().toString();
                String etnumber = numberET.getText().toString();

                Data = bar+":"+etname+":"+etSchool+":"+etGrade+":"+etClass+":"+etnumber;//Data 형식 지정
                sendNetworkThread(NetworkThread.OP_CHANGEINFO, Data);//NetworkThread로 OP코드와 DATA 보냄

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(getApplicationContext(), "회원정보 수정이 완료되었습니다! 바코드 인식 버튼을 동해 다시 바코드를 인식해 주세요!!", Toast.LENGTH_LONG).show();
                startActivity(intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Toast.makeText(getApplicationContext(), "회원정보 수정을 취소하여 메인으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
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
