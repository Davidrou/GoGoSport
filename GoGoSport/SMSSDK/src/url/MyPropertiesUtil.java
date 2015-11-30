package url;

import java.io.InputStream;
import java.util.Properties;

import android.content.Context;

public class MyPropertiesUtil {

	private static Properties urlProperties;
	public static Properties getProperties(Context c) {
		Properties properties = new Properties();
		InputStream is = MyPropertiesUtil.class.getResourceAsStream("/assets/u.properties");
		try {
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		urlProperties = properties;
		return urlProperties;
	}
	
}
