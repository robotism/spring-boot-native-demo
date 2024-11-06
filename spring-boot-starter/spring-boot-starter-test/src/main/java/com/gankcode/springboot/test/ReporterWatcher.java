package com.gankcode.springboot.test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.extern.slf4j.Slf4j;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiPrintStream;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.fusesource.jansi.Ansi.ansi;

@Slf4j
@SuppressWarnings("PMD")
final class ReporterWatcher implements BeforeEachCallback, AfterEachCallback, TestWatcher {

    private static final ReporterWatcher SINGLETON = new ReporterWatcher();

    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String TARGET_DEFAULT;

    private final ExtentReports mExtentReports = new ExtentReports();

    private final ReporterOutputProxy mOutput = new ReporterOutputProxy();

    private final Map<String, ExtentTest> mUnits = new LinkedHashMap<>();


    private File mHtml;

    static {
        File reports = null;
        try {
            final File res = new PathMatchingResourcePatternResolver().getResource("").getFile();
            reports = new File(res.getAbsolutePath().split("classes")[0], "/reports/tests/extent-report.html");
        } catch (IOException e) {
            log.error("", e);
        }
        TARGET_DEFAULT = reports != null ? reports.getAbsolutePath() : null;
        AnsiConsole.systemInstall();
    }

    private ReporterWatcher() {

    }


    public static ReporterWatcher getSingleton() {
        return SINGLETON;
    }

    public void init() throws Exception {
        init(new File("").getAbsoluteFile().getName());
    }

    public void init(final String applicationName) throws Exception {
        final String title = (!StringUtils.hasText(applicationName) ? "" : applicationName + " ") + "| TestReports";
        init(title, TARGET_DEFAULT);
    }

    /**
     * @param title  显示在浏览器标签栏和显示在页面大标题
     * @param target html 输出文件位置
     */
    public void init(final String title, final String target) throws Exception {
        init(title, title, target);
    }

    /**
     * @param documentTitle 显示在浏览器标签栏
     * @param reportName    显示在页面大标题
     * @param target        html 输出文件位置
     */
    public void init(final String documentTitle, final String reportName, final String target) throws Exception {

        final File html = new File(target);
        mHtml = html;

        if (!html.getParentFile().exists() && !html.getParentFile().mkdirs()) {
            throw new RuntimeException(new IOException(target));
        }
        if (html.exists() && !html.delete()) {
            throw new RuntimeException(new IOException(target));
        }

        final ExtentSparkReporter reporter = new ExtentSparkReporter(html);
        reporter.config().setEncoding(StandardCharsets.UTF_8.toString());
        reporter.config().setDocumentTitle(documentTitle);
        reporter.config().setReportName(reportName);
        reporter.config().setTheme(Theme.STANDARD);
        reporter.config().setTimeStampFormat(TIME_FORMAT);
        mExtentReports.attachReporter(reporter);
        mExtentReports.setReportUsesManualConfiguration(true);

        mUnits.clear();

        mOutput.start();
    }


    @Override
    public void testSuccessful(ExtensionContext context) {
        postUnitNode(context, node -> {
            final String details = mOutput.toString();
            node.pass(MarkupHelper.createCodeBlock(details));
        });
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        postUnitNode(context, node -> {
            final String details = mOutput.toString();
            node.skip(MarkupHelper.createCodeBlock(details));
        });
    }


    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        postUnitNode(context, node -> {
            final String details = mOutput.toString();
            if (StringUtils.hasLength(details)) {
                node.fail(MarkupHelper.createCodeBlock(details));
            }

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cause.printStackTrace(new PrintStream(baos));
            node.fail(MarkupHelper.createCodeBlock(baos.toString()));
        });
    }


    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        postUnitNode(context, node -> {
            final String details = mOutput.toString();
            if (StringUtils.hasLength(details)) {
                node.warning(MarkupHelper.createCodeBlock(details));
            }
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cause.printStackTrace(new PrintStream(baos));
            node.warning(MarkupHelper.createCodeBlock(baos.toString()));
        });
    }

    private void postUnitNode(ExtensionContext context, Consumer<ExtentTest> consumer) {
        final String className = context.getRequiredTestClass().getName();
        final String methodName = context.getRequiredTestMethod().getName();
        if (!mUnits.containsKey(className)) {
            final ExtentTest unit = mExtentReports.createTest(className);
            unit.getModel().setStartTime(new Date());
            mUnits.put(className, unit);
        }
        final ExtentTest unit = mUnits.get(className);
        final ExtentTest node = unit.createNode(methodName);
        node.getModel().setStartTime(new Date());
        consumer.accept(node);
        node.getModel().setEndTime(new Date());
        unit.getModel().setEndTime(new Date());
    }


    public void finish() throws Exception {

        mOutput.stop();

        mExtentReports.flush();


        final Ansi split = ansi().eraseLine().fg(Ansi.Color.YELLOW).a("##############################################");
        try (AnsiPrintStream out = AnsiConsole.out()) {
            out.println(split);
            out.println(ansi().eraseLine().fg(Ansi.Color.GREEN).a("# ExtentReport output: " + mHtml));
            out.println(split);
        }

    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        mOutput.reset();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
    }


}
