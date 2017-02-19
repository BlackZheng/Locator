package com.blackzheng.app.locator.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by BlackZheng on 2017/2/18.
 */

public class FileUtil {

    public static String readLine(String filepath, int lineNumber){
        if(lineNumber < 1)
            return null;
        String line = null;
        FileReader fr= null;
        BufferedReader br = null;
        try {
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);
            for(int i = 0; i < lineNumber - 1; ++i)
                br.readLine();
            line = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fr != null){
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;

    }
}
