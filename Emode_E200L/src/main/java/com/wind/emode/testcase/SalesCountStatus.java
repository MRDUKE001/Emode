package com.wind.emode.testcase;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.wind.emode.R;
import com.wind.emode.NvRAMAgentUtils;

public class SalesCountStatus extends Activity {
	private TextView tv_state;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_salescount_status);
		Button btn = (Button) findViewById(R.id.btn);
		tv_state = (TextView) findViewById(R.id.tv_state);
		boolean state = isSalesCountEnabled();
		if (state) {
			tv_state.setText(getString(R.string.sales_count_status)
					+ getString(R.string.status_on));
		} else{
			tv_state.setText(getString(R.string.sales_count_status)
					+ getString(R.string.status_off));
		}
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setSalesCountEnabled(true);
				tv_state.setText(getString(R.string.sales_count_status)
						+ getString(R.string.status_on));
			}
		});
		Button btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setSalesCountEnabled(false);
				tv_state.setText(getString(R.string.sales_count_status)
						+ getString(R.string.status_off));
			}
		});
		// btn.setEnabled(!state);
	}

	private boolean isSalesCountEnabled() {
		byte[] buff = NvRAMAgentUtils.readFile();
		if (buff != null && buff.length > 125) {
			return buff[124] != 'Y';
		}
		return false;
	}

	private void setSalesCountEnabled(boolean enable) {
		byte[] buff = NvRAMAgentUtils.readFile();
		if (buff == null || buff.length < 125) {
			return;
		}
		buff[124] = (byte) (enable ? 'N' : 'Y');
		NvRAMAgentUtils.writeFile(buff);
	}

}
