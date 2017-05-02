package edu.mum.waa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Crawlers on 5/1/2017.
 */
public class FileHandler {
    public static String readFile(String fileName) throws Exception{
        String filePath = PathConstant.DOCUMENT_ROOT+fileName;
        File file = new File(filePath);

        if (!file.exists()){
            return null;
        }
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        StringBuilder content = new StringBuilder(1024);
        String line;
        while((line = in.readLine()) != null)
        {
            content.append(line);
        }
        in.close();

        return content.toString();
    }
}
