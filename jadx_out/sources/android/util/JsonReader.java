package android.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import libcore.internal.StringPool;

/* JADX INFO: loaded from: classes2.dex */
public final class JsonReader implements Closeable {
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private final Reader in;
    private String name;
    private boolean skipping;
    private JsonToken token;
    private String value;
    private int valueLength;
    private int valuePos;
    private final StringPool stringPool = new StringPool();
    private boolean lenient = false;
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;
    private int bufferStartLine = 1;
    private int bufferStartColumn = 1;
    private final List<JsonScope> stack = new ArrayList();

    public JsonReader(Reader reader) {
        push(JsonScope.EMPTY_DOCUMENT);
        this.skipping = false;
        if (reader == null) {
            throw new NullPointerException("in == null");
        }
        this.in = reader;
    }

    private JsonToken advance() throws IOException {
        peek();
        JsonToken jsonToken = this.token;
        this.token = null;
        this.value = null;
        this.name = null;
        return jsonToken;
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private JsonToken decodeLiteral() throws IOException {
        if (this.valuePos == -1) {
            return JsonToken.STRING;
        }
        if (this.valueLength == 4 && (('n' == this.buffer[this.valuePos] || 'N' == this.buffer[this.valuePos]) && (('u' == this.buffer[this.valuePos + 1] || 'U' == this.buffer[this.valuePos + 1]) && (('l' == this.buffer[this.valuePos + 2] || 'L' == this.buffer[this.valuePos + 2]) && ('l' == this.buffer[this.valuePos + 3] || 'L' == this.buffer[this.valuePos + 3]))))) {
            this.value = "null";
            return JsonToken.NULL;
        }
        if (this.valueLength == 4 && (('t' == this.buffer[this.valuePos] || 'T' == this.buffer[this.valuePos]) && (('r' == this.buffer[this.valuePos + 1] || 'R' == this.buffer[this.valuePos + 1]) && (('u' == this.buffer[this.valuePos + 2] || 'U' == this.buffer[this.valuePos + 2]) && ('e' == this.buffer[this.valuePos + 3] || 'E' == this.buffer[this.valuePos + 3]))))) {
            this.value = TRUE;
            return JsonToken.BOOLEAN;
        }
        if (this.valueLength == 5 && (('f' == this.buffer[this.valuePos] || 'F' == this.buffer[this.valuePos]) && (('a' == this.buffer[this.valuePos + 1] || 'A' == this.buffer[this.valuePos + 1]) && (('l' == this.buffer[this.valuePos + 2] || 'L' == this.buffer[this.valuePos + 2]) && (('s' == this.buffer[this.valuePos + 3] || 'S' == this.buffer[this.valuePos + 3]) && ('e' == this.buffer[this.valuePos + 4] || 'E' == this.buffer[this.valuePos + 4])))))) {
            this.value = FALSE;
            return JsonToken.BOOLEAN;
        }
        this.value = this.stringPool.get(this.buffer, this.valuePos, this.valueLength);
        return decodeNumber(this.buffer, this.valuePos, this.valueLength);
    }

    private JsonToken decodeNumber(char[] cArr, int i, int i2) {
        int i3;
        int i4;
        char c;
        char c2 = cArr[i];
        if (c2 == '-') {
            int i5 = i + 1;
            i3 = i5;
            c2 = cArr[i5];
        } else {
            i3 = i;
        }
        if (c2 == '0') {
            i4 = i3 + 1;
            c = cArr[i4];
        } else {
            if (c2 < '1' || c2 > '9') {
                return JsonToken.STRING;
            }
            i4 = i3 + 1;
            c = cArr[i4];
            while (c >= '0' && c <= '9') {
                i4++;
                c = cArr[i4];
            }
        }
        if (c == '.') {
            i4++;
            c = cArr[i4];
            while (c >= '0' && c <= '9') {
                i4++;
                c = cArr[i4];
            }
        }
        if (c == 'e' || c == 'E') {
            int i6 = i4 + 1;
            char c3 = cArr[i6];
            if (c3 == '+' || c3 == '-') {
                i6++;
                c3 = cArr[i6];
            }
            if (c3 < '0' || c3 > '9') {
                return JsonToken.STRING;
            }
            i4 = i6 + 1;
            char c4 = cArr[i4];
            while (c4 >= '0' && c4 <= '9') {
                i4++;
                c4 = cArr[i4];
            }
        }
        return i4 == i + i2 ? JsonToken.NUMBER : JsonToken.STRING;
    }

    private void expect(JsonToken jsonToken) throws IOException {
        peek();
        if (this.token == jsonToken) {
            advance();
            return;
        }
        throw new IllegalStateException("Expected " + jsonToken + " but was " + peek());
    }

    private boolean fillBuffer(int i) throws IOException {
        for (int i2 = 0; i2 < this.pos; i2++) {
            if (this.buffer[i2] == '\n') {
                this.bufferStartLine++;
                this.bufferStartColumn = 1;
            } else {
                this.bufferStartColumn++;
            }
        }
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.limit);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        do {
            int i3 = this.in.read(this.buffer, this.limit, this.buffer.length - this.limit);
            if (i3 == -1) {
                return false;
            }
            this.limit += i3;
            if (this.bufferStartLine == 1 && this.bufferStartColumn == 1 && this.limit > 0 && this.buffer[0] == 65279) {
                this.pos++;
                this.bufferStartColumn--;
            }
        } while (this.limit < i);
        return true;
    }

