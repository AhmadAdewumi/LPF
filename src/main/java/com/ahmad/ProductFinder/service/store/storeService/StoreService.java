package com.ahmad.ProductFinder.service.store.storeService;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.PagedResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreWithInventoryDto;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.models.Tag;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.projection.StoreProjection;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import com.ahmad.ProductFinder.service.store.utils.StoreUtils;
import com.ahmad.ProductFinder.service.tagService.TagService;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StoreService implements IStoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final StoreQueryService storeQueryService;
    private final TagService tagService;
    private final StoreUtils storeUtils;

    public StoreService(StoreRepository storeRepository, StoreMapper storeMapper, StoreQueryService storeQueryService, TagService tagService, StoreUtils storeUtils) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
        this.storeQueryService = storeQueryService;
        this.tagService = tagService;
        this.storeUtils = storeUtils;
    }

    //User clicks map in the FE , FE gets coordinates that is lat and long send to BE ,
    //I convert it to point using geometry factory which gets processed using postGIS
    @Transactional
    @Override
    @PreAuthorize("hasAnyRole('USER','STORE_OWNER','ADMIN')")
    public StoreResponseDto createStore(CreateStoreRequestDto request) {
        log.info("Creating store for request: {}", request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("Authenticated username: {}", username);

        User owner = storeUtils.getCurrentUser(username);

        Long ownerId = owner.getId();
        Double latitude = request.latitude();
        Double longitude = request.longitude();
        storeUtils.ifStoreAlreadyExistsForAUserInSameLocation(ownerId, latitude, longitude);

        Store store = storeUtils.buildStore(request);
        store.setOwner(owner);
        storeUtils.validateRoleAndAddStoreOwnerRole(owner);

        storeRepository.save(store);
        log.info("Store created successfully: ID={} Name={}", store.getId(), store.getName());
        return StoreResponseDto.from(store);
    }

    @Transactional
    @Override
    @PreAuthorize("hasRole('STORE_OWNER')")
    public StoreResponseDto updateStore(Long storeId, UpdateStoreRequestDto dto) {
        log.info("Updating store ID: {} with data: {}", storeId, dto);

        Store store = storeUtils.fetchActiveStoreFromDb(storeId);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            store.setName(dto.getName());
        }

        store.setDescription(dto.getDescription());
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());
        Point location = storeUtils.convertCoordinatesToPoint(dto.getLongitude(), dto.getLatitude());
        store.setLocation(location);
        store.setUpdatedAt(LocalDateTime.now());

        store = storeRepository.save(store);
        log.info("Store updated: ID={}", storeId);

        return StoreResponseDto.from(store);
    }

    @Override
    @PreAuthorize("hasAnyRole('STORE_OWNER','ADMIN')")
    public void deleteStore(Long storeId) {
        log.info("Deleting store permanently with ID: {}", storeId);
        storeUtils.fetchStoreFromDb(storeId);
        storeRepository.deleteById(storeId);
        log.info("Store with ID {} deleted", storeId);
    }

    @Override
    public void disableStore(long storeId) {
        log.info("Disabling store ID: {}", storeId);
        Store store = storeUtils.fetchStoreFromDb(storeId);
        store.setActive(false);
        store.setUpdatedAt(LocalDateTime.now());
        storeRepository.save(store);
        log.info("Store ID {} disabled", storeId);
    }

    @Override
    public StoreResponseDto restoreStore(Long storeId) {
        log.info("Restoring store ID: {}", storeId);
        Store store = storeUtils.fetchStoreFromDb(storeId);
        store.setActive(true);
        storeRepository.save(store);
        log.info("Store ID {} restored", storeId);
        return StoreResponseDto.from(store);
    }

    @Override
    public StoreResponseDto getStoreUsingStoreId(Long storeId) {
        log.info("Fetching store by ID: {}", storeId);
        Store store = storeUtils.fetchActiveStoreFromDb(storeId);
        return StoreResponseDto.from(store);
    }

    @Override
    public PagedResponseDto<StoreResponseDto> getAllStores(int page, int size, String sortBy, String direction) {
        log.info("Retrieving all stores. Page: {}, Size: {}, SortBy: {}, Direction: {}", page, size, sortBy, direction);
        Pageable pageable = storeUtils.buildPageable(page, size, sortBy, direction);
        Page<Store> storePage = storeUtils.fetchActiveStores(pageable);
        List<StoreResponseDto> storeResponseDtoList = storeUtils.mapStoresToDto(storePage);
        return storeUtils.buildPagedResponse(storeResponseDtoList, storePage);
    }

    @Override
    public List<StoreWithInventoryDto> searchStoresUsingStoreName(String storeName) {
        log.info("Searching stores by name: {}", storeName);

        if (storeName == null || storeName.trim().length() < 3) {
            throw new IllegalArgumentException("Search term must be at least 3 characters");
        }

        List<Store> stores = storeRepository.searchStoreByName(storeName);

        if (stores.isEmpty()) {
            log.warn("No stores found with name: {}", storeName);
            throw new ResourceNotFoundException("No stores found with name : " + storeName);
        }

        return stores.stream().map(StoreWithInventoryDto::from).toList();
    }

    @Override
    public List<NearbyStoreResponseDto> searchByFullTextSearch(String query) {
        log.info("Full text search query: {}", query);
        List<StoreProjection> results = storeQueryService.fullTextSearch(query);

        if (results.isEmpty()) {
            log.warn("No stores matched FTS query: {}", query);
            throw new ResourceNotFoundException("No stores matching: " + query);
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    @Override
    @Transactional
    public void removeTagsFromStore(Long storeId, String tagName) {
        log.info("Removing tag '{}' from store ID: {}", tagName, storeId);
        Store store = storeUtils.fetchStoreFromDb(storeId);

        String trimmedTag = tagName.trim().toLowerCase();
        Collection<Tag> tags = store.getTags();
        log.debug("Tags before removal: {}", tags);
        Set<Tag> tagsToDelete = tags.stream().filter(tag -> tag.getName().equalsIgnoreCase(trimmedTag)).collect(Collectors.toSet());

        if (tagsToDelete.isEmpty()) {
            log.info("No matching tag '{}' found on store ID: {}", trimmedTag, storeId);
        } else {
            log.info("Removing {} tag(s) from store ID: {}", tagsToDelete.size(), storeId);
            tags.removeAll(tagsToDelete);
            storeRepository.save(store);
            log.info("Tag '{}' removed successfully from store ID: {}", trimmedTag, storeId);
        }
    }

    @Transactional
    @Override
    public PagedResponseDto<StoreResponseDto> findStoresByTags(Set<String> tagNames, boolean matchAll, Pageable pageable) {
        log.info("Filtering stores by tags: {} | matchAll: {}", tagNames, matchAll);

        Page<Store> storePage = matchAll
                ? storeRepository.findStoresWithAllTags(tagNames, tagNames.size(), pageable)
                : storeRepository.findStoresWithAnyTags(tagNames, pageable);

        if (storePage.isEmpty()) {
            log.warn("No stores found for tags: {}", tagNames);
            throw new ResourceNotFoundException("No storePage found for the specified tags.");
        }

        List<StoreResponseDto> dtoList = storePage.stream().map(StoreResponseDto::from).toList();
        return storeUtils.buildPageMetadata(dtoList, storePage);
    }

    @Override
    @Transactional
    public void assignTagsToStore(Long storeId, Collection<String> tagNames) {
        log.info("Assigning tags {} to store ID: {}", tagNames, storeId);

        Store store = storeUtils.fetchStoreFromDb(storeId);

        Collection<Tag> normalizedTags = tagService.findOrCreateTags(tagNames);
        log.debug("Resolved {} tags from input: {}", normalizedTags.size(), tagNames);

        store.getTags().addAll(normalizedTags);
        log.info("Tags assigned to store ID: {}", storeId);
        storeRepository.save(store);
    }
}

//=================================================================== HELPER METHODS ================================================================

    /***
     * this was introduced in other to get the current logged-in user
     * and set the person as the owner of the store
     * to ensure I can put constraint on them(like a user can't have a store in the same location) maybe in the future sha
     * @return user
     */
//    private User getCurrentUser(String username){
//        return Optional.ofNullable(userRepository.findByUsernameAndActiveTrue(username))
//                .orElseThrow(() -> {
//                    log.error("User with username {} not found!", username);
//                    return new ResourceNotFoundException(format("USER WITH USERNAME :, %s , NOT FOUND!", username));
//                });
//    }
//
//    private PagedResponseDto<StoreResponseDto> buildPageMetadata(List<StoreResponseDto> dtoList, Page<Store> storePage){
//        return PagedResponseDto.<StoreResponseDto>builder()
//                .content(dtoList)
//                .pageNumber(storePage.getNumber())
//                .pageSize(storePage.getSize())
//                .totalElements(storePage.getNumberOfElements())
//                .totalPages(storePage.getTotalPages())
//                .isLast(storePage.isLast())
//                .build();
//    }
//
//    private void ifStoreAlreadyExistsForAUserInSameLocation(long ownerId , Double latitude, Double longitude){
//        if (storeRepository.storeExistsAtLocation(ownerId, latitude, longitude)) {
//            log.error("Duplicate store creation attempt at location: lat={}, long={} by userId={}", latitude, longitude, ownerId);
//            throw new AlreadyExistsException("Duplicate entry detected!, You already have a store at this location, consider updating your product list in your current store!");
//        }
//    }
//
//    private void validateRoleAndAddStoreOwnerRole(User user) {
//        final String STORE_OWNER_ROLE = "STORE_OWNER";
//        boolean alreadyHasStoreOwnerRole = user.getRoles().stream().anyMatch(role -> Objects.equals(role.getName(), STORE_OWNER_ROLE));
//        if (!alreadyHasStoreOwnerRole) {
//            Role store_owner = roleRepository.findByName("STORE_OWNER").orElseThrow(() -> new ResourceNotFoundException("Role not found: " + STORE_OWNER_ROLE));
//            log.info("Role {} added for user: {}", STORE_OWNER_ROLE, user.getUsername());
//            user.getRoles().add(store_owner);
//            userRepository.save(user);
//        } else {
//            log.info("User {} already has role: {}", user.getUsername(), STORE_OWNER_ROLE);
//        }
//
//    }
//
//    private Point convertCoordinatesToPoint(Double longitude, Double latitude){
//        Point location = null;
//        if (latitude != null && longitude != null) {
//            location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
//            location.setSRID(4326);
//        }
//        assert location != null;
//
//        return location;
//    }
//
//    // X -> longitude , Y -> latitude from Point from GeometryFactory
//
//    private Store buildStore(CreateStoreRequestDto dto) {
//        log.info("In build store helper service method");
//        Address address = new Address();
//        address.setCity(dto.address().city());
//        address.setStreet(dto.address().street());
//        address.setCountry(dto.address().country());
//        address.setState(dto.address().state());
//        address.setPostalCode(dto.address().postalCode());
//
//        Point location = convertCoordinatesToPoint(dto.longitude(), dto.latitude());
//
//        Store store = new Store();
//        store.setLatitude(dto.latitude());
//        store.setLongitude(dto.longitude());
//        store.setName(dto.name());
//        store.setActive(true);
//        store.setDescription(dto.description());
//        store.setAddress(address);
//        store.setCreatedAt(LocalDateTime.now());
//        store.setLocation(location);
////        store.setOwner(dto.ownerId());
//
//        return store;
//    }
//
//    private List<StoreResponseDto> mapStoresToDto(Page<Store> storePage) {
//        return storePage.getContent()
//                .stream()
//                .map(StoreResponseDto::from)
//                .toList();
//    }
//
//
//    //    @Transactional(readOnly = true)
//
//    private PagedResponseDto<StoreResponseDto> buildPagedResponse(List<StoreResponseDto> dtoList, Page<Store> storePage) {
//        return PagedResponseDto.<StoreResponseDto>builder()
//                .content(dtoList)
//                .pageNumber(storePage.getNumber())
//                .pageSize(storePage.getSize())
//                .totalElements(storePage.getNumberOfElements())
//                .totalPages(storePage.getTotalPages())
//                .isLast(storePage.isLast())
//                .build();
//    }
//
//    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
//        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
//        return PageRequest.of(page, size, sort);
//    }
//
//    private Page<Store> fetchActiveStores(Pageable pageable) {
//        Page<Store> page = storeRepository.findByIsActiveTrue(pageable);
//        if (page.isEmpty()) {
//            log.warn("No stores found in paginated query");
//            throw new ResourceNotFoundException("No stores found !");
//        }
//        return page;
//    }
//
//    private Store fetchActiveStoreFromDb(long storeId){
//        return storeRepository.findByIdAndIsActiveTrue(storeId)
//                .orElseThrow(() -> {
//                    log.error("Store with ID {} not found!", storeId);
//                    return new ResourceNotFoundException("Store with this ID NOT FOUND");
//                });
//    }
//
//    private Store fetchStoreFromDb(long storeId){
//        return storeRepository.findById(storeId)
//                .orElseThrow(() -> {
//                    log.error("Store with ID {} not found!", storeId);
//                    return new ResourceNotFoundException("Store with this ID NOT FOUND");
//                });
//    }

    //=====================================================================

//    @Override
//    public List<NearbyStoreResponseDto> searchNearbyWithFullTextSearchAndProductInStock(String query, double lat, double lon, double radiusInKm) {
//        log.info("Initiating nearby store search with FTS. Query: '{}', Latitude: {}, Longitude: {}, Distance (km): {}",
//                query, lat, lon, radiusInKm);
//        double radiusInMetres = convertKmToMetres(radiusInKm);
//        List<StoreProjection> results = storeQueryService.searchNearbyWithByFullTextSearchAndProductInStock(query, lat, lon, radiusInMetres);
//        if (results.isEmpty()) {
//            log.warn("No search for nearby stores using FTS matching: {}", query);
//            throw new ResourceNotFoundException("No Nearby stores matching: " + query);
//        }
//        return storeMapper.toNearbyStoreDtos(results);
//    }
//
//    private double convertKmToMetres(double km) {
//        return km * 1000;
//    }
//
//    @Override
//    public PagedResponseDto<NearbyStoreResponseDto> findNearbyStores(@ModelAttribute NearbyStoreSearchParams params) {
//        log.info("find nearby stores service method invoked");
//        log.info("Searching for nearby stores within {} km of location (lat={}, long={}) and radius: {}km", params.getRadiusInKm(), params.getLatitude(), params.getLongitude(), params.getRadiusInKm());
//
//        Pageable pageable = buildPageable(params.getPage(), params.getSize(), params.getSortBy(), params.getDirection());
//        double radiusInMetres = convertKmToMetres(params.getRadiusInKm());
//        Page<StoreProjection> resultPage = storeQueryService.retrieveNearbyStores(params.getLatitude(), params.getLongitude(), radiusInMetres, pageable);
//
//        if (resultPage.isEmpty()) {
//            log.warn("No nearby stores found within radius {} km", params.getRadiusInKm());
//            throw new ResourceNotFoundException("No stores found within " + params.getRadiusInKm() + "km of your location !");
//        }
//
//        return storeMapper.toPagedResponseDto(resultPage);
//    }
//
//    @Override
//    public List<NearbyStoreResponseDto> findNearbyStoresByProductId(double latitude, double longitude, double radiusInKm, Long productId) {
//        log.info("find nearby stores with productID service method invoked");
//        log.info("Searching for nearby stores with product ID: {} within {} km", productId, radiusInKm);
//
//        double radiusInMetres = convertKmToMetres(radiusInKm);
//        List<StoreProjection> results = storeQueryService.searchNearbyStoresWithProductId(latitude, longitude, radiusInMetres, productId);
//
//        if (results.isEmpty()) {
//            log.warn("No nearby stores found within {} km that have product ID {}", radiusInKm, productId);
//            throw new ResourceNotFoundException(format("Oops, No nearby stores within radius, %.0f km , have that product in stock", radiusInKm));
//        }
//
//        return storeMapper.toNearbyStoreDtos(results);
//    }
//
//    @Transactional(readOnly = true)
//    @Override
//    public PagedResponseDto<NearbyStoreResponseDto> findNearbyStoresWithProductName(NearbyStoreSearchParams params, String productName) {
//        log.info("find nearby stores with product name service method invoked");
//        log.info("Searching for nearby stores with product '{}' within {} km", productName, params.getRadiusInKm());
//
//        Pageable pageable = buildPageable(params.getPage(), params.getSize(), params.getSortBy(), params.getDirection());
//        double radiusInMetres = convertKmToMetres(params.getRadiusInKm());
//        Page<StoreProjection> resultPage = storeQueryService.searchNearbyStoresWithProductName(params.getLatitude(), params.getLongitude(), radiusInMetres, pageable, productName);
//
//        if (resultPage.isEmpty()) {
//            log.warn("No nearby stores found with product: {}", productName);
//            throw new ResourceNotFoundException("No product found with name : " + productName);
//        }
//
//        return storeMapper.toPagedResponseDto(resultPage);
//    }
//
//



