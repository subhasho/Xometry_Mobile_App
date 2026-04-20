package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Converts TestNG {@code testng-results.xml} (Surefire or IDE test-output) to a short PDF summary.
 * Run: {@code mvn -q exec:java -Dexec.mainClass=utils.TestNgResultsToPdf -Dexec.classpathScope=test}
 * Optional: {@code args[0]} = input XML, {@code args[1]} = output PDF.
 * Full suite totals and all methods appear in the PDF (e.g. 31 tests). Only {@code Tests.Tests} login
 * methods {@code testone}…{@code testsix} get PDF-only step descriptions (Steps 1–6); other rows keep TestNG names.
 */
public final class TestNgResultsToPdf {

    private static final List<String> LOGIN_METHOD_ORDER =
        List.of("testone", "testtwo", "testthree", "testFour", "testFive", "testsix");

    /** PDF-only labels aligned with {@code Tests} class login flow (priority 1–6). */
    private static final String[] LOGIN_STEP_PDF_LABELS = {
        "Step 1: Click Log In button",
        "Step 2: Enter username (email)",
        "Step 3: Click Continue",
        "Step 4: Enter password",
        "Step 5: Click Continue",
        "Step 6: Handle open-app / Open with popup"
    };

    private static final String LOGIN_CLASS = "Tests.Tests";

    private static final float MARGIN = 50;
    private static final float ROW = 13;
    private static final float FONT = 8;
    private static final float TITLE = 11;

    private TestNgResultsToPdf() {}

    public static void main(String[] args) throws Exception {
        Path xml = resolveXml(args);
        Path pdf = resolvePdf(args, xml);

        if (!Files.isRegularFile(xml)) {
            System.err.println("Input XML not found: " + xml.toAbsolutePath());
            System.exit(1);
        }
        Files.createDirectories(pdf.getParent());

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        Document doc = f.newDocumentBuilder().parse(xml.toFile());
        Element root = doc.getDocumentElement();

        String total = root.getAttribute("total");
        String passed = root.getAttribute("passed");
        String failed = root.getAttribute("failed");
        String skipped = root.getAttribute("skipped");
        if (skipped.isEmpty()) {
            skipped = root.getAttribute("ignored");
        }

        String suiteName = "";
        NodeList suites = doc.getElementsByTagName("suite");
        if (suites.getLength() > 0) {
            suiteName = ((Element) suites.item(0)).getAttribute("name");
        }

        List<Row> rows = new ArrayList<>();
        NodeList methods = doc.getElementsByTagName("test-method");
        for (int i = 0; i < methods.getLength(); i++) {
            Element tm = (Element) methods.item(i);
            if ("true".equalsIgnoreCase(tm.getAttribute("is-config"))) {
                continue;
            }
            String status = tm.getAttribute("status");
            if (status.isEmpty()) {
                continue;
            }
            rows.add(new Row(classNameFor(tm), tm.getAttribute("name"), status, tm.getAttribute("duration-ms")));
        }

        try (PDDocument out = new PDDocument()) {
            PDType1Font font = PDType1Font.HELVETICA;
            PDType1Font bold = PDType1Font.HELVETICA_BOLD;

            PDPage page = new PDPage(PDRectangle.A4);
            out.addPage(page);
            float y = PDRectangle.A4.getHeight() - MARGIN;
            PDPageContentStream cs = new PDPageContentStream(out, page);

            y = line(cs, bold, TITLE, MARGIN, y, "TestNG summary (from XML)");
            y -= 4;
            y = line(cs, font, FONT, MARGIN, y, "Suite: " + nz(suiteName));
            y = line(cs, font, FONT, MARGIN, y,
                "Totals: total=" + nz(total) + " passed=" + nz(passed) + " failed=" + nz(failed) + " skipped=" + nz(skipped));
            y = line(cs, font, FONT, MARGIN, y, "Source: " + xml.toAbsolutePath());
            y -= 10;

            float[] w = { 128, 248, 48, 52 };
            String[] hdr = { "Class", "Method / step", "Status", "ms" };
            y = header(cs, bold, MARGIN, y, w, hdr);

            for (Row r : rows) {
                if (y < MARGIN + ROW * 5) {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    out.addPage(page);
                    cs = new PDPageContentStream(out, page);
                    y = PDRectangle.A4.getHeight() - MARGIN;
                    y = header(cs, bold, MARGIN, y, w, hdr);
                }
                y = row(cs, font, MARGIN, y, w, r);
            }
            cs.close();
            out.save(pdf.toFile());
        }
        System.out.println("PDF written: " + pdf.toAbsolutePath());
    }

