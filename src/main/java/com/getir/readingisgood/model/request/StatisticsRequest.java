package com.getir.readingisgood.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class StatisticsRequest {
    @NotNull
    @JsonFormat(pattern = "yyyy-MM")
    private Date startDate;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM")
    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
