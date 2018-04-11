package com.ptc.services.utilities.docgen;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLErrorHandler implements ErrorHandler {

    private StringBuilder message;

    public XMLErrorHandler() {
        message = new StringBuilder();
    }

    public void error(SAXParseException se) throws SAXException {
        message.append("ERROR: " + IntegrityDocs.nl);
        printError(se);
    }

    public void fatalError(SAXParseException se) throws SAXException {
        message.append("FATAL: " + IntegrityDocs.nl);
        printError(se);
    }

    public void warning(SAXParseException se) throws SAXException {
        message.append("WARNING: " + IntegrityDocs.nl);
        printError(se);
    }

    private void printError(SAXParseException e) {
        // Construct the message summary
        if (e.getSystemId().length() > 6) {
            // Assumes that system id starts with file://
            message.append(e.getSystemId().substring(6) + IntegrityDocs.nl);
        } else {
            message.append(e.getSystemId() + IntegrityDocs.nl);
        }
        message.append("Line number: " + e.getLineNumber() + IntegrityDocs.nl);
        message.append("Column number: " + e.getColumnNumber() + IntegrityDocs.nl);
        message.append("Message: " + e.getMessage() + IntegrityDocs.nl);

        // Log the error...
        System.out.println(message.toString());
    }
}
