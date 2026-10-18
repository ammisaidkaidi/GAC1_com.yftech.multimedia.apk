package android.content;

import android.net.Uri;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: classes.dex */
public class DefaultDataHandler implements ContentInsertHandler {
    private static final String ARG = "arg";
    private static final String COL = "col";
    private static final String DEL = "del";
    private static final String POSTFIX = "postfix";
    private static final String ROW = "row";
    private static final String SELECT = "select";
    private static final String URI_STR = "uri";
    private ContentResolver mContentResolver;
    private Stack<Uri> mUris = new Stack<>();
    private ContentValues mValues;

    private Uri insertRow() {
        Uri uriInsert = this.mContentResolver.insert(this.mUris.lastElement(), this.mValues);
        this.mValues = null;
        return uriInsert;
    }

    private void parseRow(Attributes attributes) throws SAXException {
        Uri uriWithAppendedPath;
        String value = attributes.getValue("uri");
        if (value != null) {
            uriWithAppendedPath = Uri.parse(value);
            if (uriWithAppendedPath == null) {
                throw new SAXException("attribute " + attributes.getValue("uri") + " parsing failure");
            }
        } else {
            if (this.mUris.size() <= 0) {
                throw new SAXException("attribute parsing failure");
            }
            String value2 = attributes.getValue(POSTFIX);
            uriWithAppendedPath = value2 != null ? Uri.withAppendedPath(this.mUris.lastElement(), value2) : this.mUris.lastElement();
        }
        this.mUris.push(uriWithAppendedPath);
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] cArr, int i, int i2) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) throws SAXException {
        if (ROW.equals(str2)) {
            if (this.mUris.empty()) {
                throw new SAXException("uri mismatch");
            }
            if (this.mValues != null) {
                insertRow();
            }
            this.mUris.pop();
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String str) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] cArr, int i, int i2) throws SAXException {
    }

    @Override // android.content.ContentInsertHandler
    public void insert(ContentResolver contentResolver, InputStream inputStream) throws SAXException, IOException {
        this.mContentResolver = contentResolver;
        Xml.parse(inputStream, Xml.Encoding.UTF_8, this);
    }

    @Override // android.content.ContentInsertHandler
    public void insert(ContentResolver contentResolver, String str) throws SAXException {
        this.mContentResolver = contentResolver;
        Xml.parse(str, this);
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String str, String str2) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String str) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
        if (ROW.equals(str2)) {
            if (this.mValues == null) {
                if (attributes.getLength() == 0) {
                    this.mUris.push(this.mUris.lastElement());
                    return;
                } else {
                    parseRow(attributes);
                    return;
                }
            }
            if (this.mUris.empty()) {
                throw new SAXException("uri is empty");
            }
            Uri uriInsertRow = insertRow();
            if (uriInsertRow == null) {
                throw new SAXException("insert to uri " + this.mUris.lastElement().toString() + " failure");
            }
            this.mUris.pop();
            this.mUris.push(uriInsertRow);
            parseRow(attributes);
            return;
        }
        if (COL.equals(str2)) {
            int length = attributes.getLength();
            if (length != 2) {
                throw new SAXException("illegal attributes number " + length);
            }
            String value = attributes.getValue(0);
            String value2 = attributes.getValue(1);
            if (value == null || value.length() <= 0 || value2 == null || value2.length() <= 0) {
                throw new SAXException("illegal attributes value");
            }
            if (this.mValues == null) {
                this.mValues = new ContentValues();
            }
            this.mValues.put(value, value2);
            return;
        }
        if (!DEL.equals(str2)) {
            throw new SAXException("unknown element: " + str2);
        }
        Uri uri = Uri.parse(attributes.getValue("uri"));
        if (uri == null) {
            throw new SAXException("attribute " + attributes.getValue("uri") + " parsing failure");
        }
        int length2 = attributes.getLength() - 2;
        if (length2 <= 0) {
            if (length2 == 0) {
                this.mContentResolver.delete(uri, attributes.getValue(1), null);
                return;
            } else {
                this.mContentResolver.delete(uri, null, null);
                return;
            }
        }
        String[] strArr = new String[length2];
        for (int i = 0; i < length2; i++) {
            strArr[i] = attributes.getValue(i + 2);
        }
        this.mContentResolver.delete(uri, attributes.getValue(1), strArr);
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String str, String str2) throws SAXException {
    }
}
