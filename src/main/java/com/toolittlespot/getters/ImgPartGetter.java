package com.toolittlespot.getters;

import com.toolittlespot.generators.TruePixDrawer;
import com.toolittlespot.pojo.ImgPart;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.toolittlespot.PrivateConstants.*;
import static com.toolittlespot.generators.GeneratorUtil.deepCloneMask;
import static com.toolittlespot.generators.GeneratorUtil.executor;

public class ImgPartGetter implements Callable<ImgPart> {
    private long photoId;
    private int xPos;
    private int yPos;
    private float mockPixelWidth;
    private float mockPixelHeight;
    private boolean[][] mask;
    private ArrayList<Coordinates> shifts;

    public ImgPartGetter(long photoId, int xPos, int yPos, float mockPixelWidth, float mockPixelHeight, boolean[][] mask, ArrayList<Coordinates> shifts) {
        this.photoId = photoId;
        this.xPos = xPos;
        this.yPos = yPos;
        this.mockPixelWidth = mockPixelWidth;
        this.mockPixelHeight = mockPixelHeight;
        this.mask = mask;
        this.shifts = shifts;
    }

    @Override
    public ImgPart call() {
        try {
            return getImgPart();
        } catch (IOException | InterruptedException ignored) {
            return null;
        }
    }

    private ImgPart getImgPart() throws IOException, InterruptedException {
        float mockX = xPos * mockPixelWidth;
        float mockY = yPos * mockPixelHeight;

        BufferedImage image = getNotShiftedImg(mockX, mockY);
        boolean[][] imgArr = deepCloneMask(mask);

        List<Callable<Boolean>> callableList = new ArrayList<>();
        for (int shiftIndex = 0; shiftIndex < shifts.size(); shiftIndex++) {
            int shiftArrX = shifts.get(shiftIndex).getX();
            int shiftArrY = shifts.get(shiftIndex).getY();

            float mockShiftX = mockX - shiftArrX * mockPixelWidth;
            float mockShiftY = mockY - shiftArrY * mockPixelHeight;

            URL imageUrl = new URL(ROOT_SITE + PHOTO_ID + photoId + X_POS + mockShiftX + Y_POS + mockShiftY + IMAGE_SIZE_PARAMS);
            Callable callable = new TruePixDrawer(image, imgArr, mask, shiftArrX, shiftArrY, imageUrl);
            callableList.add(callable);
        }

        List<Future<Boolean>> futureList = executor.invokeAll(callableList);
        for (Future<Boolean> future : futureList) {
            try {
                if (!future.get()){
                    return null;
                }
            } catch (InterruptedException | ExecutionException ignored) {
                return null;
            }
        }

        return new ImgPart(image, xPos, yPos);
    }

    private BufferedImage getNotShiftedImg(float mockX, float mockY) throws IOException {
        URL imageUrl = new URL(ROOT_SITE + PHOTO_ID + X_POS + mockX + Y_POS + mockY + IMAGE_SIZE_PARAMS);
        return ImageIO.read(imageUrl);
    }
}
