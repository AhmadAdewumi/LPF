package com.ahmad.ProductFinder.service.store.utils;

import com.ahmad.ProductFinder.dtos.request.CreateStoreRequestDto;
import com.ahmad.ProductFinder.dtos.response.PagedResponseDto;
import com.ahmad.ProductFinder.dtos.response.StoreResponseDto;
import com.ahmad.ProductFinder.embedded.Address;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.AlreadyExistsException;
import com.ahmad.ProductFinder.globalExceptionHandling.exceptions.ResourceNotFoundException;
import com.ahmad.ProductFinder.models.Role;
import com.ahmad.ProductFinder.models.Store;
import com.ahmad.ProductFinder.models.User;
import com.ahmad.ProductFinder.repositories.RoleRepository;
import com.ahmad.ProductFinder.repositories.StoreRepository;
import com.ahmad.ProductFinder.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Component
public class StoreUtils {
    private final GeometryFactory geometryFactory;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final RoleRepository roleRepository;

    public StoreUtils(GeometryFactory geometryFactory, UserRepository userRepository, StoreRepository storeRepository, RoleRepository roleRepository) {
        this.geometryFactory = geometryFactory;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.roleRepository = roleRepository;
    }

    /***
     * this was introduced in other to get the current logged-in user
     * and set the person as the owner of the store
     * to ensure I can put constraint on them(like a user can't have a store in the same location) maybe in the future sha
     * @return user
     */
    public User getCurrentUser(String username){
        return Optional.ofNullable(userRepository.findByUsernameAndActiveTrue(username))
                .orElseThrow(() -> {
                    log.error("User with username {} not found!", username);
                    return new ResourceNotFoundException(format("USER WITH USERNAME :, %s , NOT FOUND!", username));
                });
    }

    public PagedResponseDto<StoreResponseDto> buildPageMetadata(List<StoreResponseDto> dtoList, Page<Store> storePage){
        return PagedResponseDto.<StoreResponseDto>builder()
                .content(dtoList)
                .pageNumber(storePage.getNumber())
                .pageSize(storePage.getSize())
                .totalElements(storePage.getNumberOfElements())
                .totalPages(storePage.getTotalPages())
                .isLast(storePage.isLast())
                .build();
    }

    public void ifStoreAlreadyExistsForAUserInSameLocation(long ownerId , Double latitude, Double longitude){
        if (storeRepository.storeExistsAtLocation(ownerId, latitude, longitude)) {
            log.error("Duplicate store creation attempt at location: lat={}, long={} by userId={}", latitude, longitude, ownerId);
            throw new AlreadyExistsException("Duplicate entry detected!, You already have a store at this location, consider updating your product list in your current store!");
        }
    }

    public void validateRoleAndAddStoreOwnerRole(User user) {
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

    public Point convertCoordinatesToPoint(Double longitude, Double latitude){
        Point location = null;
        if (latitude != null && longitude != null) {
            location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            location.setSRID(4326);
        }
        assert location != null;

        return location;
    }

    // X -> longitude , Y -> latitude from Point from GeometryFactory

    public Store buildStore(CreateStoreRequestDto dto) {
        log.info("In build store helper service method");
        Address address = new Address();
        address.setCity(dto.address().city());
        address.setStreet(dto.address().street());
        address.setCountry(dto.address().country());
        address.setState(dto.address().state());
        address.setPostalCode(dto.address().postalCode());

        Point location = convertCoordinatesToPoint(dto.longitude(), dto.latitude());

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

    public List<StoreResponseDto> mapStoresToDto(Page<Store> storePage) {
        return storePage.getContent()
                .stream()
                .map(StoreResponseDto::from)
                .toList();
    }


    //    @Transactional(readOnly = true)

    public PagedResponseDto<StoreResponseDto> buildPagedResponse(List<StoreResponseDto> dtoList, Page<Store> storePage) {
        return PagedResponseDto.<StoreResponseDto>builder()
                .content(dtoList)
                .pageNumber(storePage.getNumber())
                .pageSize(storePage.getSize())
                .totalElements(storePage.getNumberOfElements())
                .totalPages(storePage.getTotalPages())
                .isLast(storePage.isLast())
                .build();
    }

    public Pageable buildPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    public Page<Store> fetchActiveStores(Pageable pageable) {
        Page<Store> page = storeRepository.findByIsActiveTrue(pageable);
        if (page.isEmpty()) {
            log.warn("No stores found in paginated query");
            throw new ResourceNotFoundException("No stores found !");
        }
        return page;
    }

    public Store fetchActiveStoreFromDb(long storeId){
        return storeRepository.findByIdAndIsActiveTrue(storeId)
                .orElseThrow(() -> {
                    log.error("Store with ID {} not found!", storeId);
                    return new ResourceNotFoundException("Store with this ID NOT FOUND");
                });
    }

    public Store fetchStoreFromDb(long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store with ID {} not found!", storeId);
                    return new ResourceNotFoundException("Store with this ID NOT FOUND");
                });
    }

    public Set<String> normalizeTagNames(Set<String> tagNames){
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptySet();
        }
        return tagNames.stream()
                .filter(Objects::nonNull) //skip null values
                .map(names -> names.trim())
                .filter(t -> !t.isEmpty()) //skip empty strings
                .map(names -> names.toLowerCase())
                .collect(Collectors.toSet());
    }
}
