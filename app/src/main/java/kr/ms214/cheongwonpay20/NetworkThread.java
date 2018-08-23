package kr.ms214.cheongwonpay20;

/**
 * Created by 정섭 on 2016-09-22
 * Edited by CheongwonSWClub on 2018-07-13
*/

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkThread extends HandlerThread {

    public static NetworkThread instance = null;

    public static  Handler networkHandler = null;

    public Socket socket = null;
    public DataOutputStream dos = null;
    public DataInputStream dis = null;

    public static final int OP_LOGIN = 1, OP_PURCHASE = 2, OP_ADD_ITEM = 3, OP_RF_BAL = 4, OP_GetGoodsList = 5,
            OP_GetRefundList = 6, OP_Refund = 7, OP_ATD = 10, OP_EditGoods = 11, OP_DeleteGoods = 12, OP_EditPW = 13, OP_GetName = 14,
            OP_UserMatching = 15, OP_BALANCE_CHARGE=16, OP_CHANGEINFO = 17, OP_CLUB_Profit = 18, OP_PAY_BACK = 19, OP_PURCHASE_RS_OverLimit = 104, OP_PURCHASE_RS_UserNull = 106, OP_PURCHASE_RS_Success = 105, OP_CHARGE_RS_USERNULL = 107, OP_CHARGE_RS_SUCCESS = 108,
            OP_EXIT=1110;//통신시 이용하는 명령 코드(OP-Code)를 정수 데이터타입으로 저장한다.

    public static final String OP_GetGoodsListFin = "##";// 통신시 이용하는 명령 코드(OP-Code)를 문자 데이터타입으로 저장한다. 이 코드만 문자형으로 사용하는 이유는 아래에서 DataInputStream할 때 문자형으로 불러오기 때문이다

    public static final String SERVER_IP = "211.207.150.105"; //서버IP준비 String 형식으로 작성해야함(서버호스팅 시 입력)
    public static final int SERVER_PORT = 923;

    public static void prepare(){
        if(instance!=null) return;
        instance = new NetworkThread("NetworkThread");
        instance.start();
        Log.d("NetworkThread","prepared!");
    }

    //생성자
    private NetworkThread(String name) {
        super(name);
    }

    private void connectSocket(){
        try {
            Log.d("NetworkThread", "Try connect");
            //socket = new Socket(SERVER_IP[(int)(Math.random()*3)], SERVER_PORT);// 여러개의 서버중 하나에 랜덤으로 접속할 수 있도록 Math.random()를 이용해 0이상 1미만의 값을 3배해주어 0이상 3미만을 정수(int형)로 캐스팅하여 0,1,2중 하나의 수가 랜덤 하게 나오게 하여 그 수를 서버IP배열의 인덱스값으로 설정하였다.
            socket = new Socket(SERVER_IP, SERVER_PORT);
            Log.d("NetworkThread", "Socket connected");
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Log.d("NetworkThread", e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    // MainActivity에 Handler를 이용하여 OP-Code와 데이터를 전달한다.
    private void sendMainActivity(int OP_Code, String Data){
        Message msg = new Message();
        msg.what = OP_Code;
        msg.obj = Data;
        MainActivity.mHandler.sendMessage(msg);
    }
    private void sendRefundActivity(int OP_Code, String Data){
        Message msg = new Message();
        msg.what = OP_Code;
        msg.obj = Data;
        RefundActivity.mhandler.sendMessage(msg);
    }

    @Override
    public void run() {
        Log.d("NetworkThread", "run()");

        connectSocket();
        Looper.prepare(); //건드리면 안댐
        // NetworkThread의 Handler
        networkHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                synchronized (this) {
                    Log.d("NetworkThread", "Current instance : " + this.toString());
                    Log.d("hello", "asjfdslafi");
                    switch (msg.what) {// OP-Code에 따라 작동
                        case OP_LOGIN:
                            Log.d("NetworkThread", "Start login");
                            if (socket == null || !socket.isConnected()) {
                                connectSocket();
                            }

                            if (socket == null || !socket.isConnected()) {
                                //TODO: 인터넷이 꺼져있거나, 서버가 닫혀있는 경우
                                return;
                            }

                            try {
                                dos.writeInt(OP_LOGIN);
                                dos.writeUTF((String) msg.obj);

                                String result = dis.readUTF();

                                Message res = new Message();
                                if (result.equals("Login Success!")) {

                                    res.what = LoginActivity.LOGIN_OK;
                                    MainActivity.CLUB_ID = msg.getData().getString("ID");
                                    MainActivity.Club_Name = dis.readUTF();
                                } else {
                                    res.what = LoginActivity.LOGIN_FAIL;
                                }
                                LoginActivity.mHandler.sendMessage(res);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case OP_PURCHASE:
                            try {
                                dos.writeInt(OP_PURCHASE);
                                dos.writeUTF((String) msg.obj);
                                int result = dis.readInt();
                                sendMainActivity(OP_PURCHASE, String.valueOf(result));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case OP_ADD_ITEM:
                            try {
                                dos.writeInt(OP_ADD_ITEM);
                                dos.writeUTF((String) msg.obj);//goods:price
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            break;

                        case OP_RF_BAL:
                            try {
                                dos.writeInt(OP_RF_BAL);
                                dos.writeUTF((String) msg.obj);//바코드전송

                                int result;//잔액
                                result = dis.readInt();
                                String temp = String.valueOf(result);
                                Log.d("Test", "result : " + temp);
                                sendMainActivity(OP_RF_BAL, temp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_GetGoodsList:
                            try {
                                dos.writeInt(OP_GetGoodsList);
                                String readData = dis.readUTF();
                                Log.e("READDATA", readData);
                                while (!readData.equals(OP_GetGoodsListFin)) {
                                    sendMainActivity(OP_GetGoodsList, readData);
                                    readData = dis.readUTF();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_ATD:
                            try {
                                dos.writeInt(OP_ATD);
                                dos.writeUTF((String) msg.obj);//바코드전송
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_EditGoods:
                            try {
                                dos.writeInt(OP_EditGoods);
                                dos.writeUTF((String) msg.obj);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_DeleteGoods:
                            try {
                                dos.writeInt(OP_DeleteGoods);
                                dos.writeUTF((String) msg.obj);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_EditPW:
                            try {
                                dos.writeInt(OP_EditPW);
                                dos.writeUTF((String) msg.obj);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_GetName:
                            try {
                                dos.writeInt(OP_GetName);
                                dos.writeUTF((String)msg.obj);
                                String readData = dis.readUTF();
                                sendMainActivity(OP_GetName, readData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_UserMatching:
                            try {
                                dos.writeInt(OP_UserMatching);
                                dos.writeUTF((String)msg.obj);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case OP_BALANCE_CHARGE://자금 충전
                            try{
                                dos.writeInt(OP_BALANCE_CHARGE);
                                dos.writeUTF((String)msg.obj);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                        case OP_CHANGEINFO://정보수정
                            try{
                                dos.writeInt(OP_CHANGEINFO);
                                dos.writeUTF((String)msg.obj);
                                Log.e("OP_CHANGEINFO", (String)msg.obj);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                        case OP_GetRefundList:
                            try{
                                dos.writeInt(OP_GetRefundList);
                                dos.writeUTF((String)msg.obj);
                                String readData = dis.readUTF();
                                while (!readData.equals(OP_GetGoodsListFin)) {
                                    sendRefundActivity(OP_GetRefundList, readData);
                                    readData = dis.readUTF();
                                }
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                        case OP_Refund:
                            try{
                                dos.writeInt(OP_Refund);
                                dos.writeUTF((String)msg.obj);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                        case OP_CLUB_Profit:
                            try{
                                dos.writeInt(OP_CLUB_Profit);
                                int readData = dis.readInt();
                                String readData2 = String.valueOf(readData);
                                sendMainActivity(OP_CLUB_Profit, readData2);
                                //동아리 수익금 가져와서 MainActivity로 넘기기
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            break;

                        case OP_PAY_BACK:
                            /**잔액환급요청시 실행
                             * 사용자바코드 보내기*/

                            try {
                                dos.writeInt(OP_PAY_BACK);
                                dos.writeUTF((String) msg.obj);
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                            break;
                    }
                }

            }

        };
        Looper.loop();

    }
}
