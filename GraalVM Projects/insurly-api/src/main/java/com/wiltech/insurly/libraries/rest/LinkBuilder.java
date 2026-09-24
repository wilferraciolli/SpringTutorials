package com.wiltech.insurly.libraries.rest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to build links.
 */
@Component
public class LinkBuilder {
    @Value("${server.servlet.context-path}")
    private String serverContextPath;

    public LinkBuilder() {
        // Private constructor to force the use of the builder methods
    }

    public Link buildLink(
            Class<? extends BaseRestService> controllerClass,
            String methodName,
            String linkName
    ) {
        return buildLink(controllerClass, methodName, linkName, Map.of(), Map.of());
    }

    public Link buildLink(
            Class<? extends BaseRestService> controllerClass,
            String methodName,
            String linkName,
            Map<String, Object> pathParams
    ) {
        return buildLink(controllerClass, methodName, linkName, pathParams, Map.of());
    }

    public Link buildSelfLink(
            Class<? extends BaseRestService> controllerClass,
            String methodName
    ) {
        return buildLink(controllerClass, methodName, IanaLinkRelations.SELF.value(), Map.of(), Map.of());
    }

    public Link buildSelfLink(
            Class<? extends BaseRestService> controllerClass,
            String methodName,
            Map<String, Object> pathParams
    ) {
        return buildLink(controllerClass, methodName, IanaLinkRelations.SELF.value(), pathParams, Map.of());
    }

    public Link buildSelfLink(
            Class<? extends BaseRestService> controllerClass,
            String methodName,
            Map<String, Object> pathParams,
            Map<String, Object> queryParams
    ) {
        return buildLink(controllerClass, methodName, IanaLinkRelations.SELF.value(), pathParams, queryParams);
    }

    public Link buildLink(
            Class<? extends BaseRestService> controllerClass,
            String methodName,
            String linkName,
            Map<String, Object> pathParams,
            Map<String, Object> queryParams
    ) {
        if (Objects.isNull(controllerClass)
                || Objects.isNull(methodName)
                || Objects.isNull(linkName)) {
            System.out.println("Controller class or method name cannot be null");
            return null;
        }

        String path = getPathFromRestController(controllerClass, methodName);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(path);

        if (Objects.nonNull(queryParams)) {
            queryParams.forEach(uriBuilder::queryParam);
        }

        return Link.of(uriBuilder.build().toString())
                .withRel(linkName)
                .expand(Objects.isNull(pathParams) ? Map.of() : pathParams);
    }

    private <T extends BaseRestService> String getPathFromRestController(Class<T> controllerClass, String methodName) {
        StringBuilder urlFromRestService = new StringBuilder();

        if (StringUtils.isNoneBlank(serverContextPath)) {
            urlFromRestService.append(serverContextPath)
                    .append("/");
        }

        // get the Request Mapping annotation value from the REST service
        assignRequestMappingFromClassLevel(urlFromRestService, controllerClass);

        // Work out the HTTP method and get the xMapping value and append to the path
        Method[] methods = controllerClass.getMethods();
        Arrays.stream(methods)
                .filter(Objects::nonNull)
                .filter(method -> method.getName().equals(methodName))
                .forEach(method -> {
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        getCrudMappingValueFromMethod(urlFromRestService, method, GetMapping.class);
                    } else if (method.isAnnotationPresent(PostMapping.class)) {
                        getCrudMappingValueFromMethod(urlFromRestService, method, PostMapping.class);
                    } else if (method.isAnnotationPresent(PutMapping.class)) {
                        getCrudMappingValueFromMethod(urlFromRestService, method, PutMapping.class);
                    } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                        getCrudMappingValueFromMethod(urlFromRestService, method, DeleteMapping.class);
                    }
                });

        return urlFromRestService.toString();
    }

    private <T extends BaseRestService> void assignRequestMappingFromClassLevel(StringBuilder urlFromRestService, Class<T> controllerClass) {
        RequestMapping annotation = controllerClass.getAnnotation(RequestMapping.class);

        if (Objects.nonNull(annotation)) {
            String[] values = annotation.value();
            if (values.length > 0) {
                urlFromRestService.append(values[0]);
            }
        }
    }

    private void getCrudMappingValueFromMethod(StringBuilder urlFromRestService, Method method, Class<? extends Annotation> crudMapping) {
        Annotation annotation = method.getAnnotation(crudMapping);
        String[] values = new String[0];

        if (annotation != null) {
            if (annotation instanceof GetMapping) {
                values = ((GetMapping) annotation).value();
            } else if (annotation instanceof PutMapping) {
                values = ((PutMapping) annotation).value();
            } else if (annotation instanceof PostMapping) {
                values = ((PostMapping) annotation).value();
            } else if (annotation instanceof DeleteMapping) {
                values = ((DeleteMapping) annotation).value();
            }

            if (values.length > 0) {
                urlFromRestService.append(values[0]);
            }
        }
    }
}
