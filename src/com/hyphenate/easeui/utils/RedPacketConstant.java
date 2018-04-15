package com.hyphenate.easeui.utils;

public class RedPacketConstant {
	// 以下常量值切勿更改
	public static final String MESSAGE_ATTR_IS_RED_PACKET_MESSAGE = "is_money_msg";// 是否是红包消息
	public static final String EXTRA_RED_PACKET_TYPE = "red_packet_type";// 红包类型0 个人   1群普通  2群拼手气
	public static final String EXTRA_RED_PACKET_ID = "ID";// 红包ID 也就是订单ID
	public static final String EXTRA_RED_PACKET_RECEIVER_ID = "money_receiver_id";// 目标ID
	public static final String EXTRA_RED_PACKET_GREETING = "money_greeting";// 这个是 对红包的描述   恭喜发财之类的文字
	public static final String EXTRA_SPONSOR_NAME = "money_sponsor_name";// 红包名称
	
	public static final String REFRESH_GROUP_RED_PACKET_ACTION = "refresh_group_money_action";// 发送回执action
	public static final String EXTRA_RED_PACKET_SENDER_NAME = "money_sender";
	public static final String EXTRA_RED_PACKET_RECEIVER_NAME = "money_receiver";
	public static final String EXTRA_RED_PACKET_SENDER_ID = "money_sender_id";
	public static final String EXTRA_RED_PACKET_GROUP_ID = "money_from_group_id";
	public static final String MESSAGE_ATTR_IS_RED_PACKET_ACK_MESSAGE = "is_open_money_msg";
}
