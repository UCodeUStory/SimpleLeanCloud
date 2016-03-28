package com.leancloud.im.guide.newdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.leancloud.im.guide.AVImClientManager;
import com.leancloud.im.guide.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @ Author: qiyue (ustory)
 * @ Email: qiyuekoon@foxmail.com
 * @ Data:2016/3/27
 */
public class ChatActivity extends Activity {
    private EditText editText;
    private Button send;
    private List<AVUser> mAVUsers;
    private AVIMConversation imConversation;
    private ChatFragment chatFragment;
    private AVIMConversation squareConversation;
    private String mConversationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        findusers();
        chatFragment = (ChatFragment)getFragmentManager().findFragmentById(R.id.fragment_chat);
        String memberId = getIntent().getStringExtra("memberId");
        getConversation(memberId);
    }


    private void getConversation(final String memberId) {
        final AVIMClient client = AVImClientManager.getInstance().getClient();
        AVIMConversationQuery conversationQuery = client.getQuery();
        conversationQuery.withMembers(Arrays.asList(memberId), true);
        conversationQuery.whereEqualTo("customConversationType",1);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                showToast("query done");
                if (filterException(e)) {
                    //注意：此处仍有漏洞，如果获取了多个 conversation，默认取第一个
                    if (null != list && list.size() > 0) {
                        showToast("list.get(0)="+list.get(0));
                        chatFragment.setConversation(list.get(0));
                        mConversationId = list.get(0).getConversationId();
                    } else {
                        HashMap<String,Object> attributes=new HashMap<String, Object>();
                        attributes.put("customConversationType",1);
                        client.createConversation(Arrays.asList(memberId), null, attributes, false , new AVIMConversationCreatedCallback() {
                            @Override
                            public void done(AVIMConversation avimConversation, AVIMException e) {
                                showToast("create new Conversation");
                                mConversationId = avimConversation.getConversationId();
                                chatFragment.setConversation(avimConversation);
                            }
                        });
                    }
                }else{
                    showToast("query error"+e.getMessage());
                }
            }
        });
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
           // toast(e.getMessage());
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
       /* if (null != avimConversation) {
            NotificationUtils.addTag(avimConversation.getConversationId());
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
       // NotificationUtils.removeTag(imConversation.getConversationId());
    }

    private void findusers(){
        AVQuery<AVUser>query = AVUser.getQuery();
        query.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null){
                    list.remove(AVUser.getCurrentUser());
                    mAVUsers = list;
                  //  queryInSquare("");
                }else{
                    e.getMessage();
                }
            }
        });
    }


    protected void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();

    }
    protected void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }
}
