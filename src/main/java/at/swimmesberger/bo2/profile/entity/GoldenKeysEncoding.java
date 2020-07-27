package at.swimmesberger.bo2.profile.entity;

import java.io.*;

public class GoldenKeysEncoding {
    public int decode(byte[] goldenKeyData) {
        // golden key data structure
        // 1 byte = index
        // 1 byte - 1 byte = value

        // 0 255 0
        // 0 255 0
        // 0 50 0
        // = 560
        // not sure why there are 2 bytes to represent the keys for each "entry"
        // maybe to record the already used keys? That would explain why we have to subtract the values
        int goldenKeys = 0;
        try (ByteArrayInputStream bIn = new ByteArrayInputStream(goldenKeyData); DataInputStream dIn = new DataInputStream(bIn)) {
            int index;
            while ((index = dIn.read()) != -1) {
                int subKeyVal1 = dIn.readUnsignedByte();
                int subKeyVal2 = dIn.readUnsignedByte();
                int subKeyVal = subKeyVal1 - subKeyVal2;
                goldenKeys += subKeyVal;
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return goldenKeys;
    }

    public byte[] encode(int goldenKeys) {
        int writeCount = 1;
        if (goldenKeys > 255) {
            writeCount = (int) (goldenKeys / 255d);
            int multVal = writeCount * 255;
            if (multVal < goldenKeys) {
                writeCount += 1;
            }
        }
        byte[] goldenKeyData = new byte[writeCount * 3];
        for (int index = 0; index < writeCount; index++) {
            goldenKeyData[index * 3] = (byte) index;
            int writeIndex = writeCount-1;
            if (index != writeIndex) {
                goldenKeyData[index * 3 + 1] = (byte)255;
            } else {
                int byteVal = goldenKeys - (writeIndex * 255);
                goldenKeyData[index * 3 + 1] = (byte) byteVal;
            }
        }
        return goldenKeyData;
    }
}
