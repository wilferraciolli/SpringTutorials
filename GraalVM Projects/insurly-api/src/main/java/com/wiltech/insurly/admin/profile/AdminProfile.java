package com.wiltech.insurly.admin.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * In-memory placeholder — no persistence yet. A single dummy admin user
 * lives for the lifetime of the application ({@link AdminProfileAppService}
 * holds the only instance), so a PUT is visible on the next GET even though
 * nothing is written to a database.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AdminProfile {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    public void updateValues(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
