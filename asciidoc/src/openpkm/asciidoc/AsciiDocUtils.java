/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.asciidoc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Rok Koren
 */
public class AsciiDocUtils 
{
    private static final List<String> blockExtensions = List.of("stem", "asciimath", "latexmath", "mathml", "math", "plantuml", "uml", "ditaa", "graphviz", "tree", "mermaid", "chart");

    private static final List<String> mathExtensions = List.of("stem", "asciimath", "latexmath", "mathml", "tex");

    public static String correctExtensionBlocks(String content) {

        if (Objects.isNull(content)) {
            return null;
        }

        String[] lines = content.split("\\R");
        return Arrays.stream(lines)
                .map(AsciiDocUtils::correctUmlBlocks)
                .map(AsciiDocUtils::correctTargetInBlocks)
                .map(AsciiDocUtils::correctInlineMathExtensions)
                .map(AsciiDocUtils::correctBlockMathExtensions)
                .collect(Collectors.joining("\n"));

    }

    private static String correctBlockMathExtensions(String line) {
        for (String mathExtension : mathExtensions) {
            if (line.startsWith("[" + mathExtension)) {
                line = line.replace("[" + mathExtension, "[" + mathExtension + "_");
            }
        }
        return line;
    }

    private static String correctInlineMathExtensions(String line) {
        for (String mathExtension : mathExtensions) {
            String textStart = mathExtension + ":";
            String textInMiddle = " " + mathExtension + ":";
            if (line.startsWith(textStart) || line.contains(textInMiddle)) {
                line = line.replace(textStart, mathExtension + "_:");
            }
        }
        return line;
    }

    private static String correctTargetInBlocks(String line) {
        for (String extension : blockExtensions) {
            if (line.startsWith("[" + extension + ",") && line.contains("file=\"")) {
                line = line.replace("file=\"", "target=\"");
                break;
            }
        }
        return line;
    }

    private static String correctUmlBlocks(String line) {
        line = line.replaceAll("\\[uml,", "[plantuml,");
        line = line.replaceAll("\\[uml]", "[plantuml]");
        return line;
    }    
}
