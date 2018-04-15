package com.shorigo.view.input;

import java.util.Map;

import com.hyphenate.easeui.domain.EaseEmojicon;

public class EaseEmojiconInputProvider {

	/**
	 * the global EaseEmojiconInputProvider instance
	 */
	private static EaseEmojiconInputProvider instance = null;

	/**
	 * get instance of EaseEmojiconInputProvider
	 * 
	 * @return
	 */
	public synchronized static EaseEmojiconInputProvider getInstance() {
		if (instance == null) {
			instance = new EaseEmojiconInputProvider();
		}
		return instance;
	}

	/**
	 * Emojicon provider
	 * 
	 */
	public interface EaseEmojiconInfoProvider {
		/**
		 * return EaseEmojicon for input emojiconIdentityCode
		 * 
		 * @param emojiconIdentityCode
		 * @return
		 */
		EaseEmojicon getEmojiconInfo(String emojiconIdentityCode);

		/**
		 * get Emojicon map, key is the text of emoji, value is the resource id
		 * or local path of emoji icon(can't be URL on internet)
		 * 
		 * @return
		 */
		Map<String, Object> getTextEmojiconMapping();
	}

	private EaseEmojiconInfoProvider emojiconInfoProvider;

	/**
	 * Emojicon provider
	 * 
	 * @return
	 */
	public EaseEmojiconInfoProvider getEmojiconInfoProvider() {
		return emojiconInfoProvider;
	}

	/**
	 * set Emojicon provider
	 * 
	 * @param emojiconInfoProvider
	 */
	public void setEmojiconInfoProvider(EaseEmojiconInfoProvider emojiconInfoProvider) {
		this.emojiconInfoProvider = emojiconInfoProvider;
	}
}
