package com.cyun.tracker.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    /**
     * 保存图片到本地图库, 不保存当前页面产品名字
     */
    public static void saveImage(final Context context, final String picURL) {
        saveImage(context, picURL, "");
    }

    /**
     * 保存图片到本地图库
     *
     * @param picURL  图片的url地址
     * @param proName 产品名称
     */
    public static void saveImage(final Context context, final String picURL,
                                 final String proName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(picURL);
                    InputStream is = url.openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    saveImage(context, bitmap, proName);
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        TsUtil.showToast("图片保存至" + Finals.Path_Image);
    }

    /**
     * 指定保存的路径
     *
     * @param mContext
     * @param bm       图
     * @param lineName 产品名称
     * @throws IOException
     */
    public static void saveImage(Context mContext, Bitmap bm, String lineName)
            throws IOException {
        File dirFile = new File(Finals.Path_Image);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String imagePath = Finals.Path_Image + lineName + ".jpg";
        File myCaptureFile = new File(imagePath);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();

        // TODO, 放到root目录下，不需要将文件插入到系统图库，
        // (其次把文件插入到系统图库)
        // try {
        // MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
        // myCaptureFile.getAbsolutePath(), fileName, null);
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }
        // 最后通知图库更新
        mContext.sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                + imagePath)));

    }

    /**
     * 扫描、刷新相册
     */
    public static void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);

    }


    /**
     * 删除文件或者文件夹
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i].getPath());
                }
                file.delete();
            }
        }
    }

    /**
     * 检查对应目录下面是否有包含文件名fileName的文件
     */
    public static boolean checkHasFile(String filePath, String fileName) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getAbsolutePath().contains(fileName)) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * 获取文件by名称
     */
    public static String getFileByName(String unzipFilePath, String fileName) {
        File file = new File(unzipFilePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getAbsolutePath().contains(fileName)) {
                        return files[i].getAbsolutePath();
                    }

                }
            }
        }
        return "";
    }


    /**
     * 读取文件内容转换成字符串
     */
    public static String fileToString(String filePath) {
        BufferedReader br;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    public static boolean Unzip(String zipFile, String targetDir) {
        int BUFFER = 4096; // 这里缓冲区我们使用4KB，
        String strEntry; // 保存每个zip的条目名称

        try {
            BufferedOutputStream dest = null; // 缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(fis));
            ZipEntry entry; // 每个zip条目的实例

            while ((entry = zis.getNextEntry()) != null) {

                try {
                    Log.i("Unzip: ", "=" + entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }

                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
            return true;
        } catch (Exception cwj) {
            cwj.printStackTrace();
            return false;
        }
    }

    /**
     * 生成文件
     *
     * @param filePath
     * @param fileName
     * @return
     */
    private static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 生成文件夹
     *
     * @param filePath
     */
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



}
