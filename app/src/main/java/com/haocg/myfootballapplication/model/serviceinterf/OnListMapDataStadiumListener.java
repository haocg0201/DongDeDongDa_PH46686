package com.haocg.myfootballapplication.model.serviceinterf;

import java.util.List;
import java.util.Map;

public interface OnListMapDataStadiumListener {
    void onListMapDataReceived(List<Map<String, Boolean>> lstTimeUserBook);
}
