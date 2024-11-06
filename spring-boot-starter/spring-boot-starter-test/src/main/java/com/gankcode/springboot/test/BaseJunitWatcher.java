package com.gankcode.springboot.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@Slf4j
abstract class BaseJunitWatcher extends AbstractJUnit4SpringContextTests {

    @RegisterExtension
    final ReporterWatcher reporter = ReporterWatcher.getSingleton();

    @BeforeAll
    public static void onBefore() throws Exception {
        ReporterWatcher.getSingleton().init();
    }

    @AfterAll
    public static void onAfter() throws Exception {
        ReporterWatcher.getSingleton().finish();
    }

}
