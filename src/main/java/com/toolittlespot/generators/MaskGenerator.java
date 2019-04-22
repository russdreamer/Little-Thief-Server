package com.toolittlespot.generators;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import static com.toolittlespot.Contants.BLACK_COLOR;
import static com.toolittlespot.Contants.MASK_PATH;
import static com.toolittlespot.PrivateConstants.*;

public class MaskGenerator {
    public static void main(String[] args) {
        new MaskGenerator().generate();
    }

    public boolean generate(){
        File file = new File(MASK_PATH);
        try(
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ){
            URL url = new URL(ROOT_SITE + PHOTO_ID + DEFAULT_ID + MASK_POSITION_PARAMS);
            BufferedImage image1 = ImageIO.read(url);

            boolean[][] mask = new boolean[700][700];

            for(int i = 0; i < 700; i++){
                for (int j = 0; j < 700; j++){
                    mask[i][j] = image1.getRGB(i, j) == BLACK_COLOR;
                }
            }

            oos.writeObject(mask);
            System.out.println("Mask is generated successfully!");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
