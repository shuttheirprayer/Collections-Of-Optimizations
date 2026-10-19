package com.misanthropy.collections_of_optimizations.core;

public final class CoOStacklessNumberFormatException extends NumberFormatException {

    private static final long serialVersionUID = 1L;

    public CoOStacklessNumberFormatException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
