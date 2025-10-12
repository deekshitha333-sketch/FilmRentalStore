package com.example.filmRental.Dto;

import java.util.List;

/**
 * Lombok-free PagedResponse with a minimal inner Builder that uses setXxx(...) methods.
 * Paste this class exactly as-is.
 */
public class PagedResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public PagedResponse() {
    }

    public PagedResponse(List<T> content, long totalElements, int totalPages, int pageNumber,
                         int pageSize, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    // Getters / Setters
    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }

    // ---- Builder API (with setXxx methods) ----
    public static <T> PagedResponseBuilder<T> builder() {
        return new PagedResponseBuilder<>();
    }

    public static final class PagedResponseBuilder<T> {
        private final PagedResponse<T> target = new PagedResponse<>();

        public PagedResponseBuilder<T> setContent(List<T> value) {
            target.setContent(value);
            return this;
        }

        public PagedResponseBuilder<T> setTotalElements(long value) {
            target.setTotalElements(value);
            return this;
        }

        public PagedResponseBuilder<T> setTotalPages(int value) {
            target.setTotalPages(value);
            return this;
        }

        public PagedResponseBuilder<T> setPageNumber(int value) {
            target.setPageNumber(value);
            return this;
        }

        public PagedResponseBuilder<T> setPageSize(int value) {
            target.setPageSize(value);
            return this;
        }

        public PagedResponseBuilder<T> setHasNext(boolean value) {
            target.setHasNext(value);
            return this;
        }

        public PagedResponseBuilder<T> setHasPrevious(boolean value) {
            target.setHasPrevious(value);
            return this;
        }

        public PagedResponse<T> build() {
            return target;
        }
    }
}
