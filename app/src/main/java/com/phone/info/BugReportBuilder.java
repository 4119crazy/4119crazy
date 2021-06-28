package com.phone.info;

import android.content.Context;
import android.provider.SyncStateContract;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class BugReportBuilder {

    protected Context context;
    protected File file;
    protected String content;

    public File getFile() {
        return file;
    }

    public BugReportBuilder setFileName(String fileName) {
        file = new File(context.getDir("properties", Context.MODE_PRIVATE), fileName);
        file.deleteOnExit();
        return this;
    }

    public BugReportBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public BugReportBuilder(Context context) {
        this.context = context;
    }

    public BugReportBuilder build() {
        if (null == file) {
            //Logger.e("No file specified");
            return this;
        }
        file.delete();
        write(content);
        return this;
    }

    protected void write(String content) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
        } catch (IOException e) {
            //Logger.e( "Could not write to temp file: " + e.getMessage());
        } finally {
//            Util.closeSilently(bw);
            try {
                bw.close();
            } catch (IOException e) {
                //Logger.e(e.toString());
//                e.printStackTrace();
            }
        }
    }
}
