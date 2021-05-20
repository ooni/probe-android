package org.openobservatory.ooniprobe.domain.models;

import org.openobservatory.ooniprobe.model.database.Result;

import java.util.Date;
import java.util.List;

public class DatedResults {

    private final Date groupedDate;
    private final List<Result> resultsList;

    public DatedResults(Date groupedDate, List<Result> resultsList) {
        this.groupedDate = groupedDate;
        this.resultsList = resultsList;
    }

    public Date getGroupedDate() {
        return groupedDate;
    }

    public List<Result> getResultsList() {
        return resultsList;
    }
}
