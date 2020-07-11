package at.swimmesberger.bo2.profile;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

// jackson streaming API based implementation for reading json profile data
public class JsonProfileEntryReader extends AbstractProfileEntryReader {
    private final JsonParser jsonParser;

    public JsonProfileEntryReader(InputStream in) throws IOException {
        this.jsonParser = new JsonFactory().createParser(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    @Override
    public ProfileEntryIterator iterateEntries() throws IOException {
        JsonToken token = this.jsonParser.nextToken();
        if (token != JsonToken.START_OBJECT) {
            throw new IOException("Invalid json profile entries format!");
        }
        String fieldName = this.jsonParser.nextFieldName();
        if (!fieldName.equals("entry_count")) {
            throw new IOException("Invalid json profile entries format!");
        }
        token = this.jsonParser.nextToken();
        if (token != JsonToken.VALUE_NUMBER_INT) {
            throw new IOException("Invalid json profile entries format!");
        }
        long entryCount = this.jsonParser.getNumberValue().longValue();
        fieldName = this.jsonParser.nextFieldName();
        if (!fieldName.equals("entries")) {
            throw new IOException("Invalid json profile entries format!");
        }
        return new JsonProfileEntryIterator(this.jsonParser, entryCount);
    }

    @Override
    public void close() throws IOException {

    }

    private static class JsonProfileEntryIterator implements ProfileEntryIterator {
        private final JsonParser jsonParser;
        private final long entryCount;

        // helper to anticipate EOF
        private JsonToken peekToken;
        private boolean first;
        private boolean eof;


        public JsonProfileEntryIterator(JsonParser jsonParser, long entryCount) {
            this.jsonParser = Objects.requireNonNull(jsonParser);
            this.entryCount = entryCount;
            this.peekToken = null;
            this.first = true;
            this.eof = false;
        }

        @Override
        public boolean hasNext() {
            return this.jsonParser.currentToken() != null && !this.eof;
        }

        @Override
        public ProfileEntry<?> next() {
            try {
                if (this.first) {
                    JsonToken token = this.jsonParser.nextToken();
                    if (token != JsonToken.START_ARRAY) {
                        throw new IOException("Invalid json profile entries format!");
                    }
                    this.first = false;
                }
                if (eof) {
                    throw new NoSuchElementException();
                }

                Map<String, Object> objectMap = this.parseNextObject();
                int id = ((Long) objectMap.get("id")).intValue();
                long offset = (Long) objectMap.get("offset");
                long length = (Long) objectMap.get("length");
                ProfileEntryDataType dataType = ProfileEntryDataType.valueOf((String) objectMap.get("data_type"));
                Object value = this.convertValue(objectMap.get("value"), dataType);
                ProfileEntry<?> profileEntry = new ProfileEntry<>(id, offset, length, dataType, value);
                this.peekToken = this.jsonParser.nextToken();
                if (this.peekToken == JsonToken.END_ARRAY) {
                    this.eof = true;
                }
                return profileEntry;
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }

        @Override
        public long getEntryCount() {
            return this.entryCount;
        }

        private Object convertValue(Object value, ProfileEntryDataType type) throws IOException {
            switch (type) {
                case Int32:
                    return (Long) value;
                case String:
                    return (String) value;
                case Float:
                    return ((Double) value).floatValue();
                case Binary:
                    String sValue = (String) value;
                    return Base64.getDecoder().decode(sValue);
                case Int8:
                    return ((Long) value).intValue();
                default:
                    throw new UnsupportedOperationException("Unsupported data type " + type);
            }
        }

        private Map<String, Object> parseNextObject() throws IOException {
            Map<String, Object> objectMap = new LinkedHashMap<>();
            JsonToken token;
            if (this.peekToken != null) {
                token = this.peekToken;
                this.peekToken = null;
            } else {
                token = this.jsonParser.nextToken();
            }
            if (token != JsonToken.START_OBJECT) {
                throw new IOException("Invalid json profile entries format!");
            }
            while ((token = this.jsonParser.nextValue()) != JsonToken.END_OBJECT) {
                String fieldName = this.jsonParser.getCurrentName();
                switch (token) {
                    case VALUE_NUMBER_INT:
                        objectMap.put(fieldName, this.jsonParser.getLongValue());
                        break;
                    case VALUE_NUMBER_FLOAT:
                        objectMap.put(fieldName, this.jsonParser.getDoubleValue());
                        break;
                    case VALUE_STRING:
                        objectMap.put(fieldName, this.jsonParser.getValueAsString());
                        break;
                    default:
                        throw new IOException("Unexpected json token " + token);
                }
            }
            return objectMap;
        }
    }
}
