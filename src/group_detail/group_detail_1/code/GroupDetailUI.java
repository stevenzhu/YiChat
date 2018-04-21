package group_detail.group_detail_1.code;

import group_add_member.group_add_member_1.code.GroupAddMemberUI;
import group_update_name.group_update_name_1.code.GroupEditNameUI;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import user_card.user_card_1.code.UserCardUI;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import chat.chat_1.code.ChatUI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.easeui.ui.EaseGroupListener;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseAlertDialog.AlertDialogUser;
import com.hyphenate.easeui.widget.EaseExpandGridView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.shorigo.BaseUI;
import com.shorigo.MyApplication;
import com.shorigo.custom_style.StyleUtils;
import com.shorigo.utils.ACache;
import com.shorigo.utils.AsyncTopImgLoadTask;
import com.shorigo.utils.Constants;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 群组详情
 * 
 * @author peidongxu
 * 
 */
public class GroupDetailUI extends BaseUI {
	private static final String TAG = "GroupDetailUI";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	public static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
	private static final int REQUEST_CODE_EDIT_GROUP_DESCRIPTION = 6;
	private static final int REQUEST_CODE_EDIT_GROUP_EXTENSION = 7;

	private String groupId;
	private Button tv_exit_group;
	private Button tv_exitdel_group;
	private EMGroup group;
	private GridAdapter membersAdapter;
	private ProgressDialog progressDialog;

	public static GroupDetailUI instance;

	String st = "";

	private ImageView iv_switch_groupmsg;
	private EMPushConfigs pushConfigs;

	private List<String> memberList = Collections.synchronizedList(new ArrayList<String>());

	GroupChangeListener groupChangeListener;

	@Override
	protected void loadViewLayout() {
		groupId = getIntent().getStringExtra("id");
		group = EMClient.getInstance().groupManager().getGroup(groupId);

		if (group == null) {
			back();
			return;
		}
		setContentView(R.layout.group_detail_1);
	}

	@Override
	protected void findView_AddListener() {
		RelativeLayout z_clear_all_history = (RelativeLayout) findViewById(R.id.z_clear_all_history);
		z_clear_all_history.setOnClickListener(this);
		tv_exit_group = (Button) findViewById(R.id.tv_exit_group);
		tv_exitdel_group = (Button) findViewById(R.id.tv_exitdel_group);
		RelativeLayout z_group_name = (RelativeLayout) findViewById(R.id.z_group_name);
		z_group_name.setOnClickListener(this);
		RelativeLayout z_group_desc = (RelativeLayout) findViewById(R.id.z_group_desc);
		z_group_desc.setOnClickListener(this);
		RelativeLayout z_switch_groupmsg = (RelativeLayout) findViewById(R.id.z_switch_groupmsg);
		z_switch_groupmsg.setOnClickListener(this);
		iv_switch_groupmsg = (ImageView) findViewById(R.id.iv_switch_groupmsg);
		StyleUtils.setTabBg(this, iv_switch_groupmsg, new int[] { R.drawable.group_detail_1_icon_switch_open, R.drawable.group_detail_1_icon_switch_off });
	}

	@Override
	protected void prepareData() {

		instance = this;
		st = getResources().getString(R.string.people);

		if (group.getOwner() == null || "".equals(group.getOwner()) || !group.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
			tv_exit_group.setVisibility(View.GONE);
			tv_exitdel_group.setVisibility(View.GONE);
		}
		if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
			tv_exit_group.setVisibility(View.GONE);
			tv_exitdel_group.setVisibility(View.VISIBLE);
		}

