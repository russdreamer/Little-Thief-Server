package com.toolittlespot.getters;

import com.toolittlespot.pojo.ImageSize;
import com.toolittlespot.pojo.ImgPart;
import com.toolittlespot.pojo.ImgPartProps;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.toolittlespot.Contants.COORDS_PATH;
import static com.toolittlespot.Contants.MASK_PATH;
import static com.toolittlespot.PrivateConstants.PHOTO_IMG_LINK;
import static com.toolittlespot.PrivateConstants.WINDOW_SIZE;
import static com.toolittlespot.generators.GeneratorUtil.deserializeObj;
import static com.toolittlespot.generators.GeneratorUtil.executor;

public class PhotoGetter {
    private DataOutputStream outputStream;

    public PhotoGetter(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public BufferedImage getImage(Long photoId){
        try {
            return getPhoto(photoId);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage getPhoto(Long photoId) throws IOException, InterruptedException {

        URL sizeUrl = new URL(PHOTO_IMG_LINK + photoId + ".html");
        ImageSize sizes = SizeGetter.getImageSize(sizeUrl);

        int xSteps = sizes.getWidth() / WINDOW_SIZE;
        int ySteps = sizes.getHeight() / WINDOW_SIZE;

        float mockPixelWidth = (WINDOW_SIZE + 0.0F) / sizes.getWidth();
        float mockPixelHeight = (WINDOW_SIZE + 0.0F) / sizes.getHeight();

        int restShiftX = sizes.getWidth() - WINDOW_SIZE;
        int restShiftY = sizes.getHeight() - WINDOW_SIZE;

        ArrayList<Integer> shiftXList = new ArrayList<>();
        for (int i = 0; i < xSteps; i++){
            shiftXList.add(i * WINDOW_SIZE);
        }

        ArrayList<Integer> shiftYList = new ArrayList<>();
        for (int i = 0; i < ySteps; i++){
            shiftYList.add(i * WINDOW_SIZE);
        }

        if (xSteps * WINDOW_SIZE != sizes.getWidth())
            shiftXList.add(restShiftX);

        if (ySteps * WINDOW_SIZE != sizes.getHeight())
            shiftYList.add(restShiftY);

        outputStream.writeInt(shiftXList.size() * shiftYList.size() + 1);

        BufferedImage fullImage = new BufferedImage(sizes.getWidth(), sizes.getHeight(), BufferedImage.TYPE_INT_RGB);
        ArrayList<Coordinates> shifts = (ArrayList<Coordinates>) deserializeObj(COORDS_PATH);
        boolean[][] mask = (boolean[][]) deserializeObj(MASK_PATH);

        List<Callable<ImgPart>> callableList = new ArrayList<>();
        for (int shiftX: shiftXList) {
            for (int shiftY: shiftYList) {
                ImgPartProps imgPartProps = new ImgPartProps(photoId, shiftX, shiftY, mockPixelWidth, mockPixelHeight);
                Callable callable = new ImgPartGetter(imgPartProps, mask, shifts, outputStream);
                callableList.add(callable);
            }
        }

        outputStream.writeBoolean(true);
        List<Future<ImgPart>> futureList = executor.invokeAll(callableList);
        for (Future<ImgPart> future: futureList) {
            try {
                ImgPart imgPart = future.get();

                if (imgPart == null)
                    return null;

                fullImage.getGraphics().drawImage(imgPart.getImage(), imgPart.getxPos(), imgPart.getyPos(), null);

            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }

        return fullImage;
    }
}
