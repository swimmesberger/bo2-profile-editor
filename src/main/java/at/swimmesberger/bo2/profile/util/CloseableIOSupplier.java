package at.swimmesberger.bo2.profile.util;

import java.io.IOException;

public interface CloseableIOSupplier<T> extends IOSupplier<T>, AutoCloseable {
    public static <T extends AutoCloseable> CloseableIOSupplier<T> create(IOSupplier<T> supplier) {
        return new CloseableIOSupplier<T>() {
            private volatile T value = null;

            @Override
            public void close() throws IOException {
                T v = this.value;
                if (v != null) {
                    try {
                        v.close();
                    } catch (IOException e) {
                        throw e;
                    } catch (Exception ex) {
                        if (ex instanceof RuntimeException) {
                            throw ((RuntimeException) ex);
                        }
                        throw new IllegalStateException(ex);
                    }
                }
            }

            @Override
            public T get() throws IOException {
                T v = supplier.get();
                this.value = v;
                return v;
            }
        };
    }

    @Override
    void close() throws IOException;

}
