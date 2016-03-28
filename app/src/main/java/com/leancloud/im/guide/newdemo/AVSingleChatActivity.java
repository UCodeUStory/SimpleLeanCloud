package com.leancloud.im.guide.newdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

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

import butterknife.Bind;

/**
 * Created by wli on 15/8/14.
 * 一对一单聊的页面，需要传入 Constants.MEMBER_ID
 */
public class AVSingleChatActivity extends Activity {

  @Bind(R.id.toolbar)
  protected Toolbar toolbar;

  protected ChatFragment chatFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_square);

    chatFragment = (ChatFragment)getFragmentManager().findFragmentById(R.id.fragment_chat);

    //setSupportActionBar(toolbar);
    /*toolbar.setNavigationIcon(R.drawable.btn_navigation_back);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });*/

    String memberId = getIntent().getStringExtra("memberId");
    setTitle(memberId);
    getConversation(memberId);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
  /*  Bundle extras = intent.getExtras();
    if (null != extras && extras.containsKey(Constants.MEMBER_ID)) {
      String memberId = extras.getString(Constants.MEMBER_ID);
      setTitle(memberId);
      getConversation(memberId);
    }*/
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
  /**
   * 获取 conversation，为了避免重复的创建，此处先 query 是否已经存在只包含该 member 的 conversation
   * 如果存在，则直接赋值给 ChatFragment，否者创建后再赋值
   */
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
          } else {
            HashMap<String,Object> attributes=new HashMap<String, Object>();
            attributes.put("customConversationType",1);
            client.createConversation(Arrays.asList(memberId), null, attributes, false , new AVIMConversationCreatedCallback() {
              @Override
              public void done(AVIMConversation avimConversation, AVIMException e) {
                showToast("create new Conversation");
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

  protected void showToast(String content) {
    Toast.makeText(this, content, Toast.LENGTH_SHORT).show();

  }
  protected void showToast(int resId) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
  }
}
