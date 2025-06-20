package com.ahmad.ProductFinder.initializationStuffs;

import com.ahmad.ProductFinder.models.Role;
import com.ahmad.ProductFinder.repositories.RoleRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RoleInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
//        injectIfMissing("ADMIN");
//        injectIfMissing("USER");
//        injectIfMissing("STORE_OWNER");
        Set<String> roles = Set.of("USER","ADMIN","STORE_OWNER");
        createDefaultRolesIfNotExist(roles);
    }

//    public void injectIfMissing(String roleName){
//        if (!roleRepository.existsByName(roleName)){
//            Role role = new Role();
//            role.setName(roleName);
//            roleRepository.save(role);
//        }
//    }

    private void createDefaultRolesIfNotExist(Set<String> roles){
        roles.stream()
                .filter(role -> !roleRepository.existsByName(role))
                .map(Role::new).forEach(roleRepository :: save);
    }

}
