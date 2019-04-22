package com.toolittlespot.generators;

import java.util.concurrent.Callable;

import static com.toolittlespot.generators.GeneratorUtil.countFalsePixels;
import static com.toolittlespot.generators.GeneratorUtil.getMaskedImage;

public class ProcShiftedImg implements Callable<ShiftedImgRes> {
    private boolean[][] image;
    private boolean[][] mask;
    private int shiftX;
    private int shiftY;

    public ProcShiftedImg(boolean[][] image, boolean[][] mask, int shiftX, int shiftY) {
        this.image = image;
        this.mask = mask;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    @Override
    public ShiftedImgRes call() {
        //System.out.println(name + "STARTED");
        boolean[][] maskedImg = getMaskedImage(image, mask, shiftX, shiftY);
        //System.out.println(name + "GOT MASKED IMG");
        int falsePixels = countFalsePixels(maskedImg);
        //System.out.println(name + "FALSE PIXELS = " + falsePixels);
        return new ShiftedImgRes(maskedImg, shiftX, shiftY, falsePixels);
    }
}
