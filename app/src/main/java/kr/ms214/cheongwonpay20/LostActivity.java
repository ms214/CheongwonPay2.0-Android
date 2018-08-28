package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ms214 on 2018-08-19
 */

public class LostActivity extends Activity {
   //분실신고/취소 실행되는 액티비티

    public static Handler mhandler;
    public TextView titletv;
    EditText nameET, gradeET, classET, numberET;
    Spinner schoolSP;
    Button submit, cancel;
    String school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lost);

        /**
         * i)분실신고op코드 = OP_LOST_REPORT = 20
         * ii)분실 신고 취소 OP코드 = OP_LOST_DEL = 21
         */

        final String title = getIntent().getStringExtra("title");
        titletv = (TextView)findViewById(R.id.title);
        nameET = (EditText)findViewById(R.id.nameET);
        schoolSP = (Spinner)findViewById(R.id.schoolSP);
        gradeET = (EditText)findViewById(R.id.gradeET);
        classET = (EditText)findViewById(R.id.classET);
        numberET = (EditText)findViewById(R.id.numberET);
        submit = (Button)findViewById(R.id.submit);
        cancel = (Button)findViewById(R.id.cancel);

        titletv.setText(title);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //각 View 별
                String name = nameET.getText().toString();
                school = schoolSP.getSelectedItem().toString();
                if(school.equals("학교선택")){
                    school=null;
                }else if(school.equals("청원고")){
                    school="M";
                }else if(school.equals("청원여고")){
                    school="W";
                }else if(school.equals("외부방문자")){
                    school="N";
                }
                String grade = gradeET.getText().toString();
                String classet = classET.getText().toString();
                String number = numberET.getText().toString();
                if(name.equals(null) || school.equals(null) || grade.equals(null) || classet.equals(null) || number.equals(null)){
                    Toast.makeText(LostActivity.this, "입력하지 않은 값이 있습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    if(title.equals("분실 신고")){
                        /**OP코드 = OP_LOST_REPORT*/
                        String data = name+":"+school+":"+grade+":"+classet+":"+number;
                        Log.e("schoolData", data);
                        sendNetworkThread(NetworkThread.OP_LOST_REPORT, data);
                    }else if(title.equals("분실 신고 취소")){
                        /**OP코드 = OP_LOST_DEL*/
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LostActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case NetworkThread.OP_LOST_REPORT:
                            String result = (String)msg.obj;
                            if(result.equals("0")){
                                Toast.makeText(LostActivity.this, "해당하는 정보의 학생의 학생증을 분실신고처리 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }else if(result.equals("-1")){
                                Toast.makeText(LostActivity.this, "입력하신 정보에 해당하는 학생정보가 없습니다. \n다시 한번 확인해 주십시오", Toast.LENGTH_LONG).show();
                            }else if(result.equals("1")){
                                Toast.makeText(LostActivity.this, "이미 분실신고 된 학생정보입니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        break;

                    case NetworkThread.OP_CANCEL_LOST:
                        /**
                         * 0일때 정상
                         * 1일때 분실되지 않은 학생정보
                         * -1 없는 정보
                         * */
                        break;
                }
            }
        };
    }


    private void sendNetworkThread(int OP_Code, String Data){
        Message msg = new Message();
        msg.what = OP_Code;
        msg.obj = Data;
        NetworkThread.instance.networkHandler.sendMessage(msg);
    }


    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "메인으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
