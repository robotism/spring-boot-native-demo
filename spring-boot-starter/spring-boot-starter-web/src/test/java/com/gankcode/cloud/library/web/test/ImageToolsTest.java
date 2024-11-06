package com.gankcode.cloud.library.web.test;


import com.gankcode.springboot.test.BaseJunitTest;
import com.gankcode.springboot.utils.FileManager;
import com.gankcode.springboot.web.tools.image.MosaicAvatar;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageToolsTest extends BaseJunitTest {


    @Test
    @DisplayName("随机头像")
    public void loadImage() throws Exception {

        final BufferedImage bi = MosaicAvatar.builder().build().generate();

        assert bi != null;

        final File output = new File(FileManager.DIR_TMP, "mosaic.avatar.png");

        ImageIO.write(bi, "png", output);

        assert output.exists();
    }

}
