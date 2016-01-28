package com.wind.emode.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.support.v4.content.FileProvider;

import android.content.Context;
import android.net.Uri;

public class FileUtils {
	private static final String PHOTO_DATE_FORMAT = "'IMG'_yyyyMMdd_HHmmss";
	
	public static Uri generateTempPhotoUri(Context context) {
		return FileProvider.getUriForFile(context, "com.wind.emode.files", 
				new File(pathForTempPhoto(context, generateTempPhotoFileName())));
	}
	
	private static String pathForTempPhoto(Context context, String fileName) {
		final File dir = context.getCacheDir();
		dir.mkdirs();
		final File f = new File(dir, fileName);
		return f.getAbsolutePath();
	}
	
	private static String generateTempPhotoFileName() {
		final Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT, Locale.US);
		return "Photo-" + dateFormat.format(date) + ".jpg";
	}
}