    private static Path resolveXml(String[] args) {
        if (args.length > 0 && !args[0].isBlank()) {
            return Path.of(args[0]);
        }
        String p = System.getProperty("testng.xml.path");
        if (p != null && !p.isBlank()) {
            return Path.of(p);
        }
        Path testOut = Path.of("test-output/testng-results.xml");
        if (Files.isRegularFile(testOut)) {
            return testOut;
        }
        return Path.of("target/surefire-reports/testng-results.xml");
    }

    private static Path resolvePdf(String[] args, Path xml) {
        if (args.length > 1 && !args[1].isBlank()) {
            return Path.of(args[1]);
        }
        String p = System.getProperty("testng.pdf.path");
        if (p != null && !p.isBlank()) {
            return Path.of(p);
        }
        if (xml.toString().replace('\\', '/').contains("test-output")) {
            return Path.of("test-output/TestNgSummary.pdf");
        }
        return Path.of("target/reports/TestNgSummary.pdf");
    }

    /** PDF-only: {@code Tests.Tests} login methods use {@code LOGIN_STEP_PDF_LABELS}; others unchanged. */
    private static String pdfMethodDisplayName(Row r) {
        if (r == null || !LOGIN_CLASS.equals(r.cls) || r.method == null) {
            return r != null ? r.method : "";
        }
        for (int i = 0; i < LOGIN_METHOD_ORDER.size(); i++) {
            if (LOGIN_METHOD_ORDER.get(i).equals(r.method)) {
                return LOGIN_STEP_PDF_LABELS[i];
            }
        }
        return r.method;
    }

    private static String nz(String s) {
        return s == null || s.isEmpty() ? "—" : s;
    }

    private static String classNameFor(Element testMethod) {
        org.w3c.dom.Node n = testMethod.getParentNode();
        while (n != null) {
            if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element el = (Element) n;
                if ("class".equals(el.getTagName())) {
                    return el.getAttribute("name");
                }
            }
            n = n.getParentNode();
        }
        return "";
    }

    private static float line(PDPageContentStream cs, PDType1Font ft, float size, float x, float y, String s)
            throws Exception {
        cs.beginText();
        cs.setFont(ft, size);
        cs.newLineAtOffset(x, y);
        cs.showText(safe(s, 400));
        cs.endText();
        return y - ROW;
    }

    private static float header(PDPageContentStream cs, PDType1Font bold, float x, float y, float[] w, String[] h)
            throws Exception {
        float cx = x;
        for (int i = 0; i < h.length; i++) {
            cs.beginText();
            cs.setFont(bold, FONT);
            cs.newLineAtOffset(cx, y);
            cs.showText(safe(h[i], 80));
            cs.endText();
            cx += w[i];
        }
        return y - ROW - 3;
    }

    private static float row(PDPageContentStream cs, PDType1Font font, float x, float y, float[] w, Row r)
            throws Exception {
        String[] c = { r.cls, pdfMethodDisplayName(r), r.status, r.ms };
        float cx = x;
        for (int i = 0; i < c.length; i++) {
            cs.beginText();
            cs.setFont(font, FONT);
            cs.newLineAtOffset(cx, y);
            cs.showText(safe(trunc(c[i], i == 1 ? 95 : (int) (w[i] / 3)), i == 1 ? 140 : 120));
            cs.endText();
            cx += w[i];
        }
        return y - ROW;
    }

    private static String trunc(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }

    private static String safe(String s, int max) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (char c : s.toCharArray()) {
            b.append(c >= 32 && c < 127 ? c : '?');
        }
        String t = b.toString();
        return t.length() > max ? t.substring(0, max - 3) + "..." : t;
    }

    private static final class Row {
        final String cls;
        final String method;
        final String status;
        final String ms;

        Row(String cls, String method, String status, String ms) {
            this.cls = cls;
            this.method = method;
            this.status = status;
            this.ms = ms;
        }
    }
}
