package im.jianghu.enableusbstoragemode;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	TextView curModeView;
	Button mtpBtn,usbBtn,rebootBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        curModeView = (TextView)findViewById(R.id.curMode);
        mtpBtn = (Button)findViewById(R.id.gotoMtpMode);
        usbBtn = (Button)findViewById(R.id.gotoUsbMode);
        rebootBtn = (Button)findViewById(R.id.rebootBtn);
        
        Process proc;
        String curMode = "";
		try {
			proc = Runtime.getRuntime().exec(new String[]{"/system/bin/getprop", "persist.sys.usb.config"});
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        String output = reader.readLine();
	        output = output.substring(0, output.indexOf(","));
	        if (output.equals("mtp")) {
	        	curMode = "MTP";
	        	mtpBtn.setEnabled(false);
	        } else if (output.equals("mass_storage")) {
	        	curMode = "USB存储模式";
	        	usbBtn.setEnabled(false);
	        } else {
	        	curMode = output;
	        }
	        if (curMode.equals("")) {
	        	curModeView.setText("当前模式：无法获取当前设置");
	        }else{
	        	curModeView.setText("当前模式："+curMode);
	        }
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mtpBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setUsbMode("mtp,adb");
				mtpBtn.setEnabled(false);
			}
			
		});
		usbBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setUsbMode("mass_storage,adb");
				usbBtn.setEnabled(false);
			}
			
		});
		rebootBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
			        Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot" });
			        proc.waitFor();
			    } catch (Exception e) {
			    	e.printStackTrace();
			    }
			}
			
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setUsbMode(String mode) {
    	try{
    	    Process su = Runtime.getRuntime().exec("su");
    	    DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

    	    outputStream.writeBytes("setprop persist.sys.usb.config "+mode+"\n");
    	    outputStream.flush();

    	    outputStream.writeBytes("exit\n");
    	    outputStream.flush();
    	    su.waitFor();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
