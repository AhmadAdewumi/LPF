package com.ahmad.ProductFinder.service.tagService;

import com.ahmad.ProductFinder.models.Tag;

import java.util.Collection;
import java.util.List;

public interface ITagService {
    List<Tag> findOrCreateTags(Collection<String> names);
    List<Tag> listAllTags();
}
