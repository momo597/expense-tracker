package com.momo.expense_tracker.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class SummaryResponse implements Serializable {

  private String label;

  private BigDecimal total;

  private Long count;

  public SummaryResponse() {}

  public SummaryResponse(String label, BigDecimal total, Long count) {
    this.label = label;
    this.total = total;
    this.count = count;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }
  ;
}
