package com.yolifay.eventservice.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record BasePaging(
        Integer page,          // 1-based
        Integer perpage,       // size
        String sortField,      // e.g. "id"
        String sortDirection,  // "asc"|"desc"
        String q               // free text
) {
    public BasePaging {
        if (page == null || page < 1) page = 1;
        if (perpage == null || perpage < 1) perpage = 10;
        if (perpage > 200) perpage = 200;
        if (sortField == null || sortField.isBlank()) sortField = "id";
        if (!"desc".equalsIgnoreCase(sortDirection)) sortDirection = "asc";
    }

    @JsonIgnore
    public int pageIndex() { return page - 1; } // to Pageable (0-based)
}
