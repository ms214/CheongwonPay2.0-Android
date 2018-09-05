package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ms214 on 2018-08-15
 */

public class ChangeInfoActivity extends Activity {
    String Data;
    TextView userBar;
    EditText nameET, schoolET, gradeET, classET, numberET;
    Spinner schoolSP;
    String schoolsp;
    Button submit, cancel;

    //사용자 정보를 받을 액티비티
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        /**
         *  OP Code = OP_CHANGEINFO
         *  처리완료후 Toast메세지를 통해
         *  '회원정보 수정이 완료되었습니다. 바코드 인식 버튼을 통해 다시 바코드를 인식해 주세요' 메시지 출력
         *  바코드:이름:학교:학년:반:번호
         */

        final String bar = getIntent().getStringExtra("barcode");

        //뷰 연결 하기
        userBar = (TextView)findViewById(R.id.bartv);

        nameET = (EditText)findViewById(R.id.nameET);
        //schoolET = (EditText)findViewById(R.id.schoolET);
        schoolSP = (Spinner)findViewById(R.id.schoolSP);
        gradeET = (EditText)findViewById(R.id.gradeET);
        classET = (EditText)findViewById(R.id.classET);
        numberET = (EditText)findViewById(R.id.numberET);

        submit = (Button)findViewById(R.id.submit);
        cancel = (Button)findViewById(R.id.cancel);

        userBar.setText("바코드:"+bar);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolsp = schoolSP.getSelectedItem().toString();
                if(schoolsp.equals("학교선택")){
                    schoolsp=null;
                }else if(schoolsp.equals("청원고")){
                    schoolsp="M";
                }else if(schoolsp.equals("청원여고")){
                    schoolsp="W";
                }else if(schoolsp.equals("외부방문자")){
                    schoolsp="N";
                }
                //EditText의 글자 가져오기
                String etname= nameET.getText().toString();
                //String etSchool = schoolET.getText().toString();
                String etGrade = gradeET.getText().toString();
                String etClass = classET.getText().toString();
                String etnumber = numberET.getText().toString();

                if(!TextUtils.isEmpty(etname) && !TextUtils.isEmpty(schoolsp) && !TextUtils.isEmpty(etGrade) && !TextUtils.isEmpty(etClass) && !TextUtils.isEmpty(etnumber)){
                    Data = bar+":"+etname+":"+schoolsp+":"+etGrade+":"+etClass+":"+etnumber;//Data 형식 지정
                    sendNetworkThread(NetworkThread.OP_CHANGEINFO, Data);//NetworkThread로 OP코드와 DATA 보냄

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Toast.makeText(getApplicationContext(), "회원정보 수정이 완료되었습니다! 바코드 인식 버튼을 동해 다시 바코드를 인식해 주세요!!", Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다. 모든 항목을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                }
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
