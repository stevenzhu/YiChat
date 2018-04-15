package imageselector.imageselector_1.code;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class XutilsLoader implements ImageLoaderBase {

	@Override
	public void displayImage(Context context, String path, ImageView imageView) {
		Glide.with(context).load(path).override(100, 100).centerCrop().into(imageView);
	}

}
