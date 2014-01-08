package com.example.jsonparseutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class GetImageFromServerUtils {
	private static final String FILEPATH = "/jsonParse_image/";
	private static File file = null;
	private static Bitmap bitmap = null;
	private static String sdcardState = null;

	/**
	 * @param url
	 *            从服务器获取图片的url地址
	 * @param imageName
	 *            用于标示图片用的名字
	 * @return 返回获取的Bitmap图片
	 */
	public static Bitmap getBitmap(String url, int imageId) {
		sdcardState = Environment.getExternalStorageState();
		if (sdcardState.equals(Environment.MEDIA_MOUNTED)) {// 判断sdcard是否可用
			file = new File(Environment.getExternalStorageDirectory().getPath()
					+ FILEPATH);
			if (file.exists()) {// 如果文件存在就从缓存中获取图片
				bitmap = getBitmapFromCache(imageId);
				if (bitmap != null) {
					return bitmap;
				} else {
					bitmap = getBitmapFromServer(url);
					saveBitmapToSdcard(bitmap, imageId);
					return bitmap;
				}
			} else {// 如果文件不存在，就从服务器获取图片，当sdcard可用空间大于10M时，将图片保存至指定文件夹。
				// StatFs statfs = new StatFs(SDCARD);
				// long sdcardAvailableSize = statfs.getAvailableBytes();
				// if (sdcardAvailableSize > 10485760) {
				file.mkdir();
				bitmap = getBitmapFromServer(url);
				saveBitmapToSdcard(bitmap, imageId);
				return bitmap;
			}
		} else {// 如果sdcard 不可用就从服务器获取图片。
			bitmap = getBitmapFromServer(url);
			return bitmap;
		}
	}

	/**
	 * @param bitmap
	 *            从服务器获取的图片
	 * @param imageName
	 *            用于保存图片用的名字
	 */
	private static void saveBitmapToSdcard(Bitmap bitmap, int imageName) {
		try {
			File saveImage = new File(file + File.separator + imageName
					+ ".jpg");
			saveImage.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(saveImage);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
			fileOutputStream.close();
			System.out.println("保存到sdcard成功");// /////////////////////////
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("保存失败");// /////////////////////////
		}
	}

	/**
	 * @param imageName
	 *            用于查找图片用的标识
	 * @return 返回查找到的图片，若没有则返回null
	 */
	public static Bitmap getBitmapFromCache(int imageId) {
		try {
			File mfile = new File(file + File.separator + imageId + ".jpg");
			FileInputStream fileInputStream = new FileInputStream(mfile);
			Bitmap bp = BitmapFactory.decodeStream(fileInputStream);
			fileInputStream.close();
			System.out.println("获取成功" + bp);
			return bp;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("获取失败");
			return null;
		}
	}
//清空sdcard保存的图片
	public static boolean clearCache(){
		File cache =new File(file+File.separator);
		if(cache.exists()){
			if(cache.length()==0){
//				cache.delete();
				return true;
			}else{
				File[] files=cache.listFiles();
				for(File item:files){
					item.delete();
				}
//				cache.delete();
				return true;
			}
		}
		System.out.println("cache is not exsit!");
		return false;
	}
	/**
	 * @param url
	 *            从服务器获取图片的url
	 * @return 返回获得的Bitmap图片
	 */
	public static Bitmap getBitmapFromServer(String url) {
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			InputStream is = conn.getInputStream();
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
