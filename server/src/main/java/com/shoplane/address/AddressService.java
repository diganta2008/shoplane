package com.shoplane.address;

import com.shoplane.address.dto.AddressDto;
import com.shoplane.address.dto.SaveAddressRequest;
import com.shoplane.common.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    private final AddressRepository repo;

    public AddressService(AddressRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<AddressDto> list(Long userId) {
        return repo.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream().map(AddressDto::from).toList();
    }

    @Transactional(readOnly = true)
    public AddressDto get(Long userId, Long addressId) {
        return repo.findByIdAndUserId(addressId, userId)
                .map(AddressDto::from)
                .orElseThrow(() -> ApiException.notFound("Address not found"));
    }

    /**
     * Create a new address. If it's marked default (or if it's the user's
     * very first address), it becomes the default and all others are cleared.
     */
    @Transactional
    public AddressDto create(Long userId, SaveAddressRequest req) {
        Address a = new Address();
        a.setUserId(userId);
        applyRequest(a, req);

        boolean firstEver = repo.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId).isEmpty();
        boolean wantDefault = Boolean.TRUE.equals(req.isDefault()) || firstEver;
        a.setDefault(wantDefault);
        Address saved = repo.save(a);

        if (wantDefault) repo.clearOtherDefaults(userId, saved.getId());
        log.info("Address {} created for user {} (default={}).", saved.getId(), userId, wantDefault);
        return AddressDto.from(saved);
    }

    @Transactional
    public AddressDto update(Long userId, Long addressId, SaveAddressRequest req) {
        Address a = repo.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> ApiException.notFound("Address not found"));
        applyRequest(a, req);
        if (Boolean.TRUE.equals(req.isDefault())) {
            a.setDefault(true);
            Address saved = repo.save(a);
            repo.clearOtherDefaults(userId, saved.getId());
            return AddressDto.from(saved);
        }
        Address saved = repo.save(a);
        log.info("Address {} updated for user {}.", addressId, userId);
        return AddressDto.from(saved);
    }

    @Transactional
    public void delete(Long userId, Long addressId) {
        Address a = repo.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> ApiException.notFound("Address not found"));
        boolean wasDefault = a.isDefault();
        repo.delete(a);
        if (wasDefault) {
            repo.findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId).stream().findFirst()
                    .ifPresent(next -> { next.setDefault(true); repo.save(next); });
        }
        log.info("Address {} deleted for user {} (was_default={}).", addressId, userId, wasDefault);
    }

    @Transactional
    public AddressDto setDefault(Long userId, Long addressId) {
        Address a = repo.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> ApiException.notFound("Address not found"));
        repo.clearAllDefaults(userId);
        a.setDefault(true);
        Address saved = repo.save(a);
        log.info("Address {} set as default for user {}.", addressId, userId);
        return AddressDto.from(saved);
    }

    private void applyRequest(Address a, SaveAddressRequest r) {
        a.setLabel(r.label());
        a.setFullName(r.fullName());
        a.setPhone(r.phone());
        a.setStreet(r.street());
        a.setCity(r.city());
        a.setState(r.state());
        a.setZip(r.zip());
        a.setCountry(r.country());
    }
}
