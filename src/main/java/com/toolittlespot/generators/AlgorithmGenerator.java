package com.toolittlespot.generators;

import com.toolittlespot.getters.Coordinates;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.toolittlespot.Contants.MASK_PATH;

public class AlgorithmGenerator {
    private int shiftY = 0;
    private int shiftX = 0;
    private int bestShiftX = 0;
    private int bestShiftY = 0;
    private boolean reverse = false;
    private boolean xTurn = true;
    private int curMaxShift = 1;
    private int shiftLimit = 50;
    private boolean isDone = false;
    private int curMinFalsePixels = Integer.MAX_VALUE;
    private ArrayList<Coordinates> resultCoordArr;

    public static void main(String[] args) {
        new AlgorithmGenerator().generate();
    }

    private List<Coordinates> generate() {
        resultCoordArr = new ArrayList<>();
        File file = new File(MASK_PATH);
        boolean[][] mask;

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            mask = (boolean[][]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        boolean[][] image = deepCloneMask(mask);
        boolean[][] bestTempImage;

        while (!isDone) {
            bestTempImage = getAlgorithm(image, mask, shiftLimit);

            if (bestTempImage != null){
                resultCoordArr.add(new Coordinates(bestShiftX, bestShiftY));
                image = deepCloneMask(bestTempImage);
                logProgress();
                resetShiftValues();
            }
            else {
                if (shiftLimit < mask.length / 2){
                    increaseShiftValues();
                }
                else return null;
            }
        }
        return resultCoordArr;
    }

    private void increaseShiftValues() {
        curMaxShift = shiftLimit + 1;
        shiftX = shiftLimit;
        shiftY = shiftLimit;
        shiftLimit += shiftLimit;
    }

    private void resetShiftValues() {
        curMaxShift = 1;
        shiftX = 0;
        shiftY = 0;
    }

    private void logProgress() {
        System.out.println("Total Depth = " + resultCoordArr.size());
        System.out.println("Coordinates: " + resultCoordArr.toString());
    }


    private boolean[][] getAlgorithm(boolean[][] image, boolean[][] mask , int shiftLimit) {
        boolean[][] tempImg = null;

        while (curMaxShift < shiftLimit && !isDone) {
            isDone = true;
            calcCoordinates();

            boolean[][] maskedImg = getMaskedImage(image, mask);
            int falsePixels = countFalsePixels(maskedImg);

            if (falsePixels > 0)
                isDone = false;

            if (falsePixels < curMinFalsePixels) {
                tempImg = maskedImg;
                curMinFalsePixels = falsePixels;
                bestShiftX = shiftX;
                bestShiftY = shiftY;
            }
        }

        return tempImg;
    }

    private boolean[][] deepCloneMask(boolean[][] mask) {
        boolean[][] clonedMask = mask.clone();
        for(int i = 0; i < clonedMask.length; i++) {
            clonedMask[i] = clonedMask[i].clone();
        }
        return clonedMask;
    }

    private void calcCoordinates(){
        if (xTurn){
            if (curMaxShift == Math.abs(shiftX)){
                xTurn = false;
                shiftY = reverse? --shiftY: ++shiftY;
            }
            else {
                shiftX = reverse? --shiftX: ++shiftX;
            }
        }
        else {
            if (curMaxShift == Math.abs(shiftY)){
                xTurn = true;
                if (reverse){
                    curMaxShift++;
                }
                reverse = !reverse;
                shiftX = reverse? --shiftX: ++shiftX;
            }
            else {
                shiftY = reverse? --shiftY: ++shiftY;
            }
        }
    }

    private boolean[][] getMaskedImage(boolean[][] image, boolean[][] mask) {
        boolean[][] newImage = deepCloneMask(image);
        int imgSize = newImage.length;

        for(int i = 0; i < imgSize; i++){
            for (int j = 0; j < imgSize; j++){
                if (!newImage[i][j]){
                    if (i + shiftX >= 0 && i + shiftX < imgSize && j +shiftY >= 0 && j +shiftY < imgSize){
                        if (mask[i + shiftX][j + shiftY]){
                            newImage[i][j] = true;
                        }
                    }
                }
            }
        }
        return newImage;
    }

    private static int countFalsePixels(boolean[][] img) {
        int sum = 0;
        for(int i = 0; i < 700; i++) {
            for (int j = 0; j < 700; j++) {
                if (!img[i][j])
                    sum++;
            }
        }
        return sum;
    }
}
