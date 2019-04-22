package com.toolittlespot.getters;

import com.toolittlespot.generators.TruePixDrawer;
import com.toolittlespot.pojo.ImgPart;
import com.toolittlespot.pojo.ImgPartProps;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.toolittlespot.PrivateConstants.*;
import static com.toolittlespot.generators.GeneratorUtil.deepCloneMask;
import static com.toolittlespot.generators.GeneratorUtil.executor;

public class ImgPartGetter implements Callable<ImgPart> {
    private ImgPartProps imgProps;
    private boolean[][] mask;
    private ArrayList<Coordinates> shifts;
    private DataOutputStream dos;

    public ImgPartGetter(ImgPartProps imgProps, boolean[][] mask, ArrayList<Coordinates> shifts, DataOutputStream dos) {
        this.imgProps = imgProps;
        this.mask = mask;
        this.shifts = shifts;
        this.dos = dos;
    }

    @Override
    public ImgPart call() {
        try {
            ImgPart imgPart = getImgPart();
            dos.writeBoolean(! Objects.isNull(imgPart));
            return imgPart;

        } catch (IOException | InterruptedException ignored) {
            return null;
        }
    }

    private ImgPart getImgPart() throws IOException, InterruptedException {
        float mockX = imgProps.getxPos() * imgProps.getMockPixelWidth();
        float mockY = imgProps.getyPos() * imgProps.getMockPixelHeight();

        BufferedImage image = getNotShiftedImg(mockX, mockY);
        boolean[][] imgArr = deepCloneMask(mask);

        List<Callable<Boolean>> callableList = new ArrayList<>();
        for (Coordinates shift : shifts) {
            int shiftArrX = shift.getX();
            int shiftArrY = shift.getY();

            float mockShiftX = mockX - shiftArrX * imgProps.getMockPixelWidth();
            float mockShiftY = mockY - shiftArrY * imgProps.getMockPixelHeight();

            URL imageUrl = new URL(ROOT_SITE + PHOTO_ID + imgProps.getPhotoId() + X_POS + mockShiftX + Y_POS + mockShiftY + IMAGE_SIZE_PARAMS);
            Callable callable = new TruePixDrawer(image, imgArr, mask, shiftArrX, shiftArrY, imageUrl);
            callableList.add(callable);
        }

        List<Future<Boolean>> futureList = executor.invokeAll(callableList);
        for (Future<Boolean> future : futureList) {
            try {
                if (!future.get())
                    return null;

            } catch (InterruptedException | ExecutionException ignored) {
                return null;
            }
        }

        return new ImgPart(image, imgProps.getxPos(), imgProps.getyPos());
    }

    private BufferedImage getNotShiftedImg(float mockX, float mockY) throws IOException {
        URL imageUrl = new URL(ROOT_SITE + PHOTO_ID + imgProps.getPhotoId()+ X_POS + mockX + Y_POS + mockY + IMAGE_SIZE_PARAMS);
        return ImageIO.read(imageUrl);
    }
}
