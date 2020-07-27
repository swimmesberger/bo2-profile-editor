package at.swimmesberger.bo2.profile.util;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamSupplier extends IOSupplier<OutputStream> {
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
