package espresso.challenge.domain.queries;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import espresso.common.domain.queries.CommonQuery;
import espresso.common.domain.queries.QuerySizeType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Query for retrieving challenges for a specific user.
 * Can filter by date and limit the number of results returned.
 */
@Getter
@Setter
@AllArgsConstructor
public class GetUserChallengesQuery extends CommonQuery {

    /**
     * The 7-character alphanumeric key of the authenticated user making the request.
     * This value is obtained from the JWT token and not from the request body.
     */
    @JsonIgnore
    @Schema(hidden = true)
    @NotBlank(message = "LOCALIZE: A USER KEY MUST BE PROVIDED")
    @Size(min = 7, max = 7, message = "LOCALIZE: ENTITY KEY MUST BE EXACTLY 7 CHARACTERS")
    private String userKey;

    /**
     * The size type indicating how much detail to include in the response (SM, MD, LG).
     */
    @NotNull(message = "LOCALIZE: DTO SIZE MUST NOT BE NULL")
    private QuerySizeType size;

    /**
     * The 7-character alphanumeric key of the user whose challenges to retrieve.
     */
    @NotNull(message = "LOCALIZE: USER KEY MUST NOT BE NULL")
    @Size(min = 7, max = 7, message = "LOCALIZE: USER KEY MUST BE 7 CHARACTERS")
    @Setter
    private String requestedUserKey;

    /**
     * Optional filter to get challenges from this date onwards.
     * If null, no date filtering is applied.
     */
    private OffsetDateTime fromDate;

    /**
     * The maximum number of challenges to return.
     * Defaults to 10 if not specified.
     */
    private Integer limit;

    /**
     * Performs custom validation specific to this query type.
     * Validates that fromDate, if provided, is not in the future.
     * 
     * @return Set of validation error messages, empty if valid
     */
    @Override
    public Set<String> validateCustom() {
        Set<String> errors = new HashSet<>();

        // Verify that fromDate is not null and is not after now (UTC)
        if (fromDate != null && fromDate.isAfter(OffsetDateTime.now(ZoneOffset.UTC))) {
            errors.add("fromDate must not be in the future");
        }

        return errors;
    }
}
