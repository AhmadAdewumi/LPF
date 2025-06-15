package com.ahmad.ProductFinder.repositories;

import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.projection.StoreProjection;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface StoreRepository extends JpaRepository<Store, Long> {
    //    Long findStoreById(Long storeId);
    boolean existsById(Long storeId);

    Optional<Store> findByIdAndIsActiveTrue(Long storeId);

    List<Store> findByIsActiveTrue();


    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM store
                WHERE owner_id = :ownerId
                  AND ST_DWithin(location::geography , ST_SetSRID((ST_MakePoint(:lon, :lat))::geography, 4326), 5)
            )
            """, nativeQuery = true)
    boolean storeExistsAtLocation(@Param("ownerId") Long ownerId,
                                  @Param("lat") double lat,
                                  @Param("lon") double lon);


    Optional<Store> findByOwnerIdAndLocation(Long ownerId, Point location);

    @Query(value = """
                SELECT
                s.id AS id,
                s.name AS  name,
                s.description AS description,
                s.is_active AS is_active,
                s.latitude AS latitude,
                s.longitude AS longitude,
                s.street AS street,
                s.city AS city,
                s.state AS state,
                s.country AS country,
                s.postal_code AS postal_code,
                s.location AS location,
                ST_Distance(
                            s.location::geography,
                            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
                ) AS distance_in_metres
            FROM store s
            WHERE s.is_active = true
                AND ST_DWithin(
                                s.location::geography,
                                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                                :radius
                )
            ORDER BY  distance_in_metres ASC
            """, nativeQuery = true)
    Optional<List<StoreProjection>> getNearbyStores(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radius") double radiusInMetres
    );              //user can specify the range

    /* The user searches the storr by name I am retrieving lat and lon from the store entity also
    i get that in the dto also , send it to FE, FE uses the longitude and latitude to plot the location
    on map, THERE SHOULD BE A BUTTON LIKE SHOW LOCATION ON MAP

     */
//    @Query(value = """
//            SELECT DISTINCT s
//                FROM Store s
//                JOIN FETCH s.inventory i
//                JOIN FETCH i.product p
//                WHERE s.isActive =true
//                AND LOWER(s.name) like LOWER(CONCAT('%', :storeName,'%') )
//                AND i.isActive = true
//                AND i.stockQuantity>0
//            """
//    )
    @Query(value = """
            SELECT DISTINCT s 
            FROM Store s
            WHERE s.isActive=true 
            AND LOWER(s.name) LIKE LOWER(CONCAT(:storeName,'%') )
            """
    )
    List<Store> searchStoreByName(@Param("storeName") String storeName);


    @Query(value = """
            SELECT
              s.id AS id,
              s.name AS name,
              s.description AS description,
              s.is_active AS is_active,
              s.latitude AS latitude,
              s.longitude AS longitude,
              s.street AS street,
              s.city AS city,
              s.state AS state,
              s.country AS country,
              s.postal_code AS postal_code,
              s.location AS location,
              ST_Distance(
                CAST(s.location AS geography),
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
              ) AS distance_in_metres
            FROM store s
            JOIN inventory i ON i.store_id = s.id
            JOIN product p   ON p.id = i.product_id
            WHERE s.is_active = true
              AND i.is_active = true
              AND i.stock_quantity >  0
              AND LOWER(p.name) LIKE  LOWER(CONCAT('%', :productName, '%'))
              AND ST_DWithin(
                    CAST(s.location AS geography),
                    ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                    :radiusInMetres
              )
            ORDER BY distance_in_metres ASC
            """,
            nativeQuery = true)
    Optional<List<StoreProjection>> searchNearbyStoresWithProductName(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("productName") String productName,
            @Param("radiusInMetres") double radiusInMetres
    );


    @Query(value = """
            SELECT
              s.id   AS id,
              s.name    AS name,
              s.street  AS street,
              s.city  AS city,
              s.state  AS state,
              s.country AS country,
              s.postal_code  AS postal_code,
              s.latitude AS latitude,
              s.longitude    AS longitude,
              ST_Distance(
                CAST(s.location AS geography),
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
              )  AS distance_in_metres
            FROM store s
            JOIN inventory i ON i.store_id    = s.id
            WHERE s.is_active     = true
              AND i.is_active     = true
              AND i.stock_quantity > 0
              AND i.product_id    = :productId
              AND ST_DWithin(
                    CAST(s.location AS geography),
                    ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                    :radiusInMeters
              )
            ORDER BY distance_in_metres ASC
            """,
            nativeQuery = true)
    Optional<List<StoreProjection>> findNearbyStoresWithProductId(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("productId") Long productId,
            @Param("radiusInMeters") double radiusInMetres
    );

    @Query(value = """
    SELECT
      s.id                AS id,
      s.name              AS name,
      s.street            AS street,
      s.city              AS city,
      s.state             AS state,
      s.country           AS country,
      s.postal_code       AS postalCode,
      s.description       AS description,
      s.latitude          AS latitude,
      s.longitude         AS longitude
      ts_rank(
        s.searchable,
        plainto_tsquery('english', :query)
      )                   AS rank
    FROM store s
    WHERE s.is_active
      AND s.searchable @@ plainto_tsquery('english', :query)
    ORDER BY rank DESC
    LIMIT 10
    """,
            nativeQuery = true)
    List<StoreProjection> searchByText(@Param("query") String query);

    @Query(value = """
    SELECT
      s.id,
      s.name,
      s.description,
      s.is_active            AS isActive,
      s.latitude,
      s.longitude,
      s.street,
      s.city,
      s.state,
      s.country,
      s.postal_code,
      ts_rank(
        s.searchable,
        plainto_tsquery('english', :query)
      )                       AS textRank,
      ST_Distance(
        s.location::geography,
        ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
      )                       AS distance_in_metres
    FROM store s
    JOIN inventory i ON i.store_id = s.id
    JOIN product p ON p.id = i.product_id
    WHERE s.is_active
      AND i.is_active
      AND i.stock_quantity > 0
      AND s.searchable @@ plainto_tsquery('english', :query)
      AND ST_DWithin(
            s.location::geography,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
            :distance
      )
    ORDER BY textRank DESC, distance_in_metres ASC
    LIMIT 10
    """,
            nativeQuery = true)
    List<StoreProjection> searchNearbyStoresByFullTextSearchAndProductInStock(
            @Param("query")  String query,
            @Param("lat")    double latitude,
            @Param("lon")    double longitude,
            @Param("distance") double distanceInMetres
    );

}


