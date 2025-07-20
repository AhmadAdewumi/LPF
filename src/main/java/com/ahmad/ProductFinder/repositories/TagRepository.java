package com.ahmad.ProductFinder.repositories;

import com.ahmad.ProductFinder.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag , Long> {
    Optional<Tag> findByName(String name);
    List<Tag> findAllByNameIn(Collection<String> names);

//    //I AM CASTING NAMES TO ARRAY COZ UNNEST EXPECT AN ARRAY BECAUSE HIBERNATE IS CONFUSED
//    @Modifying
//    @Query(value = """
//            INSERT INTO tag(name)
//            SELECT unnest(CAST(:names AS VARCHAR[]))
//            ON CONFLICT(name)
//            DO NOTHING
//            """, nativeQuery = true)
//    void bulkInsertIgnoreExistingTags(List<String> names);
}
