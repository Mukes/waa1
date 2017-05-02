package edu.mum.waa;

/**
 * Created by Crawlers on 5/1/2017.
 */
public class DynamicClass {
    public static String dynamicResponse(String uri){
        StringBuilder response = new StringBuilder();
        String className;
        String generatedText;
        if (uri.contains("/welcome.web")){
            //className = Welcome.class.getName();
            className = LoadConfiguration.welcome;
        }else if (uri.contains("/contacts.web")){
            //className = Contacts.class.getName();
            className = LoadConfiguration.contact;
        }else {
            return null;
        }
        generatedText = "Generating text for "+className;
        response.append("<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>"+className+"</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<p>Some static text</p>\n" +
            "<p>"+generatedText+"</p>\n" +
            "</body>\n" +
            "</html>");
        return response.toString();
    }
}
