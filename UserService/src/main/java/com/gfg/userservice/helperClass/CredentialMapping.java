package com.gfg.userservice.helperClass;


import com.gfg.userservice.domain.entity.Credential;
import com.gfg.userservice.domain.entity.User;
import com.gfg.userservice.domain.dto.CredentialDTO;
import com.gfg.userservice.domain.dto.UserDTO;

public interface CredentialMapping {

    public static CredentialDTO map(final Credential credential) {
        return CredentialDTO.builder()
                .credentialId(credential.getCredentialId())
                .username(credential.getUsername())
                .password(credential.getPassword())
                .roleBasedAuthority(credential.getRoleBasedAuthority())
                .isEnabled(credential.getIsEnabled())
                .isAccountNonExpired(credential.getIsAccountNonExpired())
                .isAccountNonLocked(credential.getIsAccountNonLocked())
                .isCredentialNonExpired(credential.getIsCredentialsNonExpired())
                .userDTO(
                        UserDTO.builder()
                                .userId(credential.getUser().getUserId())
                                .fullName(credential.getUser().getFullName())
                                .sex(credential.getUser().getSex())
                                .imageUrl(credential.getUser().getImageUrl())
                                .email(credential.getUser().getEmail())
                                .phone(credential.getUser().getPhone())
                                .adress(credential.getUser().getAdress())
                                .birthday(credential.getUser().getBirthday())
                                .build())
                .build();
    }

    public static Credential map(final CredentialDTO credentialDto) {
        return Credential.builder()
                .credentialId(credentialDto.getCredentialId())
                .username(credentialDto.getUsername())
                .password(credentialDto.getPassword())
                .roleBasedAuthority(credentialDto.getRoleBasedAuthority())
                .isEnabled(credentialDto.getIsEnabled())
                .isAccountNonExpired(credentialDto.getIsAccountNonExpired())
                .isAccountNonLocked(credentialDto.getIsAccountNonLocked())
                .isCredentialsNonExpired(credentialDto.getIsCredentialNonExpired())
                .user(
                        User.builder()
                                .userId(credentialDto.getUserDTO().getUserId())
                                .fullName(credentialDto.getUserDTO().getFullName())
                                .sex(credentialDto.getUserDTO().getSex())
                                .imageUrl(credentialDto.getUserDTO().getImageUrl())
                                .email(credentialDto.getUserDTO().getEmail())
                                .phone(credentialDto.getUserDTO().getPhone())
                                .adress(credentialDto.getUserDTO().getAdress())
                                .birthday(credentialDto.getUserDTO().getBirthday())
                                .build())
                .build();
    }
}

