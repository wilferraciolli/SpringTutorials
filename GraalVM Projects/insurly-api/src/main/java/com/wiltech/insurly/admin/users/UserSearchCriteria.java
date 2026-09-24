package com.wiltech.insurly.admin.users;

import com.wiltech.insurly.account.identity.UserOwnership;

/**
 * Filters for the admin customer search ({@code GET /api/admin/users}). Every
 * field is optional; nulls mean "no constraint".
 *
 * @param query     free text matched against name and email (case-insensitive, contains)
 * @param ownership  MANAGED or SELF_SERVICE
 * @param filter    a canned segment (renewal due, open quotes, …)
 * @param sort      result ordering
 */
public record UserSearchCriteria(String query, UserOwnership ownership, UserFilter filter, UserSort sort) {

    public UserSearchCriteria {
        filter = filter == null ? UserFilter.ALL : filter;
        sort = sort == null ? UserSort.RECENT : sort;
    }

    /** Canned customer segments an admin cares about. */
    public enum UserFilter {
        ALL,
        /** Has a completed quote expiring in the next 30 days. */
        RENEWAL_DUE,
        /** Has at least one completed quote still inside its validity window. */
        HAS_OPEN_QUOTES,
        /** No quotes at all. */
        NO_QUOTES,
        /** Owns a car of a premium marque (rough proxy for "expensive car"). */
        PREMIUM_VEHICLE
    }

    public enum UserSort {
        /** Newest customer first (default). */
        RECENT,
        /** A→Z by name. */
        NAME,
        /** Soonest quote expiry first; customers with no live quote last. */
        RENEWAL_SOONEST
    }
}
