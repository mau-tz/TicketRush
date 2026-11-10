package com.mautz.ticketrush.ticketrush.auth.application.port.in;

import java.util.Optional;
import java.util.UUID;

/** Read-only API other modules may use to look users up. */
public interface UserQueryApi {

    Optional<String> findEmailById(UUID userId);
}
