package com.gfg.userservice.service;

import com.gfg.userservice.domain.dto.CredentialDTO;

import java.util.List;

public interface CredentialService {

    List<CredentialDTO> findAll();
    CredentialDTO findById(final Integer credentialId);
    CredentialDTO save(final CredentialDTO credentialDto);
    CredentialDTO update(final CredentialDTO credentialDto);
    CredentialDTO update(final Integer credentialId, final CredentialDTO credentialDto);
    void deleteById(final Integer credentialId);
    CredentialDTO findByUsername(final String username);

    void lockUserAccount(Integer credentialId);
    void unlockUserAccount(Integer credentialId);
}
