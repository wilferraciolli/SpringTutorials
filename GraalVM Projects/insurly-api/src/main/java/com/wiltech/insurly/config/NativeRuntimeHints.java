package com.wiltech.insurly.config;

import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.BaseResponse;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import com.wiltech.insurly.libraries.rest.LinkDetails;
import com.wiltech.insurly.libraries.rest.Metadata;
import com.wiltech.insurly.libraries.rest.MetadataEmnbedded;
import com.wiltech.insurly.libraries.rest.ResponseSerializer;
import jakarta.validation.ConstraintValidator;
import java.util.List;
import java.util.UUID;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

/**
 * Reflection hints the GraalVM native image needs but Spring/Hibernate/Flyway/
 * Jackson AOT does not infer on its own. Only relevant to the {@code -Pnative}
 * build.
 *
 * <ul>
 * <li><b>REST response serialization</b> — every endpoint wraps its DTO in a
 *     {@link BaseResponse} envelope (see {@code BaseRestService.buildResponseOk}),
 *     and the controllers return a <em>raw</em> {@code ResponseEntity}, so Spring
 *     AOT never sees {@code BaseResponse}, {@code PayloadData} or the concrete
 *     {@code *Resource} types. Unregistered, Jackson can't introspect them in the
 *     native image and every response serializes as {@code "{}"}. Fixed by
 *     registering the envelope types plus every {@link BaseDTO} subclass (found
 *     by classpath scan) for binding reflection.
 * <li><b>{@code UUID[]}</b> — Hibernate reflectively instantiates it for every
 *     UUID-keyed entity's multi-id loader ({@code MissingReflectionRegistrationError}
 *     on boot without it).
 * <li><b>Flyway {@code sqlExceptions.*}</b> — Flyway classifies a caught
 *     {@code SQLException} by reflectively calling {@code isFlywaySpecificVersionOf}
 *     on them; unregistered, a real DB error surfaces as a confusing
 *     {@code NoSuchMethodException}.
 * <li><b>pgjdbc SSL factories</b> — instantiated by name; Supabase requires TLS.
 * <li><b>Custom {@link ConstraintValidator}s</b> — {@code @ValidZoneId} etc. are
 *     not Spring beans, so Hibernate Validator instantiates them reflectively via
 *     their implicit no-arg constructor. Unregistered, the first {@code @Valid}
 *     request body that uses one dies with {@code NoSuchMethodException: <init>()}
 *     and 500s.
 * </ul>
 */
public class NativeRuntimeHints implements RuntimeHintsRegistrar {

    private static final String BASE_PACKAGE = "com.wiltech.insurly";

    private static final List<String> FLYWAY_SQL_EXCEPTIONS = List.of(
            "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlUnableToConnectToDbException",
            "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlNoIntegratedAuthException",
            "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlServerUntrustedCertificateSqlException",
            "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlNoDriversForInteractiveAuthException");

    private static final List<String> PGJDBC_SSL_FACTORIES = List.of(
            "org.postgresql.ssl.DefaultJavaSSLFactory",
            "org.postgresql.ssl.LibPQFactory",
            "org.postgresql.ssl.NonValidatingFactory");

    private final BindingReflectionHintsRegistrar bindingRegistrar = new BindingReflectionHintsRegistrar();

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerRestSerialization(hints, classLoader);
        registerConstraintValidators(hints, classLoader);

        hints.reflection().registerType(UUID[].class);

        FLYWAY_SQL_EXCEPTIONS.forEach(type -> hints.reflection().registerType(
                TypeReference.of(type), MemberCategory.INVOKE_PUBLIC_METHODS));

        PGJDBC_SSL_FACTORIES.forEach(type -> hints.reflection().registerType(
                TypeReference.of(type), MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS));
    }

    /**
     * Every custom {@link ConstraintValidator} in the base package, registered
     * for constructor reflection so Hibernate Validator can {@code newInstance()}
     * it in the native image (they are not Spring beans, so AOT never sees them).
     */
    private void registerConstraintValidators(RuntimeHints hints, ClassLoader classLoader) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(ConstraintValidator.class));
        scanner.findCandidateComponents(BASE_PACKAGE).forEach(candidate -> {
            try {
                Class<?> validator = ClassUtils.forName(candidate.getBeanClassName(), classLoader);
                hints.reflection().registerType(validator, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException("ConstraintValidator scan found an unloadable class", ex);
            }
        });
    }

    private void registerRestSerialization(RuntimeHints hints, ClassLoader classLoader) {
        // The response envelope. PayloadData is package-private -> register by name.
        bindingRegistrar.registerReflectionHints(hints.reflection(),
                BaseResponse.class, BaseDTO.class, Metadata.class, MetadataEmnbedded.class, LinkDetails.class);
        hints.reflection().registerType(
                TypeReference.of("com.wiltech.insurly.libraries.rest.PayloadData"),
                MemberCategory.INVOKE_PUBLIC_METHODS);

        // Custom Jackson serializers referenced via @JsonSerialize(using = ...).
        hints.reflection().registerType(ResponseSerializer.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerType(CustomNullSerializer.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        // Every concrete DTO (BaseDTO subclass) that flows through PayloadData.data
        // (declared Object, so the graph walker can't reach it on its own).
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(BaseDTO.class));
        scanner.findCandidateComponents(BASE_PACKAGE).forEach(candidate -> {
            try {
                Class<?> dto = ClassUtils.forName(candidate.getBeanClassName(), classLoader);
                bindingRegistrar.registerReflectionHints(hints.reflection(), dto);
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException("BaseDTO scan found an unloadable class", ex);
            }
        });
    }
}
