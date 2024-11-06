package com.gankcode.cloud.library.web.test;


import com.gankcode.springboot.test.BaseJunitTest;
import com.gankcode.springboot.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ToolsTest extends BaseJunitTest {


    @Test
    @DisplayName("检查是否运行测试")
    public void checkTest() throws Exception {
        assert Utils.isTest();
    }


}
