package org.cedacri.pingpong.utils;

import org.cedacri.pingpong.enums.RoleEnum;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityUtils
{

    private SecurityUtils()
    {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the current Authentication object.
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Checks if a user is logged in.
     */
    public static boolean isUserLoggedIn()
    {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    /**
     * Returns the current UserDetails if available.
     */
    public static UserDetails getCurrentUser()
    {
        Authentication auth = getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails)
        {
            return (UserDetails) auth.getPrincipal();
        }
        return null;
    }

    /**
     * Returns the username of the current user, or "anonymous" if not logged in.
     */
    public static String getUsername()
    {
        UserDetails user = getCurrentUser();
        return (user != null) ? user.getUsername() : "anonymous";
    }

    /**
     * Returns a set of RoleEnum for the current user.
     * Converts the authorities (which might be "ROLE_ADMIN", etc.) into RoleEnum values.
     */
    public static Set<RoleEnum> getUserRoles()
    {
        UserDetails user = getCurrentUser();
        if (user != null)
        {
            return user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    // Remove the "ROLE_" prefix if present and convert to upper case.
                    .map(role -> role.replace("ROLE_", "").toUpperCase())
                    // Convert to the RoleEnum. If a role doesn't match, it will throw an exception.
                    .map(RoleEnum::valueOf)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * Checks if the current user has at least one of the specified RoleEnum values.
     */
    public static boolean hasAnyRole(RoleEnum... allowedRoles)
    {
        Set<RoleEnum> userRoles = getUserRoles();
        for (RoleEnum role : allowedRoles)
        {
            if (userRoles.contains(role))
            {
                return true;
            }
        }
        return false;
    }
}
