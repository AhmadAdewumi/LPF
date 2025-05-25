package com.ahmad.ProductFinder.service.storeService;

import com.ahmad.ProductFinder.dtos.entityDto.AddressDto;
import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreWithInventoryDto;
import com.ahmad.ProductFinder.embedded.Address;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.projection.StoreProjection;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoreService implements IStoreService {
    private final GeometryFactory geometryFactory;
    private final StoreRepository storeRepository;

    public StoreService(GeometryFactory geometryFactory, StoreRepository storeRepository) {
        this.geometryFactory = geometryFactory;
        this.storeRepository = storeRepository;
    }

    //User clicks map in the FE , FE gets coordinates that is lat and long send to BE ,
    //i convert it to point using geometry factory which gets processed using postGIS
    @Override
    public void createStore(CreateStoreRequestDto dto) { //ON HOLD TILL SECURITY SET UP COZ OF OWNER ID
//        validate if store exists after i am  done with security as i have to extract owner id from JWT
//        Optional<Store> existing = storeRepository.findByOwnerIdAndLocation()

    }

    @Override
    public StoreResponseDto updateStore(Long storeId, UpdateStoreRequestDto dto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store with ID : " + storeId + " not found: "));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            store.setName(dto.getName());
        }

        store.setDescription(dto.getDescription());

        Point location = null;
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            location = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
            location.setSRID(4326);
        }

        store.setLocation(location);
        store.setUpdatedAt(LocalDateTime.now());
        store = storeRepository.save(store);
        return StoreResponseDto.from(store);
    }

    // X -> longitude , Y -> latitude from Point from GeometryFactory
    private Store buildStore(CreateStoreRequestDto dto) {
        Address address = new Address();
        address.setCity(dto.address().city());
        address.setStreet(dto.address().city());
        address.setCountry(dto.address().country());
        address.setState(dto.address().state());
        address.setPostalCode(dto.address().postalCode());


        Point location = null;
        if (dto.latitude() != null && dto.longitude() != null) {
            location = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
        }
//        assert location != null;
        location.setSRID(4326);

        Store store = new Store();
        store.setName(dto.name());
        store.setActive(true);
        store.setDescription(dto.description());
        store.setAddress(address);
        store.setCreatedAt(LocalDateTime.now());
        store.setLocation(location);

        return store;
    }

    @Override
    public void deleteStore(long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store with this ID NOT FOUND"));
        store.setActive(false);
        store.setUpdatedAt(LocalDateTime.now());
        storeRepository.save(store);
    }

    @Override
    public StoreResponseDto getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found: " + storeId));
        return StoreResponseDto.from(store);
    }

    @Override
    public List<StoreResponseDto> getAllStores() {
        List<Store> activeStores = storeRepository.findAllByIsActiveTrue();
        if (activeStores.isEmpty()){
            throw new ResourceNotFoundException("No stores found !");
        }
        return activeStores.stream()
                .map(StoreResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NearbyStoreResponseDto> findNearbyStores(double latitude, double longitude, double radiusInKm) {
        double radiusInMetres = radiusInKm * 1000;
        List<StoreProjection> results = storeRepository.getNearbyStores(latitude, longitude, radiusInMetres);
        if (results.isEmpty()){
            throw new ResourceNotFoundException("No stores found within " + radiusInKm +"km of your location !");
        }
        return results.stream()
                .map(
                        proj -> new NearbyStoreResponseDto(
                                proj.getId(),
                                proj.getName(),
                                new AddressDto(proj.getStreet(), proj.getCity(), proj.getState(), proj.getCountry(), proj.getPostal_code()),
                                proj.getDescription(),
                                proj.getLatitude(),
                                proj.getLongitude(),
                                true,
                                true,
                                proj.getDistance_in_metres()
                        )
                ).toList();
    }

    @Override
    public List<StoreWithInventoryDto> searchStoresByName(String storeName) {
        List<Store> stores = storeRepository.searchStoreByName(storeName);
        if (stores.isEmpty()){
            throw new ResourceNotFoundException("No stores found with name : " + storeName );
        }
        return stores.stream()
                .map(StoreWithInventoryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NearbyStoreResponseDto> findNearbyStoresWithProductName(double latitude, double longitude, double radiusInKm, String productName) {
        double radiusInMetres = radiusInKm * 1000;
        List<StoreProjection> results = storeRepository.searchNearbyStoresWithProductName(latitude, longitude, productName,radiusInMetres);
        if (results.isEmpty()){
            throw  new ResourceNotFoundException("No product found with name : " + productName);
        }
        return results.stream()
                .map(
                        proj -> new NearbyStoreResponseDto(
                                proj.getId(),
                                proj.getName(),
                                new AddressDto(proj.getStreet(), proj.getCity(), proj.getState(), proj.getCountry(), proj.getPostal_code()),
                                proj.getDescription(),
                                proj.getLatitude(),
                                proj.getLongitude(),
                                true,
                                true,
                                proj.getDistance_in_metres()
                        )
                ).toList();
    }


    @Override
    public List<NearbyStoreResponseDto> findNearbyStoresWithProductId(double latitude, double longitude, double radiusInKm, Long productId) {
        double radiusInMetres = radiusInKm * 1000;
        List<StoreProjection> results = storeRepository
                .findNearbyStoresWithProductId(latitude, longitude,productId,radiusInMetres)
                .orElseThrow(()-> new ResourceNotFoundException("No Nearby Product with ID " + productId + " Found !"));
        return results.stream()
                .map(
                        proj -> new NearbyStoreResponseDto(
                                proj.getId(),
                                proj.getName(),
                                new AddressDto(proj.getStreet(), proj.getCity(), proj.getState(), proj.getCountry(), proj.getPostal_code()),
                                proj.getDescription(),
                                proj.getLatitude(),
                                proj.getLongitude(),
                                true,
                                true,
                                proj.getDistance_in_metres()
                        )
                ).toList();
    }

//    @Override
//    public List<StoreResponseDto> findStoresInLocationWithProductName(double latitude, double longitude, double radiusInKm, String productName) {
//        return List.of();
//    }
//
//    @Override
//    public List<StoreResponseDto> findStoresInLocationWithProductId(double latitude, double longitude, double radiusInKm, Long productId) {
//        return List.of();
//    }
}
