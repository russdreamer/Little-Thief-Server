package com.toolittlespot.generators;

import com.toolittlespot.getters.Coordinates;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

import static com.toolittlespot.Contants.COORDS_PATH;
import static com.toolittlespot.Contants.MASK_PATH;
import static com.toolittlespot.Contants.MAX_THREADS_AMOUNT;
import static com.toolittlespot.generators.GeneratorUtil.deepCloneMask;
import static com.toolittlespot.generators.GeneratorUtil.serializeObj;

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
    private ExecutorService executor;

    public static void main(String[] args) {
        List<Coordinates> coordList = new AlgorithmGenerator().generate();

        try {
            serializeObj(coordList, COORDS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Coordinates> generate() {
        executor = Executors.newSingleThreadExecutor();
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
                if (shiftLimit < mask.length / 2)
                    increaseShiftValues();

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
        List<Callable<ShiftedImgRes>> callableList;
        List<ShiftedImgRes> shiftedImgList = new ArrayList<>();
        ShiftedImgRes bestRes = null;

        while (curMaxShift < shiftLimit) {
            callableList = createFutures(image, mask);
            bestRes = invokeAllAndGetBest(callableList, shiftedImgList);
        }

        if (bestRes != null) {
            System.out.println("FALSE PIXELS = " + bestRes.getFalsePixels());
            if (bestRes.getFalsePixels() < curMinFalsePixels) {
                if (bestRes.getFalsePixels() == 0) {
                    isDone = true;
                    executor.shutdownNow();
                }

                curMinFalsePixels = bestRes.getFalsePixels();
                bestShiftX = bestRes.getShiftX();
                bestShiftY = bestRes.getShiftY();

                return bestRes.getMaskedImg();
            }
        }

        return null;
    }

    private ShiftedImgRes invokeAllAndGetBest(List<Callable<ShiftedImgRes>> callableList, List<ShiftedImgRes> shiftedImgList) {
        try {
            List<Future<ShiftedImgRes>> futureList = executor.invokeAll(callableList);
            for (Future<ShiftedImgRes> fut : futureList) {
                ShiftedImgRes result = fut.get();
                shiftedImgList.add(result);
            }

            ShiftedImgRes bestRes = shiftedImgList
                    .parallelStream()
                    .min(Comparator.comparing(ShiftedImgRes::getFalsePixels))
                    .get();

            shiftedImgList.clear();
            callableList.clear();
            shiftedImgList.add(bestRes);

            return bestRes;

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Callable<ShiftedImgRes>> createFutures(boolean[][] image, boolean[][] mask) {
        List<Callable<ShiftedImgRes>> list = new ArrayList<>();
        for (int i = 0; i < MAX_THREADS_AMOUNT && curMaxShift < shiftLimit; i++) {
            calcCoordinates();
            Callable<ShiftedImgRes> callable = new ProcShiftedImg(image, mask, shiftX, shiftY);
            list.add(callable);
        }
        return list;
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


}
