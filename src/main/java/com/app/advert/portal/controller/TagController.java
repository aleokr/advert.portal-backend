package com.app.advert.portal.controller;

import com.app.advert.portal.dto.ResourceTagRequestDto;
import com.app.advert.portal.dto.TagRequestDto;
import com.app.advert.portal.dto.TagResponse;
import com.app.advert.portal.model.Tag;
import com.app.advert.portal.security.SecurityUtils;
import com.app.advert.portal.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Slf4j
public class TagController {

    private final TagService tagService;

    @Operation(tags = {"Tag"}, description = "Add tag")
    @PostMapping("/addTag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dane poprawnie przetworzone", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Tag.class))}),
            @ApiResponse(responseCode = "422", description = "Błąd przetwarzania danych", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Błąd generowania odpowiedzi", content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<?> addTag(@Validated @RequestBody TagRequestDto requestDto) {
        try {
            log.info("TagController: Save new advert");
            if (requestDto.getName() == null) {
                return ResponseEntity.unprocessableEntity().body("No name ");
            }
            Tag tag = tagService.saveTag(requestDto.getName(), SecurityUtils.getLoggedCompanyId(), SecurityUtils.getLoggedUserId());
            if (tag == null) {
                return ResponseEntity.unprocessableEntity().body("Tag has already exists ");
            }
            return ResponseEntity.ok().body(tag);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(tags = {"Tag"}, description = "Add resource tag")
    @PostMapping("/addResourceTag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dane poprawnie przetworzone", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Błąd generowania odpowiedzi", content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<?> addResourceTag(@Validated @RequestBody ResourceTagRequestDto requestDto) {
        try {
            log.info("TagController: Add resource tag");
            tagService.saveResourceTag(requestDto, SecurityUtils.getLoggedCompanyId(), SecurityUtils.getLoggedUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(tags = {"Tag"}, description = "Return tags list")
    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dane poprawnie przetworzone", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Błąd generowania odpowiedzi", content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<?> getTagsList(
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit
    ) {
        try {
            log.info("TagController: Return tags list");
            return ResponseEntity.ok().body(tagService.getTagsList(limit, offset));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }

    @Operation(tags = {"Tag"}, description = "Return available tags list")
    @GetMapping("/availableTags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dane poprawnie przetworzone", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TagResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Błąd generowania odpowiedzi", content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<?> getAvailableTagList() {
        try {
            log.info("TagController: Return available tags list");
            return ResponseEntity.ok().body(tagService.getAvailableTagsList(SecurityUtils.getLoggedCompanyId(), SecurityUtils.getLoggedUserId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }

    }
}