    private int getColumnNumber() {
        int i = this.bufferStartColumn;
        for (int i2 = 0; i2 < this.pos; i2++) {
            i = this.buffer[i2] == '\n' ? 1 : i + 1;
        }
        return i;
    }

    private int getLineNumber() {
        int i = this.bufferStartLine;
        for (int i2 = 0; i2 < this.pos; i2++) {
            if (this.buffer[i2] == '\n') {
                i++;
            }
        }
        return i;
    }

    private CharSequence getSnippet() {
        StringBuilder sb = new StringBuilder();
        int iMin = Math.min(this.pos, 20);
        sb.append(this.buffer, this.pos - iMin, iMin);
        sb.append(this.buffer, this.pos, Math.min(this.limit - this.pos, 20));
        return sb;
    }

    private JsonToken nextInArray(boolean z) throws IOException {
        if (z) {
            replaceTop(JsonScope.NONEMPTY_ARRAY);
        } else {
            int iNextNonWhitespace = nextNonWhitespace();
            if (iNextNonWhitespace != 44) {
                if (iNextNonWhitespace != 59) {
                    if (iNextNonWhitespace != 93) {
                        throw syntaxError("Unterminated array");
                    }
                    pop();
                    JsonToken jsonToken = JsonToken.END_ARRAY;
                    this.token = jsonToken;
                    return jsonToken;
                }
                checkLenient();
            }
        }
        int iNextNonWhitespace2 = nextNonWhitespace();
        if (iNextNonWhitespace2 != 44 && iNextNonWhitespace2 != 59) {
            if (iNextNonWhitespace2 != 93) {
                this.pos--;
                return nextValue();
            }
            if (z) {
                pop();
                JsonToken jsonToken2 = JsonToken.END_ARRAY;
                this.token = jsonToken2;
                return jsonToken2;
            }
        }
        checkLenient();
        this.pos--;
        this.value = "null";
        JsonToken jsonToken3 = JsonToken.NULL;
        this.token = jsonToken3;
        return jsonToken3;
    }

    private JsonToken nextInObject(boolean z) throws IOException {
        if (!z) {
            int iNextNonWhitespace = nextNonWhitespace();
            if (iNextNonWhitespace != 44 && iNextNonWhitespace != 59) {
                if (iNextNonWhitespace != 125) {
                    throw syntaxError("Unterminated object");
                }
                pop();
                JsonToken jsonToken = JsonToken.END_OBJECT;
                this.token = jsonToken;
                return jsonToken;
            }
        } else {
            if (nextNonWhitespace() == 125) {
                pop();
                JsonToken jsonToken2 = JsonToken.END_OBJECT;
                this.token = jsonToken2;
                return jsonToken2;
            }
            this.pos--;
        }
        int iNextNonWhitespace2 = nextNonWhitespace();
        if (iNextNonWhitespace2 == 34) {
            this.name = nextString((char) iNextNonWhitespace2);
        } else if (iNextNonWhitespace2 != 39) {
            checkLenient();
            this.pos--;
            this.name = nextLiteral(false);
            if (this.name.isEmpty()) {
                throw syntaxError("Expected name");
            }
        } else {
            checkLenient();
            this.name = nextString((char) iNextNonWhitespace2);
        }
        replaceTop(JsonScope.DANGLING_NAME);
        JsonToken jsonToken3 = JsonToken.NAME;
        this.token = jsonToken3;
        return jsonToken3;
    }

