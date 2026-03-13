package com.batrobot.shared.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.batrobot.shared.domain.event.DomainEvent;

/**
 * Base class for Aggregate Roots in the domain model.
 * Defines contract for managing domain events.
 */
public abstract class BaseAggregateRoot {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    protected <T> boolean updateField(T newValue, T currentValue, Consumer<T> setter) {
        if (!Objects.equals(newValue, currentValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    protected <T> boolean updateFieldWithEvent(T newValue, T currentValue, Consumer<T> setter, Supplier<DomainEvent> eventSupplier) {
        if (!Objects.equals(newValue, currentValue)) {
            this.registerEvent(eventSupplier.get());
            setter.accept(newValue);
            return true;
        }
        return false;
    }
}