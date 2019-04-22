package com.toolittlespot.getters;

import com.toolittlespot.pojo.ImageSize;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SizeGetter {

    public static ImageSize getImageSize(URL url) throws IOException {
        Pattern sizeRegex = Pattern.compile("([0-9]+x[0-9]+)");

        String content;
        URLConnection connection;

        connection =  url.openConnection();
        Scanner scanner = new Scanner(connection.getInputStream());
        scanner.useDelimiter("\\Z");
        content = scanner.next();
        scanner.close();

        String[] sizes = getElementByRegex(content, sizeRegex).split("x");
        return new ImageSize(Integer.valueOf(sizes[0]), Integer.valueOf(sizes[1]));
    }

    private static String getElementByRegex(String text, Pattern regex) {
        Matcher matcher = regex.matcher(text);

        while (matcher.find()){
            if (! matcher.group(1).trim().isEmpty())
                return matcher.group(1);
        }
        return null;
    }
}
