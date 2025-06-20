//package com.ahmad.ProductFinder.initializationStuffs;
//
//import com.ahmad.ProductFinder.models.Role;
//import com.ahmad.ProductFinder.repositories.RoleRepository;
//import com.fasterxml.jackson.core.JacksonException;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonDeserializer;
//import jakarta.persistence.Transient;
//
//import java.io.IOException;
//
//public class RoleDeserializer extends JsonDeserializer<Role> {
//    private final RoleRepository roleRepository;
//
//    public RoleDeserializer(RoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//    }
//
//    @Override
//    public Role deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
//        String roleName=jsonParser.getValueAsString();
//        return roleRepository.save(new Role(roleName));
////        return roleRepository.save()
//    }
//}
