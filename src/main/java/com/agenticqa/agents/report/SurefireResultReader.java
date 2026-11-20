package com.agenticqa.agents.report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads TestNG/Surefire {@code TEST-*.xml} reports for pass/fail rows.
 */
public final class SurefireResultReader {

    private SurefireResultReader() {
    }

    public static List<ExecutionTestResult> readDirectory(Path surefireDir) {
        if (surefireDir == null || !Files.isDirectory(surefireDir)) {
            return List.of();
        }
        List<ExecutionTestResult> results = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(surefireDir, "TEST-*.xml")) {
            for (Path xml : stream) {
                results.addAll(readFile(xml));
            }
        } catch (Exception ignored) {
            // best-effort
        }
        return results;
    }

    static List<ExecutionTestResult> readFile(Path xml) {
        List<ExecutionTestResult> results = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = factory.newDocumentBuilder().parse(xml.toFile());
            NodeList cases = doc.getElementsByTagName("testcase");
            for (int i = 0; i < cases.getLength(); i++) {
                Element testCase = (Element) cases.item(i);
                String className = simpleName(testCase.getAttribute("classname"));
                String methodName = testCase.getAttribute("name");
                double seconds = 0d;
                try {
                    seconds = Double.parseDouble(testCase.getAttribute("time"));
                } catch (Exception ignored) {
                    // keep 0
                }
                long ms = Math.round(seconds * 1000d);
                ExecutionTestResult.Status status = ExecutionTestResult.Status.PASS;
                String classification = null;
                if (testCase.getElementsByTagName("failure").getLength() > 0
                        || testCase.getElementsByTagName("error").getLength() > 0) {
                    status = ExecutionTestResult.Status.FAIL;
                } else if (testCase.getElementsByTagName("skipped").getLength() > 0) {
                    status = ExecutionTestResult.Status.SKIP;
                }
                if (methodName == null || methodName.isBlank()) {
                    continue;
                }
                results.add(new ExecutionTestResult(className, methodName, status, ms, classification));
            }
        } catch (Exception ignored) {
            // best-effort
        }
        return results;
    }

    private static String simpleName(String classname) {
        if (classname == null || classname.isBlank()) {
            return "";
        }
        int dot = classname.lastIndexOf('.');
        return dot >= 0 ? classname.substring(dot + 1) : classname;
    }
}
