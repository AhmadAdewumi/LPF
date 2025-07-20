package com.ahmad.ProductFinder.controller.swaggerDocs;

import com.ahmad.ProductFinder.dtos.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import java.util.Collection;

@SecurityRequirement(name = "bearerAuth")
public interface TagDocs {

    @Hidden
    @Operation(
            summary = "Bulk create or fetch tags",
            description = "Given a list of tag names, fetches existing tags and creates any that are missing.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A JSON array of tag names",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = String[].class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Returns the full list of Tag objects (existing + newly created)",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseBody.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<ApiResponseBody> findOrCreateTags(@Parameter(description = "Tag names", required = true) Collection<String> names);

//    @Hidden
    @Operation(
            summary = "List all tags",
            description = "Returns every tag in the system, for front‑end autocomplete or display.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of all tags",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponseBody.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    ResponseEntity<ApiResponseBody> listAllTags();
}
