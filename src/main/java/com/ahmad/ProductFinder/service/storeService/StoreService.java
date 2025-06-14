package com.ahmad.ProductFinder.service.storeService;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreWithInventoryDto;
import com.ahmad.ProductFinder.embedded.Address;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.projection.StoreProjection;
import com.ahmad.ProductFinder.repositories.ProductRepository;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import com.ahmad.ProductFinder.repositories.UserRepository;
import com.ahmad.ProductFinder.service.userService.UserService;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Service
public class StoreService implements IStoreService {
    private final GeometryFactory geometryFactory;
    private final StoreRepository storeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreQueryService queryService;
    private final StoreMapper storeMapper;
    private final StoreQueryService storeQueryService;

    public StoreService(GeometryFactory geometryFactory, StoreRepository storeRepository, UserService userService, UserRepository userRepository, ProductRepository productRepository, StoreQueryService queryService, StoreMapper storeMapper, StoreQueryService storeQueryService) {
        this.geometryFactory = geometryFactory;
        this.storeRepository = storeRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.queryService = queryService;
        this.storeMapper = storeMapper;
        this.storeQueryService = storeQueryService;
    }

    //User clicks map in the FE , FE gets coordinates that is lat and long send to BE ,
    //i convert it to point using geometry factory which gets processed using postGIS
    @Override
    public StoreResponseDto createStore(CreateStoreRequestDto request) {
        log.info("In create store service method");//ON HOLD TILL SECURITY SET UP COZ OF OWNER ID
        log.info("Attempting to create a store for request: {}", request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Authenticated username: {}", username);

        User owner = Optional.ofNullable(userRepository.findByUsernameAndActiveTrue(username))
                .orElseThrow(() -> {
                    log.error("User with username {} not found!", username);
                    return new ResourceNotFoundException(format("USER WITH USERNAME :, %s , NOT FOUND!", username));
                });

        //store must not exist in the same location validator
        Long ownerId = owner.getId();
        Double latitude = request.latitude();
        Double longitude = request.longitude();
        if (storeRepository.storeExistsAtLocation(ownerId,latitude,longitude)){
            log.error("Duplicate store creation attempt at location: lat={}, long={} by userId={}", latitude, longitude, ownerId);
            throw new AlreadyExistsException("Duplicate entry detected!, You already have a store at this location, consider updating your product list in your current store!");
        }

        return Optional.of(request)
                .filter(user -> userService.getAuthenticatedUser(username))
                .map(req -> {
                    Store store = buildStore(request);
                    store.setOwner(owner);

                    storeRepository.save(store);
                    log.info("Store created successfully with name: {}", store.getName());

                    return StoreResponseDto.from(store);
                }).orElseThrow(() -> {
                    log.error("User with username {} not found in authenticated check", request.username());
                    return new ResourceNotFoundException(format("User with username,%s , Not Found! , Please signUp and try again", request.username()));
                });
    }

    // X -> longitude , Y -> latitude from Point from GeometryFactory
    private Store buildStore(CreateStoreRequestDto dto) {
        log.info("In build store helper service method");
        Address address = new Address();
        address.setCity(dto.address().city());
        address.setStreet(dto.address().street());
        address.setCountry(dto.address().country());
        address.setState(dto.address().state());
        address.setPostalCode(dto.address().postalCode());

        Point location = null;
        if (dto.latitude() != null && dto.longitude() != null) {
            location = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
        }
        assert location != null;
        location.setSRID(4326);

        Store store = new Store();
        store.setLatitude(dto.latitude());
        store.setLongitude(dto.longitude());
        store.setName(dto.name());
        store.setActive(true);
        store.setDescription(dto.description());
        store.setAddress(address);
        store.setCreatedAt(LocalDateTime.now());
        store.setLocation(location);
//        store.setOwner(dto.ownerId());

        return store;
    }

    @Override
    public StoreResponseDto updateStore(Long storeId, UpdateStoreRequestDto dto) {
        log.info("In update store service method");
        log.info("Updating store with ID: {}", storeId);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store with ID {} not found for update", storeId);
                    return new ResourceNotFoundException("Store with ID : " + storeId + " not found!");
                });

        if (dto.getName() != null && !dto.getName().isBlank()) {
            store.setName(dto.getName());
        }

