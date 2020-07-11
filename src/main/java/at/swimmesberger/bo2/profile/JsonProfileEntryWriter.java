package at.swimmesberger.bo2.profile;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.OutputStream;

public class JsonProfileEntryWriter implements ProfileEntryWriter {
    private final JsonGenerator jsonGenerator;

    public JsonProfileEntryWriter(OutputStream outputStream) throws IOException {
        this.jsonGenerator = new JsonFactory().createGenerator(outputStream, JsonEncoding.UTF8);
        this.jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
    }

    @Override
    public void begin(long entryCount) throws IOException {
        this.jsonGenerator.writeStartObject();
        this.jsonGenerator.writeFieldName("entry_count");
        this.jsonGenerator.writeNumber(entryCount);
        this.jsonGenerator.writeFieldName("entries");
        this.jsonGenerator.writeStartArray();
    }

    @Override
    public void write(ProfileEntry<?> entry) throws IOException {
        this.jsonGenerator.writeStartObject();
        this.jsonGenerator.writeFieldName("id");
        this.jsonGenerator.writeNumber(entry.getId());
        this.jsonGenerator.writeFieldName("offset");
        this.jsonGenerator.writeNumber(entry.getOffset());
        this.jsonGenerator.writeFieldName("length");
        this.jsonGenerator.writeNumber(entry.getLength());
        this.jsonGenerator.writeFieldName("data_type");
        this.jsonGenerator.writeString(entry.getDataType().name());
        this.jsonGenerator.writeFieldName("value");
        this.printValue(entry.getValue(), entry.getDataType());
        this.jsonGenerator.writeEndObject();
    }

    private void printValue(Object value, ProfileEntryDataType type) throws IOException {
        switch (type) {
            case Int32:
                this.jsonGenerator.writeNumber((Long) value);
                break;
            case String:
                this.jsonGenerator.writeString((String) value);
                break;
            case Float:
                this.jsonGenerator.writeNumber((Float) value);
                break;
            case Binary:
                this.jsonGenerator.writeBinary((byte[]) value);
                break;
            case Int8:
                this.jsonGenerator.writeNumber((Integer) value);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported data type " + type);
        }
    }

    @Override
    public void end() throws IOException {
        this.jsonGenerator.writeEndArray();
        this.jsonGenerator.writeEndObject();
    }

    @Override
    public void close() throws IOException {
        this.jsonGenerator.close();
    }
}
