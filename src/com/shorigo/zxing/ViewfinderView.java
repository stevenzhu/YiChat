/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shorigo.zxing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.shorigo.yichat.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
public final class ViewfinderView extends View {
	/**
	 * ˢ�½����ʱ��
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * �ĸ���ɫ�߽Ƕ�Ӧ�ĳ���
	 */
	private int ScreenRate;

	/**
	 * �ĸ���ɫ�߽Ƕ�Ӧ�Ŀ��
	 */
	private static final int CORNER_WIDTH = 5;
	/**
	 * ɨ����е��м��ߵĿ��
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;

	/**
	 * ɨ����е��м��ߵ���ɨ������ҵļ�϶
	 */
	private static final int MIDDLE_LINE_PADDING = 5;

	/**
	 * �м�������ÿ��ˢ���ƶ��ľ���
	 */
	private static final int SPEEN_DISTANCE = 5;

	/**
	 * �ֻ����Ļ�ܶ�
	 */
	private static float density;
	/**
	 * �����С
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * �������ɨ�������ľ���
	 */
	private static final int TEXT_PADDING_TOP = 30;

	/**
	 * ���ʶ��������
	 */
	private Paint paint;

	/**
	 * �м们���ߵ����λ��
	 */
	private int slideTop;

	/**
	 * �м们���ߵ���׶�λ��
	 */
	private int slideBottom;

	/**
	 * ��ɨ��Ķ�ά��������������û��������ܣ���ʱ������
	 */
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private List<ResultPoint> possibleResultPoints;
	private List<ResultPoint> lastPossibleResultPoints;

	boolean isFirst;
	private Context context;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		density = context.getResources().getDisplayMetrics().density;
		// ������ת����dp
		ScreenRate = (int) (15 * density);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new ArrayList<ResultPoint>(5);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// �м��ɨ�����Ҫ�޸�ɨ���Ĵ�С��ȥCameraManager�����޸�
//		CameraManager.init(context);
		Rect frame = CameraManager.get().getFramingRect();
		if (frame == null) {
			return;
		}

		// ��ʼ���м��߻��������ϱߺ����±�
		if (!isFirst) {
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		// ��ȡ��Ļ�Ŀ�͸�
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// ����ɨ����������Ӱ���֣����ĸ����֣�ɨ�������浽��Ļ���棬ɨ�������浽��Ļ����
		// ɨ��������浽��Ļ��ߣ�ɨ�����ұߵ���Ļ�ұ�
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// ��ɨ�����ϵĽǣ��ܹ�8������
			paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left, frame.top, frame.left + ScreenRate, frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top + ScreenRate, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.top, frame.right, frame.top + CORNER_WIDTH, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top + ScreenRate, paint);
			canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left + ScreenRate, frame.bottom, paint);
			canvas.drawRect(frame.left, frame.bottom - ScreenRate, frame.left + CORNER_WIDTH, frame.bottom, paint);
			canvas.drawRect(frame.right - ScreenRate, frame.bottom - CORNER_WIDTH, frame.right, frame.bottom, paint);
			canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - ScreenRate, frame.right, frame.bottom, paint);

			// �����м����,ÿ��ˢ�½��棬�м���������ƶ�SPEEN_DISTANCE

			slideTop += SPEEN_DISTANCE;
			if (slideTop >= frame.bottom) {
				slideTop = frame.top;
			}
			Rect lineRect = new Rect();
			lineRect.left = frame.left;
			lineRect.right = frame.right;
			lineRect.top = slideTop;
			lineRect.bottom = slideTop + 18;
			canvas.drawBitmap(((BitmapDrawable) (getResources().getDrawable(R.drawable.qrcode_scan_line))).getBitmap(), null, lineRect, paint);

			// ��ɨ����������
			paint.setColor(Color.WHITE);
			paint.setTextSize(TEXT_SIZE * density);
			paint.setAlpha(0x40);
			paint.setTypeface(Typeface.create("System", Typeface.BOLD));
			String text = "将二维码放入框内, 即可自动扫描";
			float textWidth = paint.measureText(text);

			canvas.drawText(text, (width - textWidth) / 2, (float) (frame.bottom + (float) TEXT_PADDING_TOP * density), paint);

			List<ResultPoint> currentPossible = possibleResultPoints;
			List<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
