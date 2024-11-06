package com.gankcode.springboot.web.utils;

import com.gankcode.springboot.crypto.Crypto;
import com.gankcode.springboot.web.tools.image.MosaicAvatar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;
import java.util.function.Consumer;


@Slf4j
public class ImageUtils {

    public static BufferedImage base64ToImage(String base64) {
        try {
            final byte[] bytes = base64ToBytes(base64);
            if (bytes == null) {
                return null;
            }
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static byte[] base64ToBytes(String base64) {
        if (!StringUtils.hasText(base64)) {
            return null;
        }
        try {
            final String split = ";base64,";
            final String data = base64.contains(split) ? base64.split(split)[1] : base64;
            return Crypto.base64().decode(data);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static String imageToBase64(BufferedImage image) {
        if (image == null) {
            return null;
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            final byte[] bytes = baos.toByteArray();
            final String base64 = Crypto.base64().encodeToString(bytes);
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    public static String imageToBase64(InputStream is) {
        if (is == null) {
            return null;
        }
        try (is) {
            return imageToBase64(ImageIO.read(is));
        } catch (Exception ignored) {

        }
        return null;
    }

    public static String imageToBase64(File imageFile) {
        if (imageFile == null) {
            return null;
        }
        try {
            return imageToBase64(new FileInputStream(imageFile));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String imageToBase64(byte[] imageBytes) {
        if (imageBytes == null) {
            return null;
        }
        return imageToBase64(new ByteArrayInputStream(imageBytes));
    }

    public static BufferedImage scale(BufferedImage source, int w, int h) {
        if (source == null) {
            return null;
        }

        final int ow = source.getWidth();
        final int oh = source.getHeight();
        // 对截取的帧进行等比例缩放
        final int width;
        final int height;
        if (w > 0 && h > 0) {
            width = w;
            height = h;
        } else if (w > 0) {
            width = w;
            height = width * oh / ow;
        } else if (h > 0) {
            height = h;
            width = height * ow / oh;
        } else {
            width = ow;
            height = oh;
        }
        final BufferedImage image = newImage(width, height, g2 -> {
            g2.drawImage(source.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        });
        return Optional.ofNullable(image).orElse(source);
    }

    public static BufferedImage newImage(int width, int height, Consumer<Graphics2D> filter) {
        return newImage(width, height, BufferedImage.TYPE_INT_ARGB, filter);
    }

    public static BufferedImage newImage(int width, int height, int type, Consumer<Graphics2D> filter) {
        try {
            final BufferedImage bi = new BufferedImage(width, height, type);
            final Graphics2D g2 = (Graphics2D) bi.getGraphics();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
            g2.setBackground(new Color(0, 0, 0, 0));
            g2.clearRect(0, 0, width, height);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            filter.accept(g2);
            g2.dispose();
            return bi;
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    public static BufferedImage randomAvatar() {
        return MosaicAvatar.builder().build().generate();
    }
}
