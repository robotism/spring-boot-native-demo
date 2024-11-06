package com.gankcode.springboot.web.tools.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MosaicAvatar {

    @Builder.Default
    private Color background = Color.WHITE;

    @Builder.Default
    private int width = 360;
    @Builder.Default
    private int height = 360;

    @Builder.Default
    private int padding = 30;

    @Builder.Default
    private double radio = 0.45;

    @Builder.Default
    private int blockCount = 9;

    @Builder.Default
    private int colorEvaluation = 100;

    public BufferedImage generate() {
        final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setBackground(background);
        g2.fillRect(0, 0, width, height);


        final Color paintColor = randomColor();

        final Set<Point> list = randomPoints();

        final int rowBlockLength = (height - 2 * padding) / blockCount;
        final int colBlockLength = (width - 2 * padding) / blockCount;

        g2.setColor(paintColor);

        for (Point point : list) {
            g2.fillRect(padding + point.x * rowBlockLength,
                    padding + point.y * colBlockLength,
                    rowBlockLength,
                    colBlockLength);
        }
        return bi;
    }

    private Color randomColor() {
        final Random random = new Random();
        int r;
        int g;
        int b;
        int rg;
        int rb;
        int gb;
        do {
            r = random.nextInt(256);
            g = random.nextInt(256);
            b = random.nextInt(256);
            rg = Math.abs(r - g);
            rb = Math.abs(r - b);
            gb = Math.abs(g - b);
        } while (rg > colorEvaluation || rb > colorEvaluation || gb > colorEvaluation);
        return new Color(r, g, b);
    }

    private Set<Point> randomPoints() {
        final Set<Point> set = new HashSet<>();

        for (int i = 0; i < blockCount / 2; i++) {
            for (int j = 0; j < blockCount; j++) {
                if (Math.random() < radio) {
                    set.add(new Point(i, j));
                    set.add(new Point(blockCount - 1 - i, j));
                }
            }
        }
        if (blockCount % 2 == 1) {
            for (int j = 0; j < blockCount; j++) {
                if (Math.random() < radio) {
                    set.add(new Point(blockCount / 2, j));
                }
            }
        }
        return set;
    }
}
