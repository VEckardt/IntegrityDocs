package com.ptc.services.utilities.docgen;

import static com.ptc.services.utilities.docgen.Constants.nl;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLErrorHandler implements ErrorHandler {

    private StringBuilder message;

    public XMLErrorHandler() {
        message = new StringBuilder();
    }

    public void error(SAXParseException se) throws SAXException {
        message.append("ERROR: " + nl);
        printError(se);
    }

    public void fatalError(SAXParseException se) throws SAXException {
        message.append("FATAL: " + nl);
        printError(se);
    }

    public void warning(SAXParseException se) throws SAXException {
        message.append("WARNING: " + nl);
        printError(se);
    }

    private void printError(SAXParseException e) {
        // Construct the message summary
        if (e.getSystemId().length() > 6) {
            // Assumes that system id starts with file://
            message.append(e.getSystemId().substring(6) + nl);
        } else {
            message.append(e.getSystemId() + nl);
        }
        message.append("Line number: " + e.getLineNumber() + nl);
        message.append("Column number: " + e.getColumnNumber() + nl);
        message.append("Message: " + e.getMessage() + nl);

        // Log the error...
        log(message.toString());
    }
}
