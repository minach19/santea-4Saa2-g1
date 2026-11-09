package com.santea.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.santea.shared.model.UserAccountPatch;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
@EnableConfigurationProperties(UserDirectoryProperties.class)
public class UserDirectoryService {
    private final RestClient restClient;
    private final UserDirectoryProperties properties;
    private final ObjectMapper objectMapper;

    public UserDirectoryService(
            RestClient.Builder restClientBuilder,
            UserDirectoryProperties properties,
            ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public List<ManagedUserView> list(String token, String typeKey, String query, Boolean active) {
        Stream<ManagedUserType> types = typeKey == null
                ? Stream.of(ManagedUserType.values())
                : Stream.of(ManagedUserType.fromKey(typeKey));
        String normalizedQuery = query == null ? null : query.toLowerCase(Locale.ROOT);
        return types.flatMap(type -> listType(type, token).stream())
                .filter(user -> active == null || user.active() == active)
                .filter(user -> normalizedQuery == null || normalizedQuery.isBlank()
                        || searchableText(user).contains(normalizedQuery))
                .toList();
    }

    public ManagedUserView get(String token, String typeKey, Long id) {
        ManagedUserType type = ManagedUserType.fromKey(typeKey);
        JsonNode profile = call(() -> restClient.get()
                .uri(endpoint(type, "/" + id))
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .body(JsonNode.class));
        return toView(type, requireBody(profile));
    }

    public ManagedUserView create(String token, String typeKey, JsonNode profile) {
        ManagedUserType type = ManagedUserType.fromKey(typeKey);
        JsonNode created = call(() -> restClient.post()
                .uri(endpoint(type, ""))
                .headers(headers -> headers.setBearerAuth(token))
                .body(profile)
                .retrieve()
                .body(JsonNode.class));
        return toView(type, requireBody(created));
    }

    public ManagedUserView updateAccount(String token, String typeKey, Long id, UserAccountPatch patch) {
        ManagedUserType type = ManagedUserType.fromKey(typeKey);
        JsonNode updated = call(() -> restClient.patch()
                .uri(endpoint(type, "/" + id + "/account"))
                .headers(headers -> headers.setBearerAuth(token))
                .body(patch)
                .retrieve()
                .body(JsonNode.class));
        return toView(type, requireBody(updated));
    }

    public void delete(String token, String typeKey, Long id) {
        ManagedUserType type = ManagedUserType.fromKey(typeKey);
        call(() -> {
            restClient.delete()
                    .uri(endpoint(type, "/" + id))
                    .headers(headers -> headers.setBearerAuth(token))
                    .retrieve()
                    .toBodilessEntity();
            return Boolean.TRUE;
        });
    }

    private List<ManagedUserView> listType(ManagedUserType type, String token) {
        JsonNode profiles = call(() -> restClient.get()
                .uri(endpoint(type, ""))
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .body(JsonNode.class));
        if (profiles == null || !profiles.isArray()) {
            throw new ResponseStatusException(BAD_GATEWAY, "Le service de profils a retourné une liste invalide");
        }
        List<ManagedUserView> users = new ArrayList<>();
        profiles.forEach(profile -> users.add(toView(type, profile)));
        return users;
    }

    private ManagedUserView toView(ManagedUserType type, JsonNode profile) {
        JsonNode account = type == ManagedUserType.ADMIN ? profile : profile.path("user");
        if (profile.path("id").isMissingNode() || account.isMissingNode() || account.isNull()) {
            throw new ResponseStatusException(BAD_GATEWAY, "Un service de profils a retourné un compte incomplet");
        }
        long profileId = profile.path("id").asLong();
        JsonNode userIdNode = type == ManagedUserType.ADMIN ? profile.path("id") : profile.path("userId");
        Long userId = userIdNode.isNumber() ? userIdNode.asLong() : null;
        String role = account.path("role").asText(type.role());
        boolean active = !account.path("isBanned").asBoolean(false)
                && (account.path("deletedAt").isMissingNode() || account.path("deletedAt").isNull());

        return new ManagedUserView(
                type.key() + ":" + profileId,
                type.key(),
                profileId,
                userId,
                text(account, "username"),
                text(account, "email"),
                text(account, "nom"),
                text(account, "prenom"),
                role,
                active,
                profile
        );
    }

    private String searchableText(ManagedUserView user) {
        return String.join(" ",
                safe(user.username()),
                safe(user.email()),
                safe(user.nom()),
                safe(user.prenom()),
                safe(user.role()),
                safe(user.profileType())).toLowerCase(Locale.ROOT);
    }

    private String endpoint(ManagedUserType type, String suffix) {
        String serviceUrl = type.serviceUrl(properties);
        if (serviceUrl == null || serviceUrl.isBlank()) {
            throw new IllegalStateException("URL non configurée pour le service " + type.key());
        }
        return serviceUrl.replaceAll("/+$", "") + "/api/v1/" + type.collection() + suffix;
    }

    private <T> T call(Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientResponseException exception) {
            HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
            if (status != null && status.is4xxClientError()) {
                throw new ResponseStatusException(exception.getStatusCode(), "Le service de profils a refusé la requête");
            }
            throw new ResponseStatusException(BAD_GATEWAY, "Un service de profils est en erreur");
        } catch (ResourceAccessException exception) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Un service de profils est indisponible", exception);
        }
    }

    private JsonNode requireBody(JsonNode body) {
        if (body == null || body.isNull()) {
            throw new ResponseStatusException(BAD_GATEWAY, "Le service de profils n'a retourné aucun compte");
        }
        return body;
    }

    private String text(JsonNode account, String field) {
        JsonNode value = account.path(field);
        return value.isTextual() ? value.asText() : null;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
