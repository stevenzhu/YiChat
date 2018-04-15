package group_update_name.group_update_name_1.code;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.shorigo.BaseUI;
import com.shorigo.utils.Utils;
import com.shorigo.yichat.R;

/**
 * 修改群的名称
 * 
 * @author peidongxu
 * 
 */
public class GroupEditNameUI extends BaseUI {
	private EditText et_content;

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.group_update_name_1);
	}

	@Override
	protected void findView_AddListener() {
		et_content = (EditText) findViewById(R.id.et_content);
	}

	@Override
	protected void prepareData() {
		setTitle("改变群名称");
		setRightButton("保存");

		String name = getIntent().getStringExtra("name");
		if (!Utils.isEmity(name)) {
			et_content.setText(name);
			et_content.setSelection(et_content.length());
		}
	}

	@Override
	protected void onMyClick(View v) {
		switch (v.getId()) {
		case R.id.z_right:
			Intent intent = new Intent();
			intent.putExtra("data", et_content.getText().toString());
			setResult(RESULT_OK, intent);
			back();
			break;
		default:
			break;
		}
	}

	@Override
	protected void back() {
		finish();
	}
}
