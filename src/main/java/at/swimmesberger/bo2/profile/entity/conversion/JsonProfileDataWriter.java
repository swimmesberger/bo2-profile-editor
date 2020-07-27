package at.swimmesberger.bo2.profile.entity.conversion;

import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.OutputStream;

public class JsonProfileDataWriter implements ProfileDataWriter {
    private final JsonGenerator jsonGenerator;

    public JsonProfileDataWriter(OutputStream outputStream) throws IOException {
        this.jsonGenerator = new JsonFactory().createGenerator(outputStream, JsonEncoding.UTF8);
        this.jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
    }

    @Override
    public void writeData(ProfileData profileData) throws IOException {
        this.jsonGenerator.writeStartObject();
        for (ProfileDataValueType type : ProfileDataValueType.values()) {
            this.jsonGenerator.writeFieldName(type.name());
            Object value = profileData.getValue(type);
            if (value instanceof Double || value instanceof Float) {
                Number num = (Number) value;
                this.jsonGenerator.writeNumber(num.doubleValue());
            } else if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
                Number num = (Number) value;
                this.jsonGenerator.writeNumber(num.longValue());
            } else if (value instanceof Boolean) {
                this.jsonGenerator.writeBoolean((Boolean) value);
            } else {
                this.jsonGenerator.writeString(String.valueOf(value));
            }
        }
        this.jsonGenerator.writeEndObject();
    }

    @Override
    public void close() throws IOException {
        this.jsonGenerator.close();
    }
}
