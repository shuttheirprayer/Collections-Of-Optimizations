package com.misanthropy.collections_of_optimizations.core;

public interface CataclysmCapHolder {

    Object COO_ABSENT = new Object();

    int COO_CAP_COUNT = 5;

    Object coo$getCataclysmCap(int index);

    void coo$setCataclysmCap(int index, Object value);
}
