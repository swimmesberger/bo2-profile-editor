package at.swimmesberger.bo2.profile.entity.conversion;

import at.swimmesberger.bo2.profile.entity.ProfileData;
import at.swimmesberger.bo2.profile.entity.ProfileDataValueType;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonProfileDataReader implements ProfileDataReader {
    private final JsonParser jsonParser;

    public JsonProfileDataReader(InputStream in) throws IOException {
        this.jsonParser = new JsonFactory().createParser(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    @Override
    public ProfileData readData() throws IOException {
        JsonToken token = this.jsonParser.nextToken();
        if (token != JsonToken.START_OBJECT) {
            throw new IOException("Invalid json profile data format!");
        }
        ProfileData.ProfileDataBuilder builder = ProfileData.builder();
        while (true) {
            token = this.jsonParser.nextToken();
            if (token == JsonToken.END_OBJECT || token == null)
                break;
            if (token != JsonToken.FIELD_NAME) {
                throw new IOException("Invalid json profile data format!");
            }
            String fieldName = this.jsonParser.getCurrentName();
            ProfileDataValueType valueType = ProfileDataValueType.valueOf(fieldName);
            token = this.jsonParser.nextToken();
            Object value;
            if (token == JsonToken.VALUE_NUMBER_INT) {
                value = this.jsonParser.getLongValue();
            } else if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                value = this.jsonParser.getDoubleValue();
            } else if (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE) {
                value = this.jsonParser.getBooleanValue();
            } else if (token == JsonToken.VALUE_STRING) {
                value = this.jsonParser.getValueAsString();
            } else {
                throw new IOException("Invalid json profile data format!");
            }
            builder.withValue(valueType, value);
        }
        return builder.build();
    }

    @Override
    public void close() throws IOException {
        this.jsonParser.close();
    }
}