    private String nextLiteral(boolean z) throws IOException {
        this.valuePos = -1;
        int i = 0;
        this.valueLength = 0;
        String string = null;
        int i2 = 0;
        StringBuilder sb = null;
        while (true) {
            if (this.pos + i2 < this.limit) {
                switch (this.buffer[this.pos + i2]) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i2++;
                        continue;
                }
                i = i2;
            } else if (i2 >= this.buffer.length) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                sb.append(this.buffer, this.pos, i2);
                this.valueLength += i2;
                this.pos += i2;
                if (fillBuffer(1)) {
                    i2 = 0;
                }
            } else if (!fillBuffer(i2 + 1)) {
                this.buffer[this.limit] = 0;
                i = i2;
            }
        }
        if (z && sb == null) {
            this.valuePos = this.pos;
        } else if (this.skipping) {
            string = "skipped!";
        } else if (sb == null) {
            string = this.stringPool.get(this.buffer, this.pos, i);
        } else {
            sb.append(this.buffer, this.pos, i);
            string = sb.toString();
        }
        this.valueLength += i;
        this.pos += i;
        return string;
    }

    private int nextNonWhitespace() throws IOException {
        while (true) {
            if (this.pos >= this.limit && !fillBuffer(1)) {
                throw new EOFException("End of input");
            }
            char[] cArr = this.buffer;
            int i = this.pos;
            this.pos = i + 1;
            char c = cArr[i];
            if (c != '\r' && c != ' ') {
                if (c == '#') {
                    checkLenient();
                    skipToEndOfLine();
                } else if (c != '/') {
                    switch (c) {
                        case '\t':
                        case '\n':
                            break;
                        default:
                            return c;
                    }
                } else {
                    if (this.pos == this.limit && !fillBuffer(1)) {
                        return c;
                    }
                    checkLenient();
                    char c2 = this.buffer[this.pos];
                    if (c2 == '*') {
                        this.pos++;
                        if (!skipTo("*/")) {
                            throw syntaxError("Unterminated comment");
                        }
                        this.pos += 2;
                    } else {
                        if (c2 != '/') {
                            return c;
                        }
                        this.pos++;
                        skipToEndOfLine();
                    }
                }
            }
        }
    }

    private String nextString(char c) throws IOException {
        StringBuilder sb = null;
        do {
            int i = this.pos;
            while (this.pos < this.limit) {
                char[] cArr = this.buffer;
                int i2 = this.pos;
                this.pos = i2 + 1;
                char c2 = cArr[i2];
                if (c2 == c) {
                    if (this.skipping) {
                        return "skipped!";
                    }
                    if (sb == null) {
                        return this.stringPool.get(this.buffer, i, (this.pos - i) - 1);
                    }
                    sb.append(this.buffer, i, (this.pos - i) - 1);
                    return sb.toString();
                }
                if (c2 == '\\') {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append(this.buffer, i, (this.pos - i) - 1);
                    sb.append(readEscapeCharacter());
                    i = this.pos;
                }
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append(this.buffer, i, this.pos - i);
        } while (fillBuffer(1));
        throw syntaxError("Unterminated string");
    }

    private JsonToken nextValue() throws IOException {
        int iNextNonWhitespace = nextNonWhitespace();
        if (iNextNonWhitespace != 34) {
            if (iNextNonWhitespace != 39) {
                if (iNextNonWhitespace == 91) {
                    push(JsonScope.EMPTY_ARRAY);
                    JsonToken jsonToken = JsonToken.BEGIN_ARRAY;
                    this.token = jsonToken;
                    return jsonToken;
                }
                if (iNextNonWhitespace != 123) {
                    this.pos--;
                    return readLiteral();
                }
                push(JsonScope.EMPTY_OBJECT);
                JsonToken jsonToken2 = JsonToken.BEGIN_OBJECT;
                this.token = jsonToken2;
                return jsonToken2;
            }
            checkLenient();
        }
        this.value = nextString((char) iNextNonWhitespace);
        JsonToken jsonToken3 = JsonToken.STRING;
        this.token = jsonToken3;
        return jsonToken3;
    }

    private JsonToken objectValue() throws IOException {
        int iNextNonWhitespace = nextNonWhitespace();
        if (iNextNonWhitespace != 58) {
            if (iNextNonWhitespace != 61) {
                throw syntaxError("Expected ':'");
            }
            checkLenient();
            if ((this.pos < this.limit || fillBuffer(1)) && this.buffer[this.pos] == '>') {
                this.pos++;
            }
        }
        replaceTop(JsonScope.NONEMPTY_OBJECT);
        return nextValue();
    }

    private JsonScope peekStack() {
        return this.stack.get(this.stack.size() - 1);
    }

    private JsonScope pop() {
        return this.stack.remove(this.stack.size() - 1);
    }

    private void push(JsonScope jsonScope) {
        this.stack.add(jsonScope);
    }

    private char readEscapeCharacter() throws IOException {
        if (this.pos == this.limit && !fillBuffer(1)) {
            throw syntaxError("Unterminated escape sequence");
        }
        char[] cArr = this.buffer;
        int i = this.pos;
        this.pos = i + 1;
        char c = cArr[i];
        if (c == 'b') {
            return '\b';
        }
        if (c == 'f') {
            return '\f';
        }
        if (c == 'n') {
            return '\n';
        }
        if (c == 'r') {
            return '\r';
        }
        switch (c) {
            case 't':
                return '\t';
            case 'u':
                if (this.pos + 4 > this.limit && !fillBuffer(4)) {
                    throw syntaxError("Unterminated escape sequence");
                }
                String str = this.stringPool.get(this.buffer, this.pos, 4);
                this.pos += 4;
                return (char) Integer.parseInt(str, 16);
            default:
                return c;
        }
    }

    private JsonToken readLiteral() throws IOException {
        this.value = nextLiteral(true);
        if (this.valueLength == 0) {
            throw syntaxError("Expected literal value");
        }
        this.token = decodeLiteral();
        if (this.token == JsonToken.STRING) {
            checkLenient();
        }
        return this.token;
    }

    private void replaceTop(JsonScope jsonScope) {
        this.stack.set(this.stack.size() - 1, jsonScope);
    }

    private boolean skipTo(String str) throws IOException {
        while (true) {
            if (this.pos + str.length() > this.limit && !fillBuffer(str.length())) {
                return false;
            }
            for (int i = 0; i < str.length(); i++) {
                if (this.buffer[this.pos + i] != str.charAt(i)) {
                    this.pos++;
                }
            }
            return true;
        }
    }

    private void skipToEndOfLine() throws IOException {
        char c;
        do {
            if (this.pos >= this.limit && !fillBuffer(1)) {
                return;
            }
            char[] cArr = this.buffer;
            int i = this.pos;
            this.pos = i + 1;
            c = cArr[i];
            if (c == '\r') {
                return;
            }
        } while (c != '\n');
    }

    private IOException syntaxError(String str) throws IOException {
        throw new MalformedJsonException(str + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    public void beginArray() throws IOException {
        expect(JsonToken.BEGIN_ARRAY);
    }

    public void beginObject() throws IOException {
        expect(JsonToken.BEGIN_OBJECT);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.value = null;
        this.token = null;
        this.stack.clear();
        this.stack.add(JsonScope.CLOSED);
        this.in.close();
    }

    public void endArray() throws IOException {
        expect(JsonToken.END_ARRAY);
    }

    public void endObject() throws IOException {
        expect(JsonToken.END_OBJECT);
    }

    public boolean hasNext() throws IOException {
        peek();
        return (this.token == JsonToken.END_OBJECT || this.token == JsonToken.END_ARRAY) ? false : true;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public boolean nextBoolean() throws IOException {
        peek();
        if (this.token == JsonToken.BOOLEAN) {
            boolean z = this.value == TRUE;
            advance();
            return z;
        }
        throw new IllegalStateException("Expected a boolean but was " + this.token);
    }

    public double nextDouble() throws IOException {
        peek();
        if (this.token == JsonToken.STRING || this.token == JsonToken.NUMBER) {
            double d = Double.parseDouble(this.value);
            advance();
            return d;
        }
        throw new IllegalStateException("Expected a double but was " + this.token);
    }

    public int nextInt() throws IOException {
        int i;
        peek();
        if (this.token != JsonToken.STRING && this.token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected an int but was " + this.token);
        }
        try {
            i = Integer.parseInt(this.value);
        } catch (NumberFormatException unused) {
            double d = Double.parseDouble(this.value);
            int i2 = (int) d;
            if (i2 != d) {
                throw new NumberFormatException(this.value);
            }
            i = i2;
        }
        advance();
        return i;
    }

    public long nextLong() throws IOException {
        long j;
        peek();
        if (this.token != JsonToken.STRING && this.token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected a long but was " + this.token);
        }
        try {
            j = Long.parseLong(this.value);
        } catch (NumberFormatException unused) {
            double d = Double.parseDouble(this.value);
            long j2 = (long) d;
            if (j2 != d) {
                throw new NumberFormatException(this.value);
            }
            j = j2;
        }
        advance();
        return j;
    }

    public String nextName() throws IOException {
        peek();
        if (this.token == JsonToken.NAME) {
            String str = this.name;
            advance();
            return str;
        }
        throw new IllegalStateException("Expected a name but was " + peek());
    }

    public void nextNull() throws IOException {
        peek();
        if (this.token == JsonToken.NULL) {
            advance();
            return;
        }
        throw new IllegalStateException("Expected null but was " + this.token);
    }

    public String nextString() throws IOException {
        peek();
        if (this.token == JsonToken.STRING || this.token == JsonToken.NUMBER) {
            String str = this.value;
            advance();
            return str;
        }
        throw new IllegalStateException("Expected a string but was " + peek());
    }

    public JsonToken peek() throws IOException {
        if (this.token != null) {
            return this.token;
        }
        switch (peekStack()) {
            case EMPTY_DOCUMENT:
                replaceTop(JsonScope.NONEMPTY_DOCUMENT);
                JsonToken jsonTokenNextValue = nextValue();
                if (this.lenient || this.token == JsonToken.BEGIN_ARRAY || this.token == JsonToken.BEGIN_OBJECT) {
                    return jsonTokenNextValue;
                }
                throw new IOException("Expected JSON document to start with '[' or '{' but was " + this.token);
            case EMPTY_ARRAY:
                return nextInArray(true);
            case NONEMPTY_ARRAY:
                return nextInArray(false);
            case EMPTY_OBJECT:
                return nextInObject(true);
            case DANGLING_NAME:
                return objectValue();
            case NONEMPTY_OBJECT:
                return nextInObject(false);
            case NONEMPTY_DOCUMENT:
                try {
                    JsonToken jsonTokenNextValue2 = nextValue();
                    if (this.lenient) {
                        return jsonTokenNextValue2;
                    }
                    throw syntaxError("Expected EOF");
                } catch (EOFException unused) {
                    JsonToken jsonToken = JsonToken.END_DOCUMENT;
                    this.token = jsonToken;
                    return jsonToken;
                }
            case CLOSED:
                throw new IllegalStateException("JsonReader is closed");
            default:
                throw new AssertionError();
        }
    }

    public void setLenient(boolean z) {
        this.lenient = z;
    }

    public void skipValue() throws IOException {
        this.skipping = true;
        try {
            if (!hasNext() || peek() == JsonToken.END_DOCUMENT) {
                throw new IllegalStateException("No element left to skip");
            }
            int i = 0;
            do {
                JsonToken jsonTokenAdvance = advance();
                if (jsonTokenAdvance == JsonToken.BEGIN_ARRAY || jsonTokenAdvance == JsonToken.BEGIN_OBJECT) {
                    i++;
                } else if (jsonTokenAdvance == JsonToken.END_ARRAY || jsonTokenAdvance == JsonToken.END_OBJECT) {
                    i--;
                }
            } while (i != 0);
            this.skipping = false;
        } catch (Throwable th) {
            this.skipping = false;
            throw th;
        }
    }

    public String toString() {
        return getClass().getSimpleName() + " near " + ((Object) getSnippet());
    }
}
