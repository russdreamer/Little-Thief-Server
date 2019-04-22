package com.toolittlespot.generators;

public class ShiftedImgRes {
    private boolean[][] maskedImg;
    private int shiftX;
    private int shiftY;
    private int falsePixels;

    public ShiftedImgRes(boolean[][] maskedImg, int shiftX, int shiftY, int falsePixels) {
        this.maskedImg = maskedImg;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.falsePixels = falsePixels;
    }

    public int getShiftX() {
        return shiftX;
    }

    public int getShiftY() {
        return shiftY;
    }

    public boolean[][] getMaskedImg() {
        return maskedImg;
    }

    public int getFalsePixels() {
        return falsePixels;
    }
}
