package com.wiltech.insurly.config;

import com.wiltech.insurly.validations.ValidEnumValidator;
import com.wiltech.insurly.validations.ValidZoneIdValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

import static org.assertj.core.api.Assertions.assertThat;

class NativeRuntimeHintsTest {

    private final RuntimeHints hints = new RuntimeHints();

    @BeforeEach
    void register() {
        new NativeRuntimeHints().registerHints(hints, getClass().getClassLoader());
    }

    @Test
    void registersCustomConstraintValidatorsForConstructorReflection() {
        // Hibernate Validator instantiates these reflectively in the native image;
        // without the hint the first @Valid body using one 500s with NoSuchMethodException.
        assertThat(RuntimeHintsPredicates.reflection()
                .onType(ValidZoneIdValidator.class)
                .withMemberCategory(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS))
                .accepts(hints);
        assertThat(RuntimeHintsPredicates.reflection()
                .onType(ValidEnumValidator.class)
                .withMemberCategory(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS))
                .accepts(hints);
    }
}
