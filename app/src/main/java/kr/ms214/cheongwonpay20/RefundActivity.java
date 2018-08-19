package kr.ms214.cheongwonpay20;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ms214 on 2018-08-19
 */

public class RefundActivity extends Activity {
   //환불시 실행되는 액티비티
    String UserBar, UserName;
    TextView userbar, username;
    ListView refundlist;
    public static Handler mhandler;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);

        /**
         * OP_Code = OP_GetRefundList로  서버에 요청
         * 가져온 값을 listview에다가 셋팅 (Time:Goods_Num:Goods_Name)형식
         *
         * listview터치시 alertdialog 띄우기
         * 확인시 해당 listviewItem의 Barcode:Goods_Num형식으로 넘김 (서버로 OP_Code = OP_REFUND)
         * 뒤로가기 버튼 눌렀을때 MainActivity intent 실행
         * '바코드:충전요청금액' 형식의 OBJ
         */

        userbar = (TextView)findViewById(R.id.userbar);
        username = (TextView)findViewById(R.id.username);
        refundlist = (ListView)findViewById(R.id.refundlist);

        mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case NetworkThread.OP_GetRefundList:
                        String item[] = msg.obj.toString().split(":");
                        adapter.addItem(item[1], item[2], item[0]);
                        adapter.notifyDataSetChanged();//listview 재시작
                        break;
                }
            }
        };

        //listviewAdapter 객체 생성
        adapter = new ListViewAdapter();

        //listview에 Adapter 셋팅
        refundlist.setAdapter(adapter);

        UserBar = getIntent().getStringExtra("bar");
        UserName = getIntent().getStringExtra("name");

        userbar.setText("바코드:"+UserBar);
        username.setText("이름:"+username);

        sendNetworkThread(NetworkThread.OP_GetRefundList);

        refundlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                String time = ((ListViewItem)(adapter.getItem(i))).getItemCode();
                String Goods_Num = ((ListViewItem)(adapter.getItem(i))).getTitle();
                String Goods_Name = ((ListViewItem)(adapter.getItem(i))).getDesc();

                final String data = time+":"+Goods_Num+":"+Goods_Name;

                AlertDialog dialog = new AlertDialog.Builder(getApplicationContext())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("환불")
                        .setMessage(UserName+"님의 상품"+Goods_Name+"을(를) 환불하시겠습니까?")
                        .setPositiveButton("환불", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendNetworkThread(NetworkThread.OP_Refund, data);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(RefundActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    private void sendNetworkThread(int OP_Code){
        Message msg = new Message();
        msg.what = OP_Code;
        NetworkThread.instance.networkHandler.sendMessage(msg);
    }

    private void sendNetworkThread(int OP_Code, String Data){
        Message msg = new Message();
        msg.what = OP_Code;
        msg.obj = Data;
        NetworkThread.instance.networkHandler.sendMessage(msg);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Toast.makeText(getApplicationContext(), "메인으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}
