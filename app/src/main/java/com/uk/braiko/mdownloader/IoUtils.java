package com.uk.braiko.mdownloader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;

public class IoUtils {

	public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver) {
		String filePath;
		String[] filePathColumn = { MediaColumns.DATA };

		Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		filePath = cursor.getString(columnIndex);
		cursor.close();
		return filePath;
	}

	public static String gzipToString(GZIPInputStream _is) {

		String response = "";
		try
		{
			InputStreamReader reader = new InputStreamReader(_is);
			BufferedReader in = new BufferedReader(reader);

			String readed;
			while ((readed = in.readLine()) != null)
			{
				response = response + "\n" + readed;
			}
		}
		catch (Exception e)
		{
		}
		return response;
	}

	public static final String md5(final String s) {
		try
		{
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
			{
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public static String[] readStringFromAssets(Context context, String path) {
		try
		{
			// char[] buffer = new char[16384];
			InputStream is = context.getAssets().open(path);
			ArrayList<String> mas = new ArrayList<String>();

			// Read text from file

			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 65535);
			String line;

			while ((line = br.readLine()) != null)
			{
				mas.add(line);
			}
			return mas.toArray(new String[mas.size()]);
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public static String getStringFromAssets(Context context, String path) {
		byte[] buffer = null;
		InputStream is;
		try
		{
			is = context.getAssets().open(path);
			int size = is.available();
			buffer = new byte[size];
			is.read(buffer);
			is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		try
		{
			String str_data = new String(buffer);
			return str_data;
		}
		catch (Exception e)
		{
			// TODO: handle exception
			return "";
		}
	}

	public static void deleteDir(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				deleteDir(child);

		fileOrDirectory.delete();
	}

	public static void writeFile(String sBody, String _fileName) {
		try
		{
			File gpxfile = new File(Environment.getExternalStorageDirectory(), _fileName);
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(sBody);
			writer.flush();
			writer.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static String[] getAvailableFonts(Context _context, String _fontFolder) {
		try
		{
			AssetManager assetManager = _context.getAssets();
			return assetManager.list(_fontFolder);
		}
		catch (Exception e)
		{
			return new String[0];
		}
	}

	public static String readFile(String _fileName) {
		try
		{

			InputStreamReader input = new InputStreamReader(new FileInputStream(_fileName));
			BufferedReader buffreader = new BufferedReader(input);
			String res = "";
			String line;
			while ((line = buffreader.readLine()) != null)
			{
				res += line;
			}
			input.close();
			return res;

		}
		catch (Exception e)
		{
			return "";
		}
	}

	public static File copyFile(String _name, InputStream _in) throws IOException {

		File result = new File(_name);

		try
		{
			result.createNewFile();
			OutputStream out = new FileOutputStream(result);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = _in.read(buffer)) > 0)
			{
				out.write(buffer, 0, length);
			}
			out.flush();
			out.close();
			_in.close();
		}
		catch (Exception e)
		{
			return null;
		}

		return result;
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		if (is != null)
		{
			Writer writer = new StringWriter();
			char[] buffer = new char[16384];
			try
			{
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 65535);
				int n;
				while ((n = reader.read(buffer)) != -1)
				{
					writer.write(buffer, 0, n);
				}
			}
			finally
			{
				is.close();
			}
			return writer.toString();
		}
		else
		{
			return "";
		}
	}

}
