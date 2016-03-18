import com.moodysalem.phantomjs.wrapper.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.AssertJUnit.assertTrue;

public class TestPhantomJS {
    @Test
    public void testRunScript() throws IOException, InterruptedException {
        assertTrue(PhantomJS.exec(TestPhantomJS.class.getResourceAsStream("test-script.js")) == 0);
    }

    @Test
    public void testOptions() throws IOException {
        PhantomJSOptions pjo = new PhantomJSOptions();
        pjo.setHelp(true);
        assertTrue(PhantomJS.exec(TestPhantomJS.class.getResourceAsStream("test-script.js"), pjo) == 0);
    }

    @Test
    public void testRender() throws IOException, RenderException {
        try (InputStream is = PhantomJS.render(TestPhantomJS.class.getResourceAsStream("test-js-waiting.html"),
            PaperSize.Letter, ViewportDimensions.VIEW_1280_1024, Margin.ZERO,
            BannerInfo.EMPTY, BannerInfo.EMPTY,
            RenderFormat.PDF, 1000L, 100L)) {
            PDDocument doc = PDDocument.load(is);
            assertTrue(doc.getNumberOfPages() == 1);
        }
    }

    @Test
    public void testWithExternalCss() throws IOException, RenderException {
        try (InputStream is = PhantomJS.render(TestPhantomJS.class.getResourceAsStream("test-with-resources.html"),
            PaperSize.Letter, ViewportDimensions.VIEW_1280_1024, Margin.ZERO,
            BannerInfo.EMPTY, BannerInfo.EMPTY,
            RenderFormat.PDF, 1000L, 100L)) {
            PDDocument doc = PDDocument.load(is);
            assertTrue(doc.getNumberOfPages() == 1);
        }
    }

    @Test
    public void testRenderJsWait() throws IOException, RenderException {
        try (InputStream is = PhantomJS.render(TestPhantomJS.class.getResourceAsStream("test-js-waiting.html"),
            PaperSize.Letter, ViewportDimensions.VIEW_1280_1024, Margin.ZERO,
            BannerInfo.EMPTY, BannerInfo.EMPTY,
            RenderFormat.PDF, 1000L, 100L)) {
            PDDocument doc = PDDocument.load(is);
            assertTrue(doc.getNumberOfPages() == 1);
        }
    }

    @Test(expectedExceptions = RenderException.class)
    public void testRenderJsWaitTimeout() throws IOException, RenderException {
        try (InputStream is = PhantomJS.render(TestPhantomJS.class.getResourceAsStream("test-js-waiting.html"),
            PaperSize.Letter, ViewportDimensions.VIEW_1280_1024, Margin.ZERO,
            BannerInfo.EMPTY, BannerInfo.EMPTY,
            RenderFormat.PDF, 100L, 100L)) {
            PDDocument doc = PDDocument.load(is);
            assertTrue(doc.getNumberOfPages() == 1);
        }
    }


    @Test
    public void testFooterAndHeader() throws IOException, RenderException {
        BannerInfo header = new BannerInfo(1, SizeUnit.in, "function (pageNum, numPages) { return \"<h5>Header: <span style='float:right'>\" + ['zero','one','two'][pageNum] + \" / \" + ['zero','one','two'][numPages] + \"</span></h5>\"; }");
        BannerInfo footer = new BannerInfo(1, SizeUnit.in, "function (pageNum, numPages) { return \"<h5>Footer: <span style='float:right'>\" + pageNum + \" / \" + numPages + \"</span></h5>\"; }");
        try (InputStream is = PhantomJS.render(TestPhantomJS.class.getResourceAsStream("test.html"),
            PaperSize.Letter, ViewportDimensions.VIEW_1280_1024, Margin.ZERO,
            header, footer,
            RenderFormat.PDF, 100L, 100L)) {
            PDDocument doc = PDDocument.load(is);
            assertTrue(doc.getNumberOfPages() == 2);
        }
    }
}
