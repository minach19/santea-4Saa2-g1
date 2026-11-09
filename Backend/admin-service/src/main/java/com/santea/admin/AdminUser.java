package com.santea.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.santea.shared.model.UserAccount;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@Getter
@Setter
@NoArgsConstructor
public class AdminUser extends UserAccount {
    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
}
