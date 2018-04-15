package imageselector.imageselector_1.code;

import java.io.Serializable;
import android.content.Context;
import android.widget.ImageView;

public interface ImageLoaderBase extends Serializable {
	void displayImage(Context context, String path, ImageView imageView);
}