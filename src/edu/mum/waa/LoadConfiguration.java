package edu.mum.waa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Crawlers on 5/2/2017.
 */
public class LoadConfiguration {
    public static String welcome;
    public static String contact;
    public static void loader(){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = System.getProperty("user.dir")+File.separator+"config.properties";

            File initialFile = new File(filename);
            input = new FileInputStream(initialFile);
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out
            welcome = prop.getProperty("/welcome");
            contact = prop.getProperty("/contacts");
            System.out.println(prop.getProperty("/welcome"));
            System.out.println(prop.getProperty("/contacts"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
