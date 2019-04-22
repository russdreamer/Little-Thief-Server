package com.toolittlespot.pojo;

import java.awt.image.BufferedImage;

public class ImgPart {
    private BufferedImage image;
    private int xPos;
    private int yPos;

    public ImgPart(BufferedImage image, int xPos, int yPos) {
        this.image = image;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }
}
