package com.jnotifier.payload.response;

import java.util.List;
import org.springframework.data.domain.Page;

public class PaginatedResponse<T> {
  private List<T> content;
  private int pageNo;
  private int pageSize;
  private long totalElements;
  private int totalPages;
  private boolean last;

  public PaginatedResponse() {
  }

  public PaginatedResponse(Page<T> page) {
    this.content = page.getContent();
    this.pageNo = page.getNumber();
    this.pageSize = page.getSize();
    this.totalElements = page.getTotalElements();
    this.totalPages = page.getTotalPages();
    this.last = page.isLast();
  }

  public PaginatedResponse(List<T> content, int pageNo, int pageSize, long totalElements, int totalPages, boolean last) {
    this.content = content;
    this.pageNo = pageNo;
    this.pageSize = pageSize;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.last = last;
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
  }

  public int getPageNo() {
    return pageNo;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public boolean isLast() {
    return last;
  }

  public void setLast(boolean last) {
    this.last = last;
  }
}