		pushConfigs = EMClient.getInstance().pushManager().getPushConfigs();
		groupChangeListener = new GroupChangeListener();
		EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);

		((TextView) findViewById(R.id.tv_title)).setText(group.getGroupName() + "(" + group.getMemberCount() + st);

		membersAdapter = new GridAdapter(this, R.layout.group_detail_1_item, new ArrayList<String>());
		EaseExpandGridView userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
		userGridview.setAdapter(membersAdapter);

		// 保证每次进详情看到的都是最新的group
		updateGroup();
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_switch_groupmsg:
			// 屏蔽或取消屏蔽群组
			toggleBlockGroup();
			break;
		case R.id.z_clear_all_history:
			// 清空聊天记录
			String st9 = getResources().getString(R.string.sure_to_empty_this);
			new EaseAlertDialog(GroupDetailUI.this, null, st9, null, new AlertDialogUser() {

				@Override
				public void onResult(boolean confirmed, Bundle bundle) {
					if (confirmed) {
						clearGroupHistory();
					}
				}
			}, true).show();
			break;
		case R.id.z_group_name:
			// 更改群组名称
			if (group != null) {
				Intent intent = new Intent(this, GroupEditNameUI.class);
				intent.putExtra("name", group.getGroupName());
				startActivityForResult(intent, REQUEST_CODE_EDIT_GROUPNAME);
			}
			break;
		case R.id.z_group_desc:
			// startActivityForResult(new Intent(this,
			// EditActivity.class).putExtra("data", group.getDescription()).
			// putExtra("title",
			// getString(R.string.change_the_group_description)).putExtra("editable",
			// isCurrentOwner(group)),
			// REQUEST_CODE_EDIT_GROUP_DESCRIPTION);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.being_added);
		String st2 = getResources().getString(R.string.is_quit_the_group_chat);
		String st3 = getResources().getString(R.string.chatting_is_dissolution);
		String st4 = getResources().getString(R.string.are_empty_group_of_news);
		final String st5 = getResources().getString(R.string.is_modify_the_group_name);
		final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
		final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);

		final String st8 = getResources().getString(R.string.is_modify_the_group_description);
		final String st9 = getResources().getString(R.string.Modify_the_group_description_successful);
		final String st10 = getResources().getString(R.string.change_the_group_description_failed_please);
		final String st11 = getResources().getString(R.string.Modify_the_group_extension_successful);
		final String st12 = getResources().getString(R.string.change_the_group_extension_failed_please);

		if (resultCode == RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(GroupDetailUI.this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// 添加群成员
				progressDialog.setMessage(st1);
				progressDialog.show();
				addMembersToGroup();
				break;
			case REQUEST_CODE_EXIT: // 退出群
				progressDialog.setMessage(st2);
				progressDialog.show();
				exitGrop();
				break;
			case REQUEST_CODE_EXIT_DELETE: // 解散群
				progressDialog.setMessage(st3);
				progressDialog.show();
				deleteGrop();
				break;
			case REQUEST_CODE_EDIT_GROUPNAME: // 修改群名称
				final String returnData = data.getStringExtra("data");
				if (!Utils.isEmity(returnData)) {
					progressDialog.setMessage(st5);
					progressDialog.show();

					new Thread(new Runnable() {
						public void run() {
							try {
								EMClient.getInstance().groupManager().changeGroupName(groupId, returnData);
								runOnUiThread(new Runnable() {
									public void run() {
										((TextView) findViewById(R.id.tv_title)).setText(group.getGroupName() + "(" + group.getMemberCount() + ")");
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
									}
								});

							} catch (HyphenateException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
					}).start();
				}
				break;
			case REQUEST_CODE_EDIT_GROUP_DESCRIPTION:
				final String returnData1 = data.getStringExtra("data");
				if (!Utils.isEmity(returnData1)) {
					progressDialog.setMessage(st5);
					progressDialog.show();

					new Thread(new Runnable() {
						public void run() {
							try {
								EMClient.getInstance().groupManager().changeGroupDescription(groupId, returnData1);
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
									}
								});
							} catch (HyphenateException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st10, Toast.LENGTH_SHORT).show();
									}
								});
							}
						}
					}).start();
				}
				break;
			case REQUEST_CODE_EDIT_GROUP_EXTENSION: {
				final String returnExtension = data.getStringExtra("data");
				if (!Utils.isEmity(returnExtension)) {
					progressDialog.setMessage(st5);
					progressDialog.show();

					new Thread(new Runnable() {
						public void run() {
							try {
								EMClient.getInstance().groupManager().updateGroupExtension(groupId, returnExtension);
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st11, Toast.LENGTH_SHORT).show();
									}
								});
							} catch (HyphenateException e) {
								e.printStackTrace();
								runOnUiThread(new Runnable() {
									public void run() {
										progressDialog.dismiss();
										Toast.makeText(getApplicationContext(), st12, Toast.LENGTH_SHORT).show();
									}
								});
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
	}

	private void debugList(String str, List<String> list) {
		EMLog.d(TAG, str);
		for (String member : list) {
			EMLog.d(TAG, "    " + member);
		}
	}

	private void refreshMembersAdapter() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				debugList("memberList", memberList);

				membersAdapter = new GridAdapter(GroupDetailUI.this, R.layout.group_detail_1_item, new ArrayList<String>());

				membersAdapter.clear();
				synchronized (memberList) {
					membersAdapter.addAll(memberList);
				}
				if (isCurrentOwner(group)) {
					memberList.add(null);
				}
				membersAdapter.notifyDataSetChanged();

				EaseExpandGridView userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
				userGridview.setAdapter(membersAdapter);
			}
		});
	}

	/**
	 * 点击退出群组按钮
	 * 
	 * @param view
	 */
	public void exitGroup(View view) {
		exitGrop();
		// startActivityForResult(new Intent(this, ExitGroupDialog.class),
		// REQUEST_CODE_EXIT);
	}

	/**
	 * 点击解散群组按钮
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
		deleteGrop();
		// startActivityForResult(new Intent(this,
		// ExitGroupDialog.class).putExtra("deleteToast",
		// getString(R.string.dissolution_group_hint)),
		// REQUEST_CODE_EXIT_DELETE);
	}

	/**
	 * 清空群聊天记录
	 */
	private void clearGroupHistory() {
		EMConversation conversation = EMClient.getInstance().chatManager().getConversation(group.getGroupId(), EMConversationType.GroupChat);
		if (conversation != null) {
			conversation.clearAllMessages();
		}
		Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 退出群组
	 */
	private void exitGrop() {
		createProgressDialog();
		progressDialog.setMessage(getString(R.string.is_quit_the_group_chat));
		progressDialog.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().groupManager().leaveGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if (ChatUI.activityInstance != null)
								ChatUI.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							MyApplication.getInstance().showToast("系统繁忙,请稍后尝试");
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 解散群组
	 * 
	 */
	private void deleteGrop() {
		final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
		createProgressDialog();
		progressDialog.setMessage(getString(R.string.chatting_is_dissolution));
		progressDialog.show();
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().groupManager().destroyGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							if (ChatUI.activityInstance != null)
								ChatUI.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							MyApplication.getInstance().showToast("系统繁忙,请稍后尝试");
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 增加群成员newmembers
	 */
	private void addMembersToGroup() {
		Gson gson = new Gson();
		Type listTemp = new TypeToken<List<Map<String, String>>>() {
		}.getType();
		List<Map<String, String>> listMap = gson.fromJson(ACache.get(this).getAsString("select_member"), listTemp);
		int size = listMap.size();
		final String[] arrNewMember = new String[size];
		for (int i = 0; i < size; i++) {
			arrNewMember[i] = Constants.PREFIX + listMap.get(i).get("user_id");
		}
		final String st6 = getResources().getString(R.string.Add_group_members_fail);
		createProgressDialog();
		progressDialog.setMessage(getString(R.string.being_added));
		progressDialog.show();
		new Thread(new Runnable() {

			public void run() {
				try {
					// 创建者调用add方法
					if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
						EMClient.getInstance().groupManager().addUsersToGroup(groupId, arrNewMember);
					} else {
						// 一般成员调用invite方法
						EMClient.getInstance().groupManager().inviteUser(groupId, arrNewMember, null);
					}
					updateGroup();
					refreshMembersAdapter();
					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.tv_title)).setText(group.getGroupName() + "(" + group.getMemberCount() + st);
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							MyApplication.getInstance().showToast("系统繁忙,请稍后尝试");
						}
					});
				}
			}
		}).start();
	}

	private ProgressDialog createProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(GroupDetailUI.this);
			progressDialog.setCanceledOnTouchOutside(false);
		}
		return progressDialog;
	}

	private void toggleBlockGroup() {
		if (iv_switch_groupmsg.isSelected()) {
			EMLog.d(TAG, "change to unblock group msg");
			createProgressDialog();
			progressDialog.setMessage(getString(R.string.Is_unblock));
			progressDialog.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
						runOnUiThread(new Runnable() {
							public void run() {
								iv_switch_groupmsg.setSelected(false);
								progressDialog.dismiss();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(getApplicationContext(), R.string.remove_group_of, Toast.LENGTH_LONG).show();
							}
						});

					}
				}
			}).start();

		} else {
			String st8 = getResources().getString(R.string.group_is_blocked);
			final String st9 = getResources().getString(R.string.group_of_shielding);
			EMLog.d(TAG, "change to block group msg");
			createProgressDialog();
			progressDialog.setMessage(st8);
			progressDialog.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						EMClient.getInstance().groupManager().blockGroupMessage(groupId);
						runOnUiThread(new Runnable() {
							public void run() {
								iv_switch_groupmsg.setSelected(true);
								progressDialog.dismiss();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(getApplicationContext(), st9, Toast.LENGTH_LONG).show();
							}
						});
					}

				}
			}).start();
		}
	}

	void setVisibility(Dialog viewGroups, int[] ids, boolean[] visibilities) throws Exception {
		if (ids.length != visibilities.length) {
			throw new Exception("");
		}

		for (int i = 0; i < ids.length; i++) {
			View view = viewGroups.findViewById(ids[i]);
			view.setVisibility(visibilities[i] ? View.VISIBLE : View.GONE);
		}
	}

	/**
	 * 群组成员gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;

		public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			res = textViewResourceId;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(res, null);
				holder.iv_item_avatar = (ImageView) convertView.findViewById(R.id.iv_item_avatar);
				holder.iv_item_add = (ImageView) convertView.findViewById(R.id.iv_item_add);
				holder.tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
				holder.iv_item_del = (ImageView) convertView.findViewById(R.id.iv_item_del);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (isCurrentOwner(group) && position == getCount() - 1) {
				// add button
				holder.tv_item_name.setText("");
				holder.iv_item_del.setVisibility(View.GONE);
				holder.iv_item_avatar.setVisibility(View.GONE);
				if (isCurrentOwner(group)) {
					holder.iv_item_add.setVisibility(View.VISIBLE);
					holder.iv_item_add.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 进入选人页面
							startActivity(new Intent(GroupDetailUI.this, GroupAddMemberUI.class));
						}
					});
				}
				return convertView;
			} else {
				// members
				final String username = getItem(position);
				new AsyncTopImgLoadTask(GroupDetailUI.this, username, holder.tv_item_name, holder.iv_item_avatar).execute();
				// EaseUserUtils.setUserNick(username, holder.tv_item_name);
				// EaseUserUtils.setUserAvatar(getContext(), username,
				// holder.iv_item_avatar);

				holder.iv_item_avatar.setVisibility(View.VISIBLE);
				holder.iv_item_add.setVisibility(View.GONE);
				holder.iv_item_avatar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(GroupDetailUI.this, UserCardUI.class);
						intent.putExtra("user_id", username.replace(Constants.PREFIX, ""));
						startActivity(intent);
					}
				});

				if (isCurrentOwner(group)) {
					holder.iv_item_del.setVisibility(View.VISIBLE);
					holder.iv_item_del.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										EMClient.getInstance().groupManager().removeUserFromGroup(groupId, username);
										runOnUiThread(new Runnable() {
											public void run() {
												updateGroup();
											}
										});
									} catch (HyphenateException e) {
										e.printStackTrace();
									}
								}
							}).start();
						}
					});
				} else {
					holder.iv_item_del.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		@Override
		public int getCount() {
			return super.getCount() + 1;
		}
	}

	boolean isCurrentOwner(EMGroup group) {
		String owner = group.getOwner();
		if (owner == null || owner.isEmpty()) {
			return false;
		}
		return owner.equals(EMClient.getInstance().getCurrentUser());
	}

	protected void updateGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
					if (pushConfigs == null) {
						EMClient.getInstance().pushManager().getPushConfigsFromServer();
					}

					try {
						group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
						memberList.clear();
						EMCursorResult<String> result = null;
						do {
							// page size set to 20 is convenient for testing,
							// should be applied to big value
							result = EMClient.getInstance().groupManager().fetchGroupMembers(groupId, result != null ? result.getCursor() : "", 20);
							EMLog.d(TAG, "fetchGroupMembers result.size:" + result.getData().size());
							memberList.addAll(result.getData());
						} while (result.getData().size() == 20);
					} catch (Exception e) {
					} finally {
					}

					try {
						EMClient.getInstance().groupManager().fetchGroupAnnouncement(groupId);
					} catch (HyphenateException e) {
						e.printStackTrace();
					}

					runOnUiThread(new Runnable() {
						public void run() {
							refreshMembersAdapter();

							// refreshUIVisibility();
							((TextView) findViewById(R.id.tv_title)).setText(group.getGroupName() + "(" + group.getMemberCount() + ")");

							if (isCurrentOwner(group)) {
								// 显示解散按钮
								tv_exit_group.setVisibility(View.GONE);
								tv_exitdel_group.setVisibility(View.VISIBLE);
							} else {
								// 显示退出按钮
								tv_exit_group.setVisibility(View.VISIBLE);
								tv_exitdel_group.setVisibility(View.GONE);
							}

							// update block
							EMLog.d(TAG, "group msg is blocked:" + group.isMsgBlocked());
							if (group.isMsgBlocked()) {
								iv_switch_groupmsg.setSelected(true);
							} else {
								iv_switch_groupmsg.setSelected(false);
							}

							boolean isOwner = isCurrentOwner(group);
							tv_exit_group.setVisibility(isOwner ? View.GONE : View.VISIBLE);
							tv_exitdel_group.setVisibility(isOwner ? View.VISIBLE : View.GONE);
						}
					});
				} catch (Exception e) {
				}
			}
		}).start();
	}

	@Override
	protected void back() {
		finish();
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onDestroy() {
		EMClient.getInstance().groupManager().removeGroupChangeListener(groupChangeListener);
		super.onDestroy();
		instance = null;

	}

	private static class ViewHolder {
		ImageView iv_item_avatar;
		ImageView iv_item_add;
		TextView tv_item_name;
		ImageView iv_item_del;
	}

	private class GroupChangeListener extends EaseGroupListener {

		@Override
		public void onInvitationAccepted(String groupId, String inviter, String reason) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					memberList = group.getMembers();
					refreshMembersAdapter();
				}
			});
		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			finish();
		}

		@Override
		public void onGroupDestroyed(String groupId, String groupName) {
			finish();
		}

		@Override
		public void onMuteListAdded(String groupId, final List<String> mutes, final long muteExpire) {
			updateGroup();
		}

		@Override
		public void onMuteListRemoved(String groupId, final List<String> mutes) {
			updateGroup();
		}

		@Override
		public void onAdminAdded(String groupId, String administrator) {
			updateGroup();
		}

		@Override
		public void onAdminRemoved(String groupId, String administrator) {
			updateGroup();
		}

		@Override
		public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
			updateGroup();
		}

		@Override
		public void onMemberJoined(String groupId, String member) {
			updateGroup();
		}

		@Override
		public void onMemberExited(String groupId, String member) {
			updateGroup();
		}

		@Override
		public void onAnnouncementChanged(String groupId, final String announcement) {
		}

		@Override
		public void onSharedFileAdded(String groupId, final EMMucSharedFile sharedFile) {
		}

		@Override
		public void onSharedFileDeleted(String groupId, String fileId) {
		}
	}

}
