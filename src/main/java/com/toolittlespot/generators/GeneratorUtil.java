package com.toolittlespot.generators;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneratorUtil {
    public static ExecutorService executor = Executors.newCachedThreadPool();

    public static boolean[][] getMaskedImage(boolean[][] image, boolean[][] mask, int shiftX, int shiftY) {
        boolean[][] newImage = deepCloneMask(image);
        int imgSize = newImage.length;

        for(int i = 0; i < imgSize; i++){
            for (int j = 0; j < imgSize; j++){
                if (!newImage[i][j]){
                    if (i + shiftX >= 0 && i + shiftX < imgSize && j + shiftY >= 0 && j +shiftY < imgSize){
                        if (mask[i + shiftX][j + shiftY]){
                            newImage[i][j] = true;
                        }
                    }
                }
            }
        }
        return newImage;
    }

    public static int countFalsePixels(boolean[][] img) {
        int sum = 0;
        for(int i = 0; i < 700; i++) {
            for (int j = 0; j < 700; j++) {
                if (!img[i][j])
                    sum++;
            }
        }
        return sum;
    }

    public static boolean[][] deepCloneMask(boolean[][] mask) {
        boolean[][] clonedMask = new boolean[mask.length][mask.length];

        for(int i = 0; i < clonedMask.length; i++) {
            System.arraycopy(mask[i], 0, clonedMask[i], 0, clonedMask.length);
        }
        return clonedMask;
    }

    public static void serializeObj(Object object, String path) throws IOException {
        try(
                FileOutputStream fos = new FileOutputStream(path);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ){
            oos.writeObject(object);
        }
    }

    public static Object deserializeObj(String path) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path)) ){
            return ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
