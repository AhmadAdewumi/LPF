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

    Optional<Store> findByOwnerIdAndLocation(Long ownerId, Point location);

    List<Store> findAllByIsActiveTrue();


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
                            ST_SetSRID(ST_MakePoint(:lat, :lon), 4326)::geography
                ) AS distance_in_metres
            FROM store s
            WHERE s.is_active = true
                AND ST_DWithin(
                                s.location::geography,
                                ST_SetSRID(ST_MakePoint(:lat, :lon), 4326)::geography,
                                :radius
                )
            ORDER BY  distance_in_metres ASC
            """, nativeQuery = true)
    List<StoreProjection> getNearbyStores(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("radius") double radiusInMetres
    );              //user can specify the range

    /* The user searches the storr by name I am retrieving lat and lon from the store entity also
    i get that in the dto also , send it to FE, FE uses the longitude and latitude to plot the location
    on map, THERE SHOULD BE A BUTTON LIKE SHOW LOCATION ON MAP

     */
    @Query(value = """
            SELECT DISTINCT s
                FROM Store s
                JOIN FETCH s.inventory i
                JOIN FETCH i.product p
                WHERE s.isActive =true
                AND LOWER(s.name) like LOWER(CONCAT('%', :storeName,'%') ) 
                AND i.isActive = true
                AND i.stockQuantity>0
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
                s.location::geography,
                ST_SetSRID(ST_MakePoint(:lat, :lon), 4326)::geography
              )             AS distance_in_meters
            FROM store s
            JOIN inventory i ON i.store_id = s.id
            JOIN product p   ON p.id = i.product_id
            WHERE s.is_active = true
              AND i.is_active = true
              AND i.stock_quantity >  0
              AND LOWER(p.name) LIKE  LOWER(CONCAT('%', :productName, '%'))
              AND ST_DWithin(
                    s.location::geography,
                    ST_SetSRID(ST_MakePoint(:lat, :lon), 4326)::geography,
                    :radiusInMeters
              )
            ORDER BY distance_in_meters ASC
            """,
            nativeQuery = true)
    List<StoreProjection> searchNearbyStoresWithProductName(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("productName") String productName,
            @Param("radiusInMeters") double radiusInMeters
    );


    @Query(value = """
            SELECT
              s.id   AS id,
              s.name    AS name,
              s.address_street  AS street,
              s.address_city  AS city,
              s.address_state  AS state,
              s.address_country AS country,
              s.address_postal_code  AS postal_code,
              s.latitude AS latitude,
              s.longitude    AS longitude,
              ST_Distance(
                CAST(s.location AS geography),
                ST_SetSRID(ST_MakePoint(:lat, :lon), 4326)::geography
              )                       AS distance_in_metres
            FROM store s
            JOIN inventory i ON i.store_id    = s.id
            WHERE s.is_active     = true
              AND i.is_active     = true
              AND i.stock_quantity > 0
              AND i.product_id    = :productId
              AND ST_DWithin(
                    CAST(s.location AS geography),
                    ST_SetSRID(ST_MakePoint(:lat, :lon), 4326)::geography,
                    :radiusInMeters
              )
            ORDER BY distance_in_metres ASC
            """,
            nativeQuery = true)
    Optional<List<StoreProjection>> findNearbyStoresWithProductId(
            @Param("lat") double latitude,
            @Param("lon") double longitude,
            @Param("productId") Long productId,
            @Param("radiusInMeters") double radiusInMeters
    );

}

