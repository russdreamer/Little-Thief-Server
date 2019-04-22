package com.toolittlespot.pojo;

public class ImgPartProps {
    private long photoId;
    private int xPos;
    private int yPos;
    private float mockPixelWidth;
    private float mockPixelHeight;

    public ImgPartProps(long photoId, int xPos, int yPos, float mockPixelWidth, float mockPixelHeight) {
        this.photoId = photoId;
        this.xPos = xPos;
        this.yPos = yPos;
        this.mockPixelWidth = mockPixelWidth;
        this.mockPixelHeight = mockPixelHeight;
    }

    public long getPhotoId() {
        return photoId;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public float getMockPixelWidth() {
        return mockPixelWidth;
    }

    public float getMockPixelHeight() {
        return mockPixelHeight;
    }
}
