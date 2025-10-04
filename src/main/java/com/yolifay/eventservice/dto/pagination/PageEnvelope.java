package com.yolifay.eventservice.dto.pagination;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({ "content", "totalElements", "page", "size", "from", "to",
        "totalPages", "numberOfElements", "first", "last", "nextPage", "hasNext", "sortMeta" })
public record PageEnvelope<T>(
        List<T> content,
        long totalElements,
        int page,                 // nomor halaman saat ini (1-based)
        int size,                 // jumlah item per halaman
        long from,                // indeks urutan global item pertama pada halaman ini (1-based), 0 jika kosong
        long to,                  // indeks urutan global item terakhir pada halaman ini, 0 jika kosong
        int totalPages,           // total jumlah halaman
        int numberOfElements,     // jumlah item aktual pada halaman ini
        boolean first,            // true jika halaman saat ini adalah halaman pertama
        boolean last,             // true jika halaman saat ini adalah halaman terakhir
        Integer nextPage,         // nomor halaman berikutnya; null jika tidak ada
        boolean hasNext,           // true jika masih ada halaman setelah halaman saat ini
        SortMeta sortMeta
) {
    public static <T> PageEnvelope<T> of(BasePaging paging, long total, List<T> content, SortMeta sort) {
        int page = paging.page();
        int size = paging.perpage();

        int totalPages = (int) Math.ceil((double) total / (double) size);
        boolean first = page <= 1;
        boolean last = (totalPages == 0) || (page >= totalPages);

        long from = (total == 0) ? 0 : ((long) (page - 1) * size) + 1;
        long to = Math.min((long) page * size, total);
        if (from > to) { from = 0; to = 0; }

        boolean hasNext = !last && total > 0;
        Integer nextPage = hasNext ? page + 1 : null;

        return new PageEnvelope<>(
                content,
                total,
                page,
                size,
                from,
                to,
                totalPages,
                content != null ? content.size() : 0,
                first,
                last,
                nextPage,
                hasNext,
                sort
        );
    }
}
