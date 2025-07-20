package com.ahmad.ProductFinder.controller;

import com.ahmad.ProductFinder.controller.swaggerDocs.TagDocs;
import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import com.ahmad.ProductFinder.models.Tag;
import com.ahmad.ProductFinder.service.tagService.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
public class TagController implements TagDocs {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @PostMapping("/bulk")
    public ResponseEntity<ApiResponseBody> findOrCreateTags(@RequestBody Collection<String> names) {
        log.info("fetching or creating {} tag(s)", names == null ? 0 : names.size());
        List<Tag> result = tagService.findOrCreateTags(names);
        log.info("fetching or creation complete");
        return ResponseEntity.ok(new ApiResponseBody("Fetching or creation of missing tags done successfully", result));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponseBody> listAllTags() {
        List<Tag> result = tagService.listAllTags();

        log.info("Listing {} tag(s), results : {}", result.size(), result.stream().map(Tag::toString).collect(Collectors.joining(", ")));

        return ResponseEntity.ok(new ApiResponseBody("Fetched All Tags: ", result));
    }
}
