package at.swimmesberger.bo2.profile.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public interface OutputStreamSupplier extends IOSupplier<OutputStream> {
    public static CloseableOutputStreamSupplier newFileOutputStream(Path file) throws IOException {
        return CloseableOutputStreamSupplier.create(() -> new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)));
    }

    public static interface CloseableOutputStreamSupplier extends OutputStreamSupplier, CloseableIOSupplier<OutputStream> {
        public static CloseableOutputStreamSupplier create(OutputStreamSupplier supplier) {
            CloseableIOSupplier<OutputStream> closeableIOSupplier = CloseableIOSupplier.create(supplier);
            return new CloseableOutputStreamSupplier() {
                @Override
                public void close() throws IOException {
                    closeableIOSupplier.close();
                }

                @Override
                public OutputStream get() throws IOException {
                    return closeableIOSupplier.get();
                }
            };
        }
    }
}
