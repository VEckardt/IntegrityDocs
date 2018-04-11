package com.ptc.services.utilities.docgen;

import java.util.StringTokenizer;

public class HyperLinkFactory {

    public static String convertHyperLinks(String text) {
        // Guard against null values...
        if (null == text || text.length() == 0) {
            return new String("");
        }

        String nl = IntegrityDocs.nl;
        StringBuffer formattedString = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer(text, nl);
        while (tokens.hasMoreTokens()) {
            String currentToken = tokens.nextToken();
            // Check to see if the line contains a http/https reference...
            if (currentToken.indexOf("http://") >= 0 || currentToken.indexOf("https://") >= 0) {
                while (currentToken.indexOf("http://") >= 0 || currentToken.indexOf("https://") >= 0) {
                    // Find the beginning and end of the URL reference
                    int indxStart = currentToken.indexOf("http://") >= 0 ? currentToken.indexOf("http://") : currentToken.indexOf("https://");
                    int indxEnd = currentToken.indexOf(' ', indxStart) >= 0 ? currentToken.indexOf(' ', indxStart) - 1 : currentToken.length() - 1;
                    // Save any text from the beginning of the line
                    formattedString.append(currentToken.substring(0, indxStart));
                    // Generate the hyper-link
                    formattedString.append("<a href='" + currentToken.substring(indxStart, indxEnd + 1) + "'>");
                    formattedString.append(currentToken.substring(indxStart, indxEnd + 1) + "</a>");
                    // Move the pointer forward
                    currentToken = currentToken.substring(indxEnd + 1);
                    // If there aren't any more hyper-links, then append the rest of the line
                    if (currentToken.indexOf("http://") < 0 && currentToken.indexOf("https://") < 0) {
                        formattedString.append(currentToken);
                    }
                }
            } else {
                // Just append the whole line
                formattedString.append(currentToken.substring(0, currentToken.length()));
            }
            // Insert an html line break, since we're splitting on regular line breaks
            formattedString.append("<br>");
            // Add a regular newline just so the output is readable
            formattedString.append(nl);
        }

        return formattedString.toString();
    }
}
