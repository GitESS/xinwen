package hsb.ess.xinwen.ui;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(socketTimeout = 5000, formKey = "", httpMethod = org.acra.sender.HttpSender.Method.PUT, reportType = org.acra.sender.HttpSender.Type.JSON, formUri = "http://acme.hemant.iriscouch.com/acra-xinwen/_design/acra-storage/_update/report", formUriBasicAuthLogin = "heman", formUriBasicAuthPassword = "password12345")
public class xinwenApplication extends Application {

	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}
}
