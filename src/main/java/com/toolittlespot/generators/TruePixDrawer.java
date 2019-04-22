package com.toolittlespot.generators;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.toolittlespot.PrivateConstants.WINDOW_SIZE;

public class TruePixDrawer implements Callable<Boolean> {
    private BufferedImage image;
    private boolean[][] imgArr;
    private boolean[][] mask;
    private int shiftX;
    private int shiftY;
    private URL imageUrl;

    public TruePixDrawer(BufferedImage image, boolean[][] imgArr, boolean[][] mask, int shiftX, int shiftY, URL imageUrl) {
        this.image = image;
        this.imgArr = imgArr;
        this.mask = mask;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.imageUrl = imageUrl;
    }

    @Override
    public Boolean call() {
        try {
            getShiftedImage(image, imgArr, mask, shiftX, shiftY, imageUrl);
            return true;
        } catch (IOException e) {
           return false;
        }
    }

    private void getShiftedImage(BufferedImage image, boolean[][] imgArr, boolean[][] mask, int shiftX, int shiftY, URL imageUrl) throws IOException {
        BufferedImage maskedImg = ImageIO.read(imageUrl);

        for(int i = 0; i < WINDOW_SIZE; i++){
            for (int j = 0; j < WINDOW_SIZE; j++){
                if (!imgArr[i][j]){
                    if (i + shiftX >= 0 && i + shiftX < WINDOW_SIZE && j +shiftY >= 0 && j +shiftY < WINDOW_SIZE) {
                        if (mask[i + shiftX][j +shiftY]) {
                            imgArr[i][j] = true;
                            image.setRGB(i, j, maskedImg.getRGB(i+shiftX, j+shiftY));
                        }
                    }
                }
            }
        }
    }
}
