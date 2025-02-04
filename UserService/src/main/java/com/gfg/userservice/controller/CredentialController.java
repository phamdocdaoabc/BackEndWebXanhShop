package com.gfg.userservice.controller;

import com.gfg.userservice.domain.dto.CredentialDTO;
import com.gfg.userservice.response.ResponseCollectionDTO;
import com.gfg.userservice.service.CredentialService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    @GetMapping
    public ResponseEntity<ResponseCollectionDTO<CredentialDTO>>findAll() {
        log.info("CredentialController, find all the Credentials");
        return ResponseEntity.ok(new ResponseCollectionDTO<>(this.credentialService.findAll()));
    }

    @GetMapping("/{credentialId}")
    public ResponseEntity<CredentialDTO> findById(
            @PathVariable("credentialId")
            @NotBlank(message = "Input must not be blank")
            @Valid final String credentialId
    ) {
        log.info("CredentialController, fetch all the credentials by the Id");
        return ResponseEntity.ok(this.credentialService.findById(Integer.parseInt(credentialId.strip())));
    }

    @PostMapping
    public ResponseEntity<CredentialDTO> save(
            @RequestBody
            @NotNull(message = "input must not be blank")
            @Valid final CredentialDTO credentialDTO
    ) {
        log.info("Save the credentials");
        return ResponseEntity.ok(this.credentialService.save(credentialDTO));
    }

    @PutMapping
    public ResponseEntity<CredentialDTO> update(
            @RequestBody
            @NotNull(message = "Input must not NULL")
            @Valid final CredentialDTO credentialDto) {
        log.info("*** CredentialDto, resource; update credential *");
        return ResponseEntity.ok(this.credentialService.update(credentialDto));
    }

    @PutMapping("/{credentialId}")
    public ResponseEntity<CredentialDTO> update(
            @PathVariable("credentialId")
            @NotBlank(message = "Input must not blank") final String credentialId,
            @RequestBody
            @NotNull(message = "Input must not NULL")
            @Valid final CredentialDTO credentialDto) {
        log.info("*** CredentialDto, resource; update credential with credentialId *");
        return ResponseEntity.ok(this.credentialService.update(Integer.parseInt(credentialId.strip()), credentialDto));
    }

    @DeleteMapping("/{credentialId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable("credentialId")
            @NotBlank(message = "Input must not blank")
            @Valid final String credentialId) {
        log.info("*** Boolean, resource; delete credential by id *");
        this.credentialService.deleteById(Integer.parseInt(credentialId));
        return ResponseEntity.ok(true);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<CredentialDTO> findByUsername(
            @PathVariable("username")
            @NotBlank(message = "Input must not blank")
            @Valid final String username) {
        log.info("*** CredentialDto, resource; update credential with credentialId *");
        return ResponseEntity.ok(this.credentialService.findByUsername(username));
    }

    // API khóa tài khoản
    @PostMapping("/lock/{credentialId}")
    public ResponseEntity<String> lockUser(@PathVariable Integer credentialId) {
        credentialService.lockUserAccount(credentialId);
        return ResponseEntity.ok("Tài khoản đã bị khóa thành công!");
    }

    // API mở khóa tài khoản
    @PostMapping("/unlock/{credentialId}")
    public ResponseEntity<String> unlockUser(@PathVariable Integer credentialId) {
        credentialService.unlockUserAccount(credentialId);
        return ResponseEntity.ok("Tài khoản đã được mở khóa thành công!");
    }

}
