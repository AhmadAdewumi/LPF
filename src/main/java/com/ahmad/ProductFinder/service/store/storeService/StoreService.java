package com.ahmad.ProductFinder.service.storeService;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.request.NearbyStoreSearchParams;
import com.ahmad.ProductFinder.dtos.request.UpdateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.NearbyStoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.PagedResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreWithInventoryDto;
import com.ahmad.ProductFinder.embedded.Address;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.IllegalArgumentException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Role;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.models.Tag;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.projection.StoreProjection;
import com.ahmad.ProductFinder.repositories.*;
import com.ahmad.ProductFinder.service.tagService.TagService;
import com.ahmad.ProductFinder.service.userService.UserService;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
public class StoreService implements IStoreService {
    private final GeometryFactory geometryFactory;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreMapper storeMapper;
    private final StoreQueryService storeQueryService;
    private final RoleRepository roleRepository;
    private final TagService tagService;

    public StoreService(GeometryFactory geometryFactory, StoreRepository storeRepository, UserRepository userRepository, StoreMapper storeMapper, StoreQueryService storeQueryService, RoleRepository roleRepository, TagService tagService) {
        this.geometryFactory = geometryFactory;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.storeMapper = storeMapper;
        this.storeQueryService = storeQueryService;
        this.roleRepository = roleRepository;
        this.tagService = tagService;
    }

    //User clicks map in the FE , FE gets coordinates that is lat and long send to BE ,
    //i convert it to point using geometry factory which gets processed using postGIS
    @Override
    @PreAuthorize("hasAnyRole('USER','STORE_OWNER','ADMIN')")
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
        if (storeRepository.storeExistsAtLocation(ownerId, latitude, longitude)) {
            log.error("Duplicate store creation attempt at location: lat={}, long={} by userId={}", latitude, longitude, ownerId);
            throw new AlreadyExistsException("Duplicate entry detected!, You already have a store at this location, consider updating your product list in your current store!");
        }

