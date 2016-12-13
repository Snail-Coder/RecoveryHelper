package com.hq.recoveryhelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "RecoveryHelper";
	
    private static File RECOVERY_DIR = new File("/cache/recovery");
    private static File COMMAND_FILE = new File(RECOVERY_DIR, "command");
    // PowerManager.REBOOT_RECOVERY
    private static final String REBOOT_RECOVERY = "recovery";
    
    private static final String DEFAULT_OTA_FILENAME = Environment.getExternalStorageDirectory().getPath() 
    		+ "/dload/update.zip";
    private static final String DEFAULT_COMMAND_STR = "--update_package=" + DEFAULT_OTA_FILENAME;
    
	Button startOTABt;
	EditText cacheCommand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		startOTABt = (Button) findViewById(R.id.start_ota_bt);
		startOTABt.setOnClickListener(this);
		
		cacheCommand = (EditText) findViewById(R.id.ota_command);
		cacheCommand.setText(DEFAULT_COMMAND_STR);
	}

	@Override
	public void onClick(View view) {
		String commandStr = cacheCommand.getText().toString();
		try {
			bootCommand(this, commandStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    private void bootCommand(Context context, String cmdStr) throws IOException {
    	
    	Log.d(TAG, "bootCommand: " + cmdStr);
    	
        RECOVERY_DIR.mkdirs();  // In case we need it
        COMMAND_FILE.delete();  // In case it's not writable

        FileWriter command = new FileWriter(COMMAND_FILE);
        try {
            /*for (String arg : args) {
                if (!TextUtils.isEmpty(arg)) {
                    command.write(arg);
                    command.write("\n");
                }
            }*/
        	command.write(cmdStr);
        } finally {
            command.close();
        }

        // Having written the command file, go ahead and reboot
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot(REBOOT_RECOVERY);

        throw new IOException("Reboot failed (no permissions?)");
    }

}
