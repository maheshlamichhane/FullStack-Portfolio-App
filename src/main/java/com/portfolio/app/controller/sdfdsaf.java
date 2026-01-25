//package com.portfolio.app.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/settings")
//@RequiredArgsConstructor
//@Tag(name = "Settings", description = "Application settings management APIs")
//@Slf4j
//public class SettingsController {
//
//    private final SettingsService settingsService;
//
//    @GetMapping("/{profileId}")
//    @Operation(summary = "Get all settings for a profile")
//    public ResponseEntity<Map<String, String>> getSettings(@PathVariable Long profileId) {
//        Map<String, String> settings = settingsService.getAllSettings(profileId);
//        return ResponseEntity.ok(settings);
//    }
//
//    @GetMapping("/{profileId}/{key}")
//    @Operation(summary = "Get specific setting")
//    public ResponseEntity<Map<String, String>> getSetting(
//            @PathVariable Long profileId,
//            @PathVariable String key) {
//        String value = settingsService.getSetting(profileId, key);
//
//        Map<String, String> response = Map.of("key", key, "value", value);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/{profileId}")
//    @Operation(summary = "Update multiple settings")
//    public ResponseEntity<Map<String, String>> updateSettings(
//            @PathVariable Long profileId,
//            @Valid @RequestBody SettingsDTO settingsDTO) {
//        Map<String, String> updatedSettings = settingsService.updateSettings(profileId, settingsDTO);
//        return ResponseEntity.ok(updatedSettings);
//    }
//
//    @PutMapping("/{profileId}/{key}")
//    @Operation(summary = "Update specific setting")
//    public ResponseEntity<Map<String, String>> updateSetting(
//            @PathVariable Long profileId,
//            @PathVariable String key,
//            @RequestBody Map<String, String> request) {
//
//        String value = request.get("value");
//        settingsService.updateSetting(profileId, key, value);
//
//        Map<String, String> response = Map.of(
//                "key", key,
//                "value", value,
//                "message", "Setting updated successfully"
//        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{profileId}/{key}")
//    @Operation(summary = "Delete setting")
//    public ResponseEntity<Map<String, String>> deleteSetting(
//            @PathVariable Long profileId,
//            @PathVariable String key) {
//
//        settingsService.deleteSetting(profileId, key);
//
//        Map<String, String> response = Map.of(
//                "message", "Setting deleted successfully",
//                "key", key
//        );
//
//        return ResponseEntity.ok(response);
//    }
//}