//				synchronized (currentPossible) {
//					for (ResultPoint point : currentPossible) {
//						canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
//					}
//				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
//				synchronized (currentPossible) {
//					for (ResultPoint point : currentLast) {
//						canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
//					}
//				}
			}

			// ֻˢ��ɨ�������ݣ�����ط���ˢ��
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

	// private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192,
	// 128, 64};
	// private static final long ANIMATION_DELAY = 80L;
	// private static final int CURRENT_POINT_OPACITY = 0xA0;
	// private static final int MAX_RESULT_POINTS = 20;
	// private static final int POINT_SIZE = 6;
	//
	// private CameraManager cameraManager;
	// private final Paint paint;
	// private Bitmap resultBitmap;
	// private final int maskColor;
	// private final int resultColor;
	// private final int laserColor;
	// private final int resultPointColor;
	// private int scannerAlpha;
	// private List<ResultPoint> possibleResultPoints;
	// private List<ResultPoint> lastPossibleResultPoints;
	//
	// // This constructor is used when the class is built from an XML resource.
	// public ViewfinderView(Context context, AttributeSet attrs) {
	// super(context, attrs);
	//
	// // Initialize these once for performance rather than calling them every
	// time in onDraw().
	// paint = new Paint(Paint.ANTI_ALIAS_FLAG); //开启反锯齿
	// Resources resources = getResources();
	// maskColor = resources.getColor(R.color.viewfinder_mask);//遮盖层颜色
	// resultColor = resources.getColor(R.color.result_view);
	// laserColor = resources.getColor(R.color.viewfinder_laser);
	// resultPointColor = resources.getColor(R.color.possible_result_points);
	//
	//
	//
	//
	// scannerAlpha = 0;
	// possibleResultPoints = new ArrayList<ResultPoint>(5);
	// lastPossibleResultPoints = null;
	// }
	//
	// public void setCameraManager(CameraManager cameraManager) {
	// this.cameraManager = cameraManager;
	// }
	//
	// @SuppressLint("DrawAllocation")
	// @Override
	// public void onDraw(Canvas canvas) {
	// if (cameraManager == null) {
	// return; // not ready yet, early draw before done configuring
	// }
	// Rect frame = cameraManager.getFramingRect();//中间的取景框
	// Rect previewFrame =
	// cameraManager.getFramingRectInPreview();//zxing官方BarcodeScanner.apk解码成功时左上角存放截图的矩形框
	// if (frame == null || previewFrame == null) {
	// return;
	// }
	// int width = canvas.getWidth(); //手机屏幕宽度
	// int height = canvas.getHeight();//屏幕高度
	//
	// //取景画面中一共分为两块：外边半透明的一片(阴影部分)，中间全透明的一片。外面半透明的画面是由四个矩形组成(扫描框的
	// 上面到屏幕上面，扫描框的下面到屏幕下面,扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边)
	// // Draw the exterior (i.e. outside the framing rect) darkened
	// paint.setColor(resultBitmap != null ? resultColor : maskColor);
	// canvas.drawRect(0, 0, width, frame.top, paint);//上方矩形
	// canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);//左边
	// canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
	// paint);//右边
	// canvas.drawRect(0, frame.bottom + 1, width, height, paint);//下方
	//
	// if (resultBitmap != null) {
	// // Draw the opaque result bitmap over the scanning rectangle
	// paint.setAlpha(CURRENT_POINT_OPACITY);
	// canvas.drawBitmap(resultBitmap, null, frame, paint);
	// } else {
	//
	// // Draw a red "laser scanner" line through the middle to show decoding is
	// active
	// paint.setColor(laserColor);
	// paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
	// scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
	// int middle = frame.height() / 2 + frame.top;
	// canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2,
	// paint);
	//
	// float scaleX = frame.width() / (float) previewFrame.width();
	// float scaleY = frame.height() / (float) previewFrame.height();
	//
	// List<ResultPoint> currentPossible = possibleResultPoints;
	// List<ResultPoint> currentLast = lastPossibleResultPoints;
	// int frameLeft = frame.left;
	// int frameTop = frame.top;
	// if (currentPossible.isEmpty()) {
	// lastPossibleResultPoints = null;
	// } else {
	// possibleResultPoints = new ArrayList<ResultPoint>(5);
	// lastPossibleResultPoints = currentPossible;
	// paint.setAlpha(CURRENT_POINT_OPACITY);
	// paint.setColor(resultPointColor);
	// synchronized (currentPossible) {
	// for (ResultPoint point : currentPossible) {
	// canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
	// frameTop + (int) (point.getY() * scaleY),
	// POINT_SIZE, paint);
	// }
	// }
	// }
	// if (currentLast != null) {
	// paint.setAlpha(CURRENT_POINT_OPACITY / 2);
	// paint.setColor(resultPointColor);
	// synchronized (currentLast) {
	// float radius = POINT_SIZE / 2.0f;
	// for (ResultPoint point : currentLast) {
	// canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
	// frameTop + (int) (point.getY() * scaleY),
	// radius, paint);
	// }
	// }
	// }
	//
	// // Request another update at the animation interval, but only repaint the
	// laser line,
	// // not the entire viewfinder mask.
	// postInvalidateDelayed(ANIMATION_DELAY,
	// frame.left - POINT_SIZE,
	// frame.top - POINT_SIZE,
	// frame.right + POINT_SIZE,
	// frame.bottom + POINT_SIZE);
	// }
	// }
	//
	// public void drawViewfinder() {
	// Bitmap resultBitmap = this.resultBitmap;
	// this.resultBitmap = null;
	// if (resultBitmap != null) {
	// resultBitmap.recycle();
	// }
	// invalidate();
	// }
	//
	// /**
	// * Draw a bitmap with the result points highlighted instead of the live
	// scanning display.
	// *
	// * @param barcode An image of the decoded barcode.
	// */
	// public void drawResultBitmap(Bitmap barcode) {
	// resultBitmap = barcode;
	// invalidate();
	// }
	//
	// public void addPossibleResultPoint(ResultPoint point) {
	// List<ResultPoint> points = possibleResultPoints;
	// synchronized (points) {
	// points.add(point);
	// int size = points.size();
	// if (size > MAX_RESULT_POINTS) {
	// // trim it
	// points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
	// }
	// }
	// }

}
