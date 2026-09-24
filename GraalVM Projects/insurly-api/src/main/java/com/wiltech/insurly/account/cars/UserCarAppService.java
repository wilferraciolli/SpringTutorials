package com.wiltech.insurly.account.cars;

import com.wiltech.insurly.exceptions.ResourceNotFoundException;
import com.wiltech.insurly.quote.PrimaryUse;
import jakarta.validation.Valid;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCarAppService {

    private final UserCarRepository repository;
    private final UserCarAssembler assembler;
    private final Clock clock;

    @Transactional(readOnly = true)
    public UserCarResource createTemplate() {
        return UserCarResource.builder()
                .primaryUse(PrimaryUse.COMMUTE)
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserCarResource> findForUser(final UUID ownerId) {
        return repository.findByUserIdOrderByCreatedAtDesc(ownerId).stream()
                .map(assembler::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserCarResource findForUser(final UUID ownerId, final UUID id) {
        return assembler.toDto(load(ownerId, id));
    }

    @Transactional
    public UserCarResource create(final UUID ownerId, @Valid final UserCarResource payload) {
        final UserCar saved = repository.save(assembler.toEntity(ownerId, payload));
        return assembler.toDto(saved);
    }

    @Transactional
    public UserCarResource update(final UUID ownerId, final UUID id, @Valid final UserCarResource payload) {
        final UserCar car = load(ownerId, id);
        car.updateValues(
                payload.getMake(),
                payload.getModel(),
                payload.getYear(),
                payload.getVin(),
                payload.getPrimaryUse(),
                payload.getNickname(),
                clock.instant());
        return assembler.toDto(repository.save(car));
    }

    @Transactional
    public void delete(final UUID ownerId, final UUID id) {
        repository.delete(load(ownerId, id));
    }

    private UserCar load(final UUID ownerId, final UUID id) {
        return repository.findByIdAndUserId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Car %s not found".formatted(id)));
    }
}