        store.setDescription(dto.getDescription());
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());

        Point location = null;
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            location = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
            location.setSRID(4326);
        }
        store.setLocation(location);
        store.setUpdatedAt(LocalDateTime.now());

        store = storeRepository.save(store);
        log.info("Store with ID {} updated successfully", storeId);

        return StoreResponseDto.from(store);
    }

    @Override
    public void deleteStore(Long storeId) {
        log.info("In delete store for real service method");
        log.info("Attempting to permanently delete store with ID: {}", storeId);
        storeRepository.findById(storeId).orElseThrow(() -> {
            log.warn("Permanent delete failed - store with ID {} not found", storeId);
            return new ResourceNotFoundException(format("Store with ID:%d , Not Found", storeId));
        });
        storeRepository.deleteById(storeId);
        log.info("Store with ID {} permanently deleted", storeId);
    }

    @Override
    public void disableStore(long storeId) {
        log.info("In disable store service method");
        log.info("Disabling (deactivating i.e set to false) store with ID: {}", storeId);

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store with ID {} not found for disabling", storeId);
                    return new ResourceNotFoundException("Store with this ID NOT FOUND");
                });

        store.setActive(false);
        store.setUpdatedAt(LocalDateTime.now());
        storeRepository.save(store);

        log.info("Store with ID {} has been deactivated", storeId);
    }

    @Override
    public StoreResponseDto restoreStore(Long storeId) {
        log.info("Attempting to restore user with ID: {}", storeId);
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            log.warn("Restore store failed - user with ID {} not found", storeId);
            return new ResourceNotFoundException(format("User with ID:%d , Not Found", storeId));
        });

        store.setActive(true);
        storeRepository.save(store);
        log.info("User with ID: {} restored successfully!",storeId);
        return StoreResponseDto.from(store);
    }


    @Override
    public StoreResponseDto getStoreUsingStoreId(Long storeId) {
        log.info("In find by id store service method");
        log.info("Fetching store with ID: {}", storeId);

        Store store = storeRepository.findByIdAndIsActiveTrue(storeId)
                .orElseThrow(() -> {
                    log.error("Store with ID {} not found", storeId);
                    return new ResourceNotFoundException(format("Store with ID: %d, not found: ", storeId));
                });

        return StoreResponseDto.from(store);
    }

    @Override
    public List<StoreResponseDto> getAllStores() {
        log.info("In get all stores service method");
        log.info("Fetching all active stores");

        List<Store> activeStores = storeRepository.findByIsActiveTrue();

        if (activeStores.isEmpty()) {
            log.warn("No stores found in repository");
            throw new ResourceNotFoundException("No stores found !");
        }

        return activeStores.stream()
                .map(StoreResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NearbyStoreResponseDto> findNearbyStores(double latitude, double longitude, double radiusInKm) {
        log.info("find nearby stores service method invoked");
        log.info("Searching for nearby stores within {} km of location (lat={}, long={})", radiusInKm, latitude, longitude);

        double radiusInMetres = convertKmToMetres(radiusInKm);
        List<StoreProjection> results = storeQueryService.retrieveNearbyStores(latitude, longitude, radiusInMetres);

        if (results.isEmpty()) {
            log.warn("No nearby stores found within radius {} km", radiusInKm);
            throw new ResourceNotFoundException("No stores found within " + radiusInKm + "km of your location !");
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    @Override
    public List<StoreWithInventoryDto> searchStoresUsingStoreName(String storeName) {
        log.info("search store by name service method invoked");
        log.info("Searching for stores with name containing: {}", storeName);

        if (storeName==null || storeName.trim().length()<3){
            throw new IllegalArgumentException("Search term must be at least 3 characters");
        }

        List<Store> stores = storeRepository.searchStoreByName(storeName);

        if (stores.isEmpty()) {
            log.warn("No stores found with name: {}", storeName);
            throw new ResourceNotFoundException("No stores found with name : " + storeName);
        }

        return stores.stream()
                .map(StoreWithInventoryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<NearbyStoreResponseDto> findNearbyStoresWithProductName(double latitude, double longitude, double radiusInKm, String productName) {
        log.info("find nearby stores with product name service method invoked");
        log.info("Searching for nearby stores with product '{}' within {} km", productName, radiusInKm);

        double radiusInMetres = convertKmToMetres(radiusInKm);
        List<StoreProjection> results = storeQueryService.searchNearbyStoresWithProductName(latitude,longitude,radiusInMetres,productName);

        if (results.isEmpty()) {
            log.warn("No nearby stores found with product: {}", productName);
            throw new ResourceNotFoundException("No product found with name : " + productName);
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    @Override
    public List<NearbyStoreResponseDto> findNearbyStoresByProductId(double latitude, double longitude, double radiusInKm, Long productId) {
        log.info("find nearby stores with productID service method invoked");
        log.info("Searching for nearby stores with product ID: {} within {} km", productId, radiusInKm);

        double radiusInMetres = convertKmToMetres(radiusInKm);
        List<StoreProjection> results = storeQueryService.searchNearbyStoresWithProductId(latitude,longitude,radiusInMetres,productId);

        if (results.isEmpty()) {
            log.warn("No nearby stores found within {} km that have product ID {}", radiusInKm, productId);
            throw new ResourceNotFoundException(format("Oops, No nearby stores within radius, %.0f km , have that product in stock", radiusInKm));
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    private double convertKmToMetres(double km){
        return km*1000;
    }

}
