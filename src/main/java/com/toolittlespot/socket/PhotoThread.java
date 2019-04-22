package com.toolittlespot.socket;

import com.toolittlespot.getters.PhotoGetter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PhotoThread extends Thread {
    volatile boolean isConnected = true;
    private long id;
    private DataOutputStream dos;

    public PhotoThread(long id, DataOutputStream dos) {
        this.id = id;
        this.dos = dos;
    }

    @Override
    public void run() {
        try {
            System.out.println("RECEIVED ID = " + id);
            BufferedImage img = new PhotoGetter(dos).getImage(id);

            if (img != null){
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){

                    ImageIO.write(img, "jpg", baos);
                    baos.flush();
                    byte[] imageInByte = baos.toByteArray();

                    dos.writeInt(imageInByte.length);
                    dos.write(imageInByte);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                dos.flush();
                dos.close();
                isConnected = false;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
