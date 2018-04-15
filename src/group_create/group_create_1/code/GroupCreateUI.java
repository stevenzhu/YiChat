package group_create.group_create_1.code;

import group_add_member.group_add_member_1.code.GroupAddMemberUI;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.exceptions.HyphenateException;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.utils.ACache;
import com.shorigo.utils.Constants;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 创建群组
 * 
 * @author peidongxu
 * 
 */
public class GroupCreateUI extends BaseUI {
	private EditText et_name, et_remark;
	// 群组名称，简介
	private String groupName, desc;
	// 群成员
	private String[] arrMembers;
	// 添加人员列表
	private GridView gv_content;
	private GroupCreateAdapter adapter;
	private ProgressDialog progressDialog;
	private List<Map<String, String>> listMap;
	private Gson gson;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.group_create_1);
	}

	@Override
	protected void findView_AddListener() {
		et_name = (EditText) findViewById(R.id.et_name);
		et_remark = (EditText) findViewById(R.id.et_remark);

		LinearLayout z_add_member = (LinearLayout) findViewById(R.id.z_add_member);
		z_add_member.setOnClickListener(this);

		gv_content = (GridView) this.findViewById(R.id.gv_group_content);
	}

	@Override
	protected void prepareData() {
		setTitle("创建群组");
		setRightButton("创建");

		gson = new Gson();
		arrMembers = new String[0];
		listMap = new ArrayList<Map<String, String>>();
		ACache.get(this).put("select_member", "");
		// 初始化对象
		adapter = new GroupCreateAdapter(this, listMap, mHandler);
		gv_content.setAdapter(adapter);

	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			// 创建
			groupName = et_name.getText().toString();
			desc = et_remark.getText().toString();
			if (Utils.isEmity(groupName)) {
				MyApplication.getInstance().showToast("群组名称不能为空");
				return;
			}
			if (listMap != null && listMap.size() > 0) {
				int size = listMap.size();
				arrMembers = new String[size];
				for (int i = 0; i < size; i++) {
					arrMembers[i] = Constants.PREFIX + listMap.get(i).get("user_id");
				}
				adapter.setData(listMap);
			} else {
				arrMembers = new String[0];
			}
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			progressDialog.setMessage("正在提交中...");
			progressDialog.show();
			// 创建群组
			createGroup();
			break;
		case R.id.z_add_member:
			// 点击添加成员
			Intent intent = new Intent(this, GroupAddMemberUI.class);
			startActivityForResult(intent, 0);
			break;
		default:
			break;
		}
	}

	/**
	 * 创建群组
	 */
	private void createGroup() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				/**
				 * 创建群组
				 * 
				 * @param groupName
				 *            群组名称
				 * @param desc
				 *            群组简介
				 * @param allMembers
				 *            群组初始成员，如果只有自己传空数组即可
				 * @param reason
				 *            邀请成员加入的reason
				 * @param option
				 *            群组类型选项，可以设置群组最大用户数(默认200)及群组类型
				 *            option.inviteNeedConfirm表示邀请对方进群是否需要对方同意
				 *            ，默认是需要用户同意才能加群的。
				 *            option.extField创建群时可以为群组设定扩展字段，方便个性化订制。
				 * @return 创建好的group
				 * @throws HyphenateException
				 */
				try {
					EMGroupOptions option = new EMGroupOptions();
					option.maxUsers = 1000;
					option.inviteNeedConfirm = true;
					option.style = EMGroupStyle.EMGroupStylePublicOpenJoin;
					String reason = GroupCreateUI.this.getString(R.string.invite_join_group);
					reason = EMClient.getInstance().getCurrentUser() + reason + groupName;
					EMGroup createGroup = EMClient.getInstance().groupManager().createGroup(groupName, desc, arrMembers, reason, option);

					EMMessage message = EMMessage.createTxtSendMessage("可以开始聊天了", createGroup.getGroupId());
					message.setChatType(ChatType.GroupChat);
					EMClient.getInstance().chatManager().sendMessage(message);
					runOnUiThread(new Runnable() {
						public void run() {
							MyApplication.getInstance().showToast("创建群组成功");
							back();
						}
					});
				} catch (final HyphenateException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							MyApplication.getInstance().showToast(e.getMessage().toString());
						}
					});
				}
				progressDialog.dismiss();
			}
		}).start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 移除用户
				if (listMap != null && listMap.size() > 0) {
					listMap.remove(msg.arg1);
					adapter.setData(listMap);
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Type listTemp = new TypeToken<List<Map<String, String>>>() {
		}.getType();
		listMap = gson.fromJson(ACache.get(this).getAsString("select_member"), listTemp);
		if (listMap != null && listMap.size() > 0) {
			adapter.setData(listMap);
		}
	};

	@Override
	protected void back() {
		finish();
	}
}
