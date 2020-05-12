package com.example.lanchat.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lanchat.Adapter.MsgAdapter;
import com.example.lanchat.Bean.Msg;
import com.example.lanchat.Controller.CommunicateImageIdWithUDPController;
import com.example.lanchat.Controller.CommunicateImageWithTCPController;
import com.example.lanchat.Controller.CommunicateImageWithUDPController;
import com.example.lanchat.Controller.CommunicateWordsWithUDPController;
import com.example.lanchat.R;
import com.example.lanchat.Util.App;
import com.example.lanchat.Model.CommunicateImageIdWithUDPModel;
import com.example.lanchat.Model.CommunicateImageWithTCPModel;
import com.example.lanchat.Model.CommunicateImageWithUDPModel;
import com.example.lanchat.Model.CommunicateWordsWithUDPModel;
import com.example.lanchat.Util.OtherUtil;
import com.example.lanchat.Util.SDUtil;
import com.example.lanchat.Event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 聊天界面
 * 作者：方荣福
 * 时间：2020.5.6
 */
public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE = 101;
    private static final int CHOOSE_PHOTO = 1;
    public static final int TAKE_CAMERA = 2;
    private Uri imageUri;
    private EditText editText;//消息输入框
    private Button send;//发送按钮
    private Button sendImage;//发送图片
    private Button sendPhoto;//发送相机拍的照片
    private Button sendVoice;//发送语音
    private Button sendFile;//发送文件
    private CommunicateWordsWithUDPController transformWordsByUdp;//UDP控制类（文字）
    private CommunicateImageWithUDPController transformImageByUdp;//UDP控制类（图片）
    private CommunicateImageWithTCPController transformImageByTcp;//TCP控制类（图片）
    private CommunicateImageIdWithUDPController transformImageIdByUdp;//UDP控制类（头像ID）
    private Handler handler;
    private TextWatcher mTextWatcher;
    private View textEntryView;//dialog
    private EditText editImageId;//imageId输入框
    private EditText editIPAddr;//IP地址输入框
    private boolean isIPReady;
    private String wifiIp;//局域网下的ip地址
    private ListView msgListView;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<Msg>();
    private List mImageList;
    private int mImageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取wifiIP地址
        wifiIp = OtherUtil.getWifiIp(MainActivity.this);
        App.setsMyIP(wifiIp);

        //弹框请用户输入IP地址
        showInputIPDialog();

        //监听发送内容输入框（监听是否有内容）
        mTextWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 如果此时输入框内容不为空，则发送按钮可以点击，否则不可点击（置灰）
                if(!TextUtils.isEmpty(editText.getText().toString())){
                    send.setClickable(true);
                }else {
                    send.setClickable(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

        };

        //初始化视图界面
        initView();

        handler=new Handler();

        //发送文字消息按钮的点击事件
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sendContent = editText.getText().toString();
                if(!TextUtils.isEmpty(sendContent)){
                    Msg msg = new Msg(Msg.SENT,Msg.TEXT,App.getImageId(),sendContent);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示
                    msgListView.setSelection(msgList.size());//将ListView定位到最后一行

                    //使textView滑动到最下方显示最新消息
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //scrollView.fullScroll(View.FOCUS_DOWN);
                            editText.setText("");//清空输入框的内容
                        }
                    });

                    // 开启一个线程来发送消息
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // 通过UDPSocket发送消息
                            //transformImageIdByUdp.sendDataWithUDPSocket(String.valueOf(App.getImageId()));
                            transformWordsByUdp.sendDataWithUDPSocket(sendContent);
                        }
                    }).start();
                }
                editText.setText("");
            }
        });

        // 点击发送图片按钮，选择图片
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    //动态申请获取访问 读写磁盘的权限,也可以在注册文件上直接注册
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                    } else {
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_PHOTO);
                    }
                }else{
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivityForResult(intent, CHOOSE_PHOTO);
                }
            }
        });

        //发送相机拍的照片
        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建File对象，用于存储拍照后的图片
                //存放在手机SD卡的应用关联缓存目录下
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
               /* 从Android 6.0系统开始，读写SD卡被列为了危险权限，如果将图片存放在SD卡的任何其他目录，
                  都要进行运行时权限处理才行，而使用应用关联 目录则可以跳过这一步
                */
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                   7.0系统开始，直接使用本地真实路径的Uri被认为是不安全的，会抛 出一个FileUriExposedException异常。
                   而FileProvider则是一种特殊的内容提供器，它使用了和内 容提供器类似的机制来对数据进行保护，
                   可以选择性地将封装过的Uri共享给外部，从而提高了 应用的安全性
                 */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //大于等于版本24（7.0）的场合
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.feige.pickphoto.fileprovider", outputImage);
                } else {
                    //小于android 版本7.0（24）的场合
                    imageUri = Uri.fromFile(outputImage);
                }

                //启动相机程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //MediaStore.ACTION_IMAGE_CAPTURE = android.media.action.IMAGE_CAPTURE
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_CAMERA);
            }
        });

        //发送语音
        sendVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"发送语音功能待开发",Toast.LENGTH_SHORT).show();
            }
        });

        //发送文件
        sendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"发送文件功能待开发",Toast.LENGTH_SHORT).show();
            }
        });

        // 点击发送或者接收的图片，可以放大缩小
        msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//Listview里的Item里的删除图标设置监听
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                view.findViewById(R.id.left_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"查看图片功能待开发",Toast.LENGTH_SHORT).show();
                    }
                });

                view.findViewById(R.id.right_image).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"查看图片功能待开发",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // EventBus注册,subscriber订阅者为该MainActivity
        EventBus.getDefault().register(this);
    }

    /**
     * 显示弹框请求用户输入连接对方的IP地址
     */
    private void showInputIPDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        textEntryView = factory.inflate(R.layout.dialog, null);
        editImageId = textEntryView.findViewById(R.id.et_imageid);
        editIPAddr = textEntryView.findViewById(R.id.et_ip);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请输入自己的头像ID以及对方IP地址");
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setView(textEntryView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("清除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(false);
        final AlertDialog dialog=builder.create();
        // 此时显示弹窗
        dialog.show();

        // 点击确定时，在输入完ip地址后，打开三个接收线程，一个接收文字，一个接收图片，一个接收头像ID；打开一个发送线程，发送头像ID
        if(dialog.getButton(AlertDialog.BUTTON_POSITIVE)!=null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置头像
                    final String imageId = editImageId.getText().toString();
                    App.setImageId(Integer.parseInt(imageId));
                    String ip = editIPAddr.getText().toString();
                    if(!TextUtils.isEmpty(imageId)){
                        if(!TextUtils.isEmpty(ip)){
                            //正则表达式判断用户输入的IP地址是否合法
                            String REGEX = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";
                            Pattern pattern = Pattern.compile(REGEX);
                            Matcher matcher = pattern.matcher(ip);
                            boolean b=matcher.matches();
                            if(b){
                                transformWordsByUdp = new CommunicateWordsWithUDPController(ip);
                                transformImageByTcp = new CommunicateImageWithTCPController(ip);
                                transformImageIdByUdp = new CommunicateImageIdWithUDPController(ip);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 通过UDPSocket接收消息
                                        transformWordsByUdp.ServerReceviedByUdp();
                                    }
                                }).start();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 通过UDPSocket接收图片
                                        try {
                                            transformImageByTcp.receiveImage();
                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 接收头像ID
                                        transformImageIdByUdp.ServerReceviedByUdp();
                                    }
                                }).start();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 发送头像Id
                                        transformImageIdByUdp.sendDataWithUDPSocket(imageId);
                                    }
                                }).start();

                                Toast.makeText(MainActivity.this,"发消息试试看吧～",Toast.LENGTH_SHORT).show();
                                // 此时关闭弹窗
                                dialog.dismiss();
                            }else {
                                Toast.makeText(MainActivity.this,"非法的ip地址！",Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(MainActivity.this,"ip地址不能为空！",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this,"头像ID不能为空！",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        // 点击取消时
        if(dialog.getButton(AlertDialog.BUTTON_NEGATIVE)!=null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editIPAddr.setText("");
                }
            });
        }
    }

    /**
     * 初始化视图界面
     */
    private void initView() {
        editText = findViewById(R.id.et_send_content);
        send = findViewById(R.id.btn_send);
        sendImage = findViewById(R.id.btn_image);
        editText.addTextChangedListener(mTextWatcher);

        // 初始化消息数据
        //OtherUtil.initMsg(msgList);
        adapter = new MsgAdapter(MainActivity.this, R.layout.msg_item, msgList);
        msgListView = findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

        // 一开始消息输入框内容为空，设置为不可点击
        send.setClickable(false);

        sendPhoto = findViewById(R.id.btn_photo);//发送相机拍的照片
        sendVoice = findViewById(R.id.btn_voice);//发送语音
        sendFile = findViewById(R.id.btn_file);//发送文件
    }

    /**
     * 事件处理（真正的处理逻辑）：收到消息（事件）后做处理
     * MAIN：表示事件处理函数的线程在主线程(UI)线程，因此在这里不能进行耗时操作。
     * 这里主要做一些UI的更新
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showWords(MessageEvent messageEvent){
        if(messageEvent.getType() == CommunicateImageIdWithUDPModel.IMAGE_ID){
            App.setOtherImageId(messageEvent.getImageId());
        }
        if(!TextUtils.isEmpty(messageEvent.getWords()) || messageEvent.getImage() != null){
            // 判断接收的消息类型
            if(messageEvent.getType() == CommunicateWordsWithUDPModel.TEXT){
                //这里的头像ID不应该从APP.getImageId()里拿，而是从App.getOtherImageId()里拿
                Msg msg = new Msg(Msg.RECEIVED,Msg.TEXT,App.getOtherImageId(), messageEvent.getWords());
                msgList.add(msg);
                adapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示
                msgListView.setSelection(msgList.size());//将ListView定位到最后一行
            }else if(messageEvent.getType() == CommunicateImageWithTCPModel.IMAGE){
                Bitmap bitmap = BitmapFactory.decodeByteArray(messageEvent.getImage(), 0, messageEvent.getImage().length);
                Msg msg = new Msg(Msg.RECEIVED,Msg.IMAGE,App.getOtherImageId(),bitmap);
                msgList.add(msg);
                adapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示
                msgListView.setSelection(msgList.size());//将ListView定位到最后一行
            }

        }else if(!TextUtils.isEmpty(messageEvent.getWords())){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    final String path = SDUtil.getFilePathByUri(MainActivity.this,data.getData());

                    if(!TextUtils.isEmpty(imageUri.toString())){
                        Msg msg = new Msg(Msg.SENT,Msg.IMAGE,App.getImageId(),imageUri);
                        msgList.add(msg);

                        //有新消息时，刷新ListView中的显示
                        adapter.notifyDataSetChanged();

                        //将ListView定位到最后一行
                        msgListView.setSelection(msgList.size());

                        // 开启一个线程来发送图片
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 通过UDPSocket发送图片
                                try {
                                    transformImageByTcp.sendImage(path);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                break;
            case TAKE_CAMERA:
                if (resultCode == RESULT_OK) {
                    final String path = SDUtil.getFilePathByUri(MainActivity.this, imageUri);
                    if (!TextUtils.isEmpty(imageUri.toString())) {
                        Msg msg = new Msg(Msg.SENT, Msg.IMAGE, App.getImageId(), imageUri);
                        msgList.add(msg);

                        //有新消息时，刷新ListView中的显示
                        adapter.notifyDataSetChanged();

                        //将ListView定位到最后一行
                        msgListView.setSelection(msgList.size());

                        // 开启一个线程来发送图片
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 通过UDPSocket发送图片
                                try {
                                    transformImageByTcp.sendImage(path);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("警告！")
//                .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
//                        finish();
//                    }
//                }).show();
    }

    @Override
    protected void onResume() {
        // 在activity构建时就开启一个线程来接收消息
        super.onResume();
        if(isIPReady){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 通过UDPSocket接收消息
                    transformWordsByUdp.ServerReceviedByUdp();
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 通过UDPSocket接收图片
                    try {
                        transformImageByTcp.receiveImage();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 接收头像ID
                    transformImageIdByUdp.ServerReceviedByUdp();
                }
            }).start();
        }
    }

    /**
     * onDestroy()方法记得关闭socket连接，避免端口持续占用
     * 解注册EventBus
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭socket连接，避免端口持续占用
        transformWordsByUdp.disconnect();
        transformImageByUdp.disconnect();
        transformImageIdByUdp.disconnect();
        // EventBus注销，取消订阅
        EventBus.getDefault().unregister(this);
    }

}


