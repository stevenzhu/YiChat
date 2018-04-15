package cms_list.cms_list_1.code;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.shorigo.utils.BitmapHelp;
import com.shorigo.utils.ScreenUtils;

/**
 * 朋友圈- 九宫格图片
 * 
 * @Description: 显示1~N张图片的View
 * @author peidongxu
 */

public class MultiImageView extends LinearLayout {
	private Context context;
	// 控件最大宽度
	private int MAX_WIDTH = 0;

	// 照片的Url列表
	private List<String> listImg;

	/** 长度 单位为Pixel **/
	private int pxMoreWandH = 0;// 多张图的宽高
	private int pxImagePadding = ScreenUtils.dp2px(getContext(), 3);// 图片间的间距

	private int MAX_PER_ROW_COUNT = 3;// 每行显示最大数

	private LayoutParams onePicPara;
	private LayoutParams morePara, moreParaColumnFirst;
	private LayoutParams rowPara;

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		mOnItemClickListener = onItemClickListener;
	}

	public MultiImageView(Context context) {
		super(context);
		this.context = context;
	}

	public MultiImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void setList(List<String> listImg) throws IllegalArgumentException {
		if (listImg == null) {
			throw new IllegalArgumentException("imageList is null...");
		}
		this.listImg = listImg;

		if (MAX_WIDTH > 0) {
			pxMoreWandH = (MAX_WIDTH - pxImagePadding * 2) / 3; // 解决右侧图片和内容对不齐问题
			initImageLayoutParams();
		}

		initView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (MAX_WIDTH == 0) {
			int width = measureWidth(widthMeasureSpec);
			if (width > 250) {
				MAX_WIDTH = width;
				if (listImg != null && listImg.size() > 0) {
					setList(listImg);
				}
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * 获取控件宽度
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private void initImageLayoutParams() {
		int wrap = LayoutParams.WRAP_CONTENT;
		int match = LayoutParams.MATCH_PARENT;

		onePicPara = new LayoutParams(pxMoreWandH * 2 + pxMoreWandH / 2, pxMoreWandH * 2 + pxMoreWandH / 2);

		moreParaColumnFirst = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara = new LayoutParams(pxMoreWandH, pxMoreWandH);
		morePara.setMargins(pxImagePadding, 0, 0, 0);

		rowPara = new LayoutParams(match, wrap);
	}

	// 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
	private void initView() {
		this.setOrientation(VERTICAL);
		this.removeAllViews();
		if (MAX_WIDTH == 0) {
			addView(new View(getContext()));
			return;
		}

		if (listImg == null || listImg.size() == 0) {
			return;
		}

		if (listImg.size() == 1) {
			addView(createImageView(0, false));
		} else {
			int allCount = listImg.size();
			if (allCount == 4) {
				MAX_PER_ROW_COUNT = 2;
			} else {
				MAX_PER_ROW_COUNT = 3;
			}
			int rowCount = allCount / MAX_PER_ROW_COUNT + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// 行数
			for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
				LinearLayout rowLayout = new LinearLayout(getContext());
				rowLayout.setOrientation(LinearLayout.HORIZONTAL);

				rowLayout.setLayoutParams(rowPara);
				if (rowCursor != 0) {
					rowLayout.setPadding(0, pxImagePadding, 0, 0);
				}

				int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT : allCount % MAX_PER_ROW_COUNT;// 每行的列数
				if (rowCursor != rowCount - 1) {
					columnCount = MAX_PER_ROW_COUNT;
				}
				addView(rowLayout);

				int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
				for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
					int position = columnCursor + rowOffset;
					rowLayout.addView(createImageView(position, true));
				}
			}
		}
	}

	private ImageView createImageView(int position, final boolean isMultiImage) {
		String url = listImg.get(position);
		ImageView imageView = new ColorFilterImageView(getContext());
		if (isMultiImage) {
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
		} else {
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			// imageView.setMaxHeight(pxOneMaxWandH);

			imageView.setLayoutParams(onePicPara);
		}

		imageView.setId(url.hashCode());
		imageView.setOnClickListener(new ImageOnClickListener(position));
		// imageView.setBackgroundColor(getResources().getColor(R.color.im_font_color_text_hint));
		BitmapHelp.loadImg(context, imageView, url);

		return imageView;
	}

	private class ImageOnClickListener implements View.OnClickListener {

		private int position;

		public ImageOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClick(view, position);
			}
		}
	}

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}
}