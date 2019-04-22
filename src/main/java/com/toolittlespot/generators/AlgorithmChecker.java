package com.toolittlespot.generators;

import com.toolittlespot.getters.Coordinates;

import java.io.*;
import java.util.ArrayList;

import static com.toolittlespot.Contants.COORDS_PATH;
import static com.toolittlespot.Contants.MASK_PATH;
import static com.toolittlespot.generators.GeneratorUtil.deepCloneMask;
import static com.toolittlespot.generators.GeneratorUtil.deserializeObj;

public class AlgorithmChecker {
    public static void main(String[] args) {
        new AlgorithmChecker().check();
    }

    private boolean check() {
        boolean isDone = false;

        boolean[][] mask = (boolean[][]) deserializeObj(MASK_PATH);
        ArrayList<Coordinates> shifts = (ArrayList<Coordinates>) deserializeObj(COORDS_PATH);

        if (mask == null || shifts == null)
            return false;

        boolean[][] image = deepCloneMask(mask);

        for (Coordinates shift : shifts) {
            isDone = true;

            for (int i = 0; i < 700; i++) {
                for (int j = 0; j < 700; j++) {
                    if (!image[i][j]) {
                        int shiftX = shift.getX();
                        int shiftY = shift.getY();
                        if (i + shiftX >= 0 && i + shiftX < 700 && j + shiftY >= 0 && j + shiftY < 700) {
                            if (mask[i + shiftX][j + shiftY]) {
                                image[i][j] = true;
                            } else isDone = false;
                        }
                    }
                }
            }
        }
        System.out.println("Done is = " + isDone);
        System.out.println("successful!");
        return true;
    }
}
