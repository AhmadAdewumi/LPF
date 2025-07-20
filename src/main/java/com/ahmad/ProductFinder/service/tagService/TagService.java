package com.ahmad.ProductFinder.service.tagService;

import com.ahmad.ProductFinder.models.Tag;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import com.ahmad.ProductFinder.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TagService implements ITagService{
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public List<Tag> findOrCreateTags(Collection<String> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptyList();
        }
        //normalize the tags
        Set<String> normalized = names.stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());


        //find existing tags
        List<Tag> existingTags = tagRepository.findAllByNameIn(normalized);

        //get their names
        Set<String> existingNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        //the new tags to create
        List<Tag> toCreate = normalized.stream()
                .filter(name -> !existingNames.contains(name))
                .map(Tag::new)
                .collect(Collectors.toList());

        //persist
        List<Tag> saveNewTags = tagRepository.saveAll(toCreate);
        List<Tag> combinedTagsList = new ArrayList<>(existingTags.size() + saveNewTags.size());
        combinedTagsList.addAll(existingTags);
        combinedTagsList.addAll(saveNewTags);

        return combinedTagsList;

//        tagRepository.bulkInsertIgnoreExistingTags(new ArrayList<>(normalized));
//        return tagRepository.findAllByNameIn(normalized);
    }

    @Override
    public List<Tag> listAllTags() {
        return tagRepository.findAll();
    }
}