        Store store = buildStore(request);
        store.setOwner(owner);
        //validate user doesn't have store_owner role and add store_owner to user's role
        validateRoleAndAddStoreOwnerRole(owner);
        storeRepository.save(store);
        log.info("Store created successfully with name: {}", store.getName());
        return StoreResponseDto.from(store);

    }

    private void validateRoleAndAddStoreOwnerRole(User user) {
        final String STORE_OWNER_ROLE = "STORE_OWNER";
        boolean alreadyHasStoreOwnerRole = user.getRoles().stream().anyMatch(role -> Objects.equals(role.getName(), STORE_OWNER_ROLE));
        if (!alreadyHasStoreOwnerRole) {
            Role store_owner = roleRepository.findByName("STORE_OWNER").orElseThrow(() -> new ResourceNotFoundException("Role not found: " + STORE_OWNER_ROLE));
            log.info("Role {} added for user: {}", STORE_OWNER_ROLE, user.getUsername());
            user.getRoles().add(store_owner);
            userRepository.save(user);
        } else {
            log.info("User {} already has role: {}", user.getUsername(), STORE_OWNER_ROLE);
        }

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
    @PreAuthorize("hasRole('STORE_OWNER')")
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
    @PreAuthorize("hasAnyRole('STORE_OWNER','ADMIN')")
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
        log.info("User with ID: {} restored successfully!", storeId);
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
    public PagedResponseDto<StoreResponseDto> getAllStores(int page, int size, String sortBy, String direction) {
        log.info("In get all stores service method");
        log.info("Fetching all active stores with pagination: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<Store> storePage = fetchActiveStores(pageable);
        List<StoreResponseDto> storeResponseDtoList = mapStoresToDto(storePage);
        return buildPagedResponse(storeResponseDtoList, storePage);
    }

    private List<StoreResponseDto> mapStoresToDto(Page<Store> storePage) {
        return storePage.getContent()
                .stream()
                .map(StoreResponseDto::from)
                .toList();
    }

    @Override
    public List<StoreWithInventoryDto> searchStoresUsingStoreName(String storeName) {
        log.info("search store by name service method invoked");
        log.info("Searching for stores with name containing: {}", storeName);

        if (storeName == null || storeName.trim().length() < 3) {
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

    //    @Transactional(readOnly = true)

    @Override
    public PagedResponseDto<NearbyStoreResponseDto> findNearbyStores(@ModelAttribute NearbyStoreSearchParams params) {
        log.info("find nearby stores service method invoked");
        log.info("Searching for nearby stores within {} km of location (lat={}, long={}) and radius: {}km", params.getRadiusInKm(), params.getLatitude(), params.getLongitude(), params.getRadiusInKm());

        Pageable pageable = buildPageable(params.getPage(), params.getSize(), params.getSortBy(), params.getDirection());
        double radiusInMetres = convertKmToMetres(params.getRadiusInKm());
        Page<StoreProjection> resultPage = storeQueryService.retrieveNearbyStores(params.getLatitude(), params.getLongitude(), radiusInMetres, pageable);

        if (resultPage.isEmpty()) {
            log.warn("No nearby stores found within radius {} km", params.getRadiusInKm());
            throw new ResourceNotFoundException("No stores found within " + params.getRadiusInKm() + "km of your location !");
        }

        return storeMapper.toPagedResponseDto(resultPage);
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResponseDto<NearbyStoreResponseDto> findNearbyStoresWithProductName(NearbyStoreSearchParams params, String productName) {
        log.info("find nearby stores with product name service method invoked");
        log.info("Searching for nearby stores with product '{}' within {} km", productName, params.getRadiusInKm());

        Pageable pageable = buildPageable(params.getPage(), params.getSize(), params.getSortBy(), params.getDirection());
        double radiusInMetres = convertKmToMetres(params.getRadiusInKm());
        Page<StoreProjection> resultPage = storeQueryService.searchNearbyStoresWithProductName(params.getLatitude(), params.getLongitude(), radiusInMetres, pageable, productName);

        if (resultPage.isEmpty()) {
            log.warn("No nearby stores found with product: {}", productName);
            throw new ResourceNotFoundException("No product found with name : " + productName);
        }

        return storeMapper.toPagedResponseDto(resultPage);
    }

    @Override
    public List<NearbyStoreResponseDto> searchByFullTextSearch(String query) {
        log.info("Searching for nearby stores using query: {}", query);
        List<StoreProjection> results = storeQueryService.fullTextSearch(query);

        if (results.isEmpty()) {
            log.warn("No search matching: {}", query);
            throw new ResourceNotFoundException("No stores matching: " + query);
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    @Override
    public List<NearbyStoreResponseDto> searchNearbyWithByFullTextSearchAndProductInStock(String query, double lat, double lon, double radiusInKm) {
        log.info("Initiating nearby store search with FTS. Query: '{}', Latitude: {}, Longitude: {}, Distance (km): {}",
                query, lat, lon, radiusInKm);
        double radiusInMetres = convertKmToMetres(radiusInKm);
        List<StoreProjection> results = storeQueryService.searchNearbyWithByFullTextSearchAndProductInStock(query, lat, lon, radiusInMetres);
        if (results.isEmpty()) {
            log.warn("No search for nearby stores using FTS matching: {}", query);
            throw new ResourceNotFoundException("No Nearby stores matching: " + query);
        }
        return storeMapper.toNearbyStoreDtos(results);
    }

    @Override
    public List<NearbyStoreResponseDto> findNearbyStoresByProductId(double latitude, double longitude, double radiusInKm, Long productId) {
        log.info("find nearby stores with productID service method invoked");
        log.info("Searching for nearby stores with product ID: {} within {} km", productId, radiusInKm);

        double radiusInMetres = convertKmToMetres(radiusInKm);
        List<StoreProjection> results = storeQueryService.searchNearbyStoresWithProductId(latitude, longitude, radiusInMetres, productId);

        if (results.isEmpty()) {
            log.warn("No nearby stores found within {} km that have product ID {}", radiusInKm, productId);
            throw new ResourceNotFoundException(format("Oops, No nearby stores within radius, %.0f km , have that product in stock", radiusInKm));
        }

        return storeMapper.toNearbyStoreDtos(results);
    }

    private double convertKmToMetres(double km) {
        return km * 1000;
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    private Page<Store> fetchActiveStores(Pageable pageable) {
        Page<Store> page = storeRepository.findByIsActiveTrue(pageable);
        if (page.isEmpty()) {
            log.warn("No stores found in paginated query");
            throw new ResourceNotFoundException("No stores found !");
        }
        return page;
    }

    private PagedResponseDto<StoreResponseDto> buildPagedResponse(List<StoreResponseDto> dtoList, Page<Store> storePage) {
        return PagedResponseDto.<StoreResponseDto>builder()
                .content(dtoList)
                .pageNumber(storePage.getNumber())
                .pageSize(storePage.getSize())
                .totalElements(storePage.getNumberOfElements())
                .totalPages(storePage.getTotalPages())
                .isLast(storePage.isLast())
                .build();
    }


    @Override
    public void assignTagsToStore(Long storeId, Collection<String> tagNames) {
        /***
         * retrieve stores with that ID
         * resolve tags by using the find or create tags in tag service
         * set the tags (merge)
         * persist the store
         *
         ***/
        log.info("Assigning tags {} to store with ID: {}", tagNames, storeId);

        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            log.warn("Store with ID {} not found while assigning tags {}", storeId, tagNames);
            return new ResourceNotFoundException(format("Store with ID:%d , Not Found", storeId));
        });

        Collection<Tag> normalizedTags = tagService.findOrCreateTags(tagNames);
        log.debug("Resolved {} tag(s) from input: {}", normalizedTags.size(), tagNames);

        store.getTags().addAll(normalizedTags);
        log.info("Added {} tag(s) to store with ID: {}", normalizedTags.size(), storeId);

        storeRepository.save(store);
        log.info("Tags assigned and store saved successfully for store ID: {}", storeId);
    }


    @Override
    @Transactional
    public void removeTagsFromStore(Long storeId, String tagName) {
        /***
         * get the store using store id
         * get all the tags for the store
         * filter out the ones to delete
         * remove from the list
         * persist the store
         */
        log.info("Request to remove tag '{}' from store with ID: {}", tagName, storeId);
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            log.warn("Store with ID {} not found while attempting to remove tag '{}'", storeId, tagName);
            return new ResourceNotFoundException(format("Store with ID:%d , Not Found", storeId));
        });

        String trimmedTag = tagName.trim().toLowerCase();
        Collection<Tag> tags = store.getTags();
        log.debug("Store currently has {} tags before removal attempt", tags.size());
        Set<Tag> tagsToDelete = tags.stream().filter(tag -> tag.getName().equalsIgnoreCase(trimmedTag)).collect(Collectors.toSet());
        if (tagsToDelete.isEmpty()) {
            log.info("No matching tag '{}' found on store with ID {}", trimmedTag, storeId);
        } else {
            log.info("Removing {} matching tag(s) from store ID: {}", tagsToDelete.size(), storeId);
            tags.removeAll(tagsToDelete);
            storeRepository.save(store);
            log.info("Tag '{}' removed successfully from store with ID: {}", trimmedTag, storeId);
        }
    }

    @Override
    public PagedResponseDto<StoreResponseDto> findStoresByTags(Set<String> tagNames, boolean matchAll, Pageable pageable) {
        log.info("Finding storePage by tags: {}, matchAll: {}", tagNames, matchAll);

        Page<Store> storePage = matchAll
                ? storeRepository.findStoresWithAllTags(tagNames, tagNames.size(),pageable)
                : storeRepository.findStoresWithAnyTags(tagNames, pageable);

        if (storePage.isEmpty()) {
            log.warn("No storePage found for tags: {}", tagNames);
            throw new ResourceNotFoundException("No storePage found for the specified tags.");
        }

        List<StoreResponseDto> dtoList = storePage.stream()
                .map(StoreResponseDto::from)
                .toList();

        return PagedResponseDto.<StoreResponseDto>builder()
                .content(dtoList)
                .pageNumber(storePage.getNumber())
                .pageSize(storePage.getSize())
                .totalElements(storePage.getNumberOfElements())
                .totalPages(storePage.getTotalPages())
                .isLast(storePage.isLast())
                .build();
    }





}
