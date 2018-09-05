package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/**
 * Created by ms214 on 2018-08-19
 */

public class LostCancelActivity extends Activity {
   //분실신고/취소 실행되는 액티비티

    public static Handler mhandler;
    public TextView titletv;
    TextView bartv, nametv;
    Spinner schoolSP;
    Button submit, cancel, barbtn;
    String school, User_Name;
    String UserBar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lost_cancel);

        /**
         * i)분실신고op코드 = OP_LOST_REPORT = 20
         * ii)분실 신고 취소 OP코드 = OP_LOST_DEL = 21
         */

        final String title = getIntent().getStringExtra("title");
        titletv = (TextView)findViewById(R.id.title);
        bartv = (TextView)findViewById(R.id.bartv);
        nametv = (TextView)findViewById(R.id.name);
        barbtn = (Button)findViewById(R.id.bar_btn);
        submit = (Button)findViewById(R.id.submit);
        cancel = (Button)findViewById(R.id.cancel);

        titletv.setText(title);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LostCancelActivity.this);
                builder.setIcon(R.mipmap.ic_launcher)
                        .setTitle("분실 신고 취소")
                        .setMessage("정말 해당 학생의 분실신고를 취소하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(UserBar.equals(null)){
                                    Toast.makeText(LostCancelActivity.this, "바코드를 인식해 주세요!", Toast.LENGTH_SHORT).show();
                                }else {
                                    sendNetworkThread(NetworkThread.OP_CANCEL_LOST, UserBar);
                                }
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(LostCancelActivity.this, "분실신고취소를 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LostCancelActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        barbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZxingOrient integrator = new ZxingOrient(LostCancelActivity.this);// 바코드 인식창 Activity로 이동한다.
                integrator.setIcon(R.mipmap.ic_launcher)// 아이콘
                        .setToolbarColor("#32cd32")// 도구바 색상 설정 (청색)
                        .setInfoBoxColor("#32cd32")// 정보창 색상 설정 (청색)
                        .setInfo("학생증이나 팔찌의 바코드를 인식해주세요.");// 정보창의 알림 내용
                integrator.initiateScan();
            }
        });

        mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    /*case NetworkThread.OP_LOST_REPORT:
                            String result = (String)msg.obj;
                            if(result.equals("0")){
                                Toast.makeText(LostCancelActivity.this, "해당하는 정보의 학생의 학생증을 분실신고처리 완료하였습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }else if(result.equals("-1")){
                                Toast.makeText(LostCancelActivity.this, "입력하신 정보에 해당하는 학생정보가 없습니다. \n다시 한번 확인해 주십시오", Toast.LENGTH_LONG).show();
                            }else if(result.equals("1")){
                                Toast.makeText(LostCancelActivity.this, "이미 분실신고 된 학생정보입니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        break;*/

                    case NetworkThread.OP_EXIT:
                        AlertDialog.Builder builder = new AlertDialog.Builder(LostCancelActivity.this);
                        builder.setIcon(R.mipmap.ic_launcher)
                                .setTitle("Error!!!!")
                                .setMessage("서버간 연결 문제로 연결이 종료되었습니다. 앱 종료후 다시 실행 해 주세요!")
                                .setNeutralButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        System.exit(0);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case NetworkThread.OP_CANCEL_LOST:
                        /**
                         * 0일때 정상
                         * 1일때 분실되지 않은 학생정보
                         * -1 없는 정보
                         * */
                        String result = (String)msg.obj;
                        if(result.equals("0")){
                            Toast.makeText(LostCancelActivity.this, "바코드 : "+UserBar+"\n 해당 바코드의 학생증 분실 신고를 취소하였습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(result.equals("-1")){
                            Toast.makeText(LostCancelActivity.this, "입력하신 바코드에 해당하는 학생정보가 없습니다. \n다시 한번 확인해 주십시오", Toast.LENGTH_LONG).show();
                        }else if(result.equals("1")){
                            Toast.makeText(LostCancelActivity.this, "바코드 : "+UserBar+"\n분실신고가 되지 않은 학생정보입니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case NetworkThread.OP_GetName:// OP_GetName일 때
                        User_Name = (String)msg.obj;// 데이터를 User_Name에 저장한다.
                        String usersplit[] = User_Name.split(":");
                        User_Name = usersplit[1];

                        nametv.setText("이름:"+ User_Name);
                        break;

                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        ZxingOrientResult scanResult = ZxingOrient.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null && scanResult.getContents() != null) {// "scanResult"가 null이 아니고, "scanResult"의 내용이 null이 아닐 때
            UserBar = scanResult.getContents().toString();// "scanResult"의 내용을 "User"에 문자 데이터타입으로 변환후 저장한다.
            //Toast.makeText(this, "바코드 : " + User, Toast.LENGTH_LONG).show();// 바코드정보 Toast 알림을 띄운다.
            sendNetworkThread(3000, UserBar);
            bartv.setText("바코드 : " + UserBar);// TextView에 바코드정보 표시.
        }
        super.onActivityResult(requestCode, resultCode, intent);
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
