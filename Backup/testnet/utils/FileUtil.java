package com.hanyu.hust.testnet.utils;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * SD卡文件操作类
 */
public class FileUtil {
	
	private static final String TAG = "FileUtil";
	
	private static boolean exit = false;

	/**
	 * 复制整个文件夹
	 * @param from 原路径
	 * @param to 目的路径
	 * @throws Exception
	 */
	public static void MoveFolderAndFileWithSelf(String from, String to) throws Exception {
		try {
			File dir = new File(from);
			// 目标
			to +=  File.separator + dir.getName();
			File moveDir = new File(to);
			if(dir.isDirectory()){
				if (!moveDir.exists()) {
					moveDir.mkdirs();
				}
			}else{
				File tofile = new File(to);
				dir.renameTo(tofile);
				return;
			}
						
			// 文件一览
			File[] files = dir.listFiles();
			if (files == null)
				return;

			// 文件移动
			for (int i = 0; i < files.length; i++) {
				System.out.println("文件名："+files[i].getName());
				if (files[i].isDirectory()) {
					MoveFolderAndFileWithSelf(files[i].getPath(), to);
					// 成功，删除原文件
					files[i].delete();
				}
				File moveFile = new File(moveDir.getPath() + File.separator + files[i].getName());
				// 目标文件夹下存在的话，删除
				if (moveFile.exists()) {
					moveFile.delete();
				}
				files[i].renameTo(moveFile);
			}
			dir.delete();
		} catch (Exception e) {
			throw e;
		}
	}
	
	
    /**
     * 复制单个文件(可更名复制)
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名(注：目录路径需带文件名)
     * @return
     */
    public static void CopySingleFile(String oldPathFile, String newPathFile) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPathFile);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPathFile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * 复制单个文件(原名复制)
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名(注：目录路径需带文件名)
     * @return
     */
    public static void CopySingleFileTo(Handler handler, String oldPathFile, String targetPath, ProgressDialog pd, int code) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPathFile);
      
            pd.setMax((int) oldfile.length());
       
        	String targetfile = targetPath + File.separator +  oldfile.getName();
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPathFile); //读入原文件
                FileOutputStream fs = new FileOutputStream(targetfile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                    pd.setProgress(bytesum);
                }
                inStream.close();
            }
        } catch (Exception e) {
        	e.printStackTrace();
        	handler.sendEmptyMessage(-2);
        }finally{
        	pd.dismiss();        	
        }       
    }

	/**
	 * 复制整个文件夹的内容(含自身)
	 * @param handler
	 * @param srcFile 准备拷贝的目录
	 * @param destFilePath 指定绝对路径的新目录
	 * @param pd 进度条
	 * @param code 标示码
	 */
    public static void copyFolderWithSelf(Handler handler, String srcFile, String destFilePath, ProgressDialog pd, int code) {        	       

		LogUtil.d(TAG, "srcFile is :" + srcFile );
    	File dir = new File(srcFile);               	           	
        new File(destFilePath).mkdirs();                                   
        
		LogUtil.d(TAG, "destFilePath is :" + destFilePath );
		
        if (destFilePath.endsWith(File.separator))
        	destFilePath += dir.getName();
        else 
        	destFilePath +=  File.separator + dir.getName();            
        
		File moveDir = new File(destFilePath);
		if(dir.isDirectory()){
			if (!moveDir.exists()) {
				moveDir.mkdirs();
			}
		}
					
        String[] file = dir.list();                        
        if (pd != null)
        {			
        	if (file == null)
        	{
                if (pd != null)
                {
                	pd.dismiss();
                	handler.sendEmptyMessage(code);
                }
        		return;       		
        	}
        	LogUtil.d(TAG, "fileCount = " + file.length);						
        	pd.setMax(file.length);
        	pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					LogUtil.d(TAG, "cancel task");			
					exit = true;
				}
			});
        }
                            
        File temp = null;
        for (int i = 0; i < file.length; i++) {
            if (srcFile.endsWith(File.separator)) {
                temp = new File(srcFile + file[i]);
            } else {
                temp = new File(srcFile + File.separator + file[i]);
            }
            if (temp.isFile()) {
            	try {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(destFilePath +
                    		"/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();					
				} catch (Exception e) {
					LogUtil.e(TAG, "copy error");
				}
            }
            if (temp.isDirectory()) { 
            	copyFolderWithSelf(handler, srcFile + "/" + file[i], destFilePath, null,  code);
            }
            if (pd != null)
            {
            	pd.setProgress(i+1);
            	if (exit == true)
            	{
            		exit = false;
                	pd.dismiss();
                	handler.sendEmptyMessage(-1);
            		return;
            	}
            }
        }
        
        if (pd != null)
        {
        	pd.dismiss();
        	handler.sendEmptyMessage(code);
        }
    }

}
