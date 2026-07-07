package com.academianet.demo.service;

import com.academianet.demo.document.UserActivity;
import com.academianet.demo.document.UserPreferences;
import com.academianet.demo.dto.UserActivityResponse;
import com.academianet.demo.dto.UserPreferencesRequest;
import com.academianet.demo.dto.UserPreferencesResponse;
import com.academianet.demo.exception.NotFoundException;
import com.academianet.demo.repository.UserRepository;
import com.academianet.demo.repository.mongo.UserActivityRepository;
import com.academianet.demo.repository.mongo.UserPreferencesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock UserPreferencesRepository preferencesRepository;
    @Mock UserActivityRepository activityRepository;
    @Mock UserRepository userRepository;
    @InjectMocks UserProfileService service;

    private final UUID userId = UUID.randomUUID();

    @Test
    void getPreferences_returnsDefaultsWhenNoneStored() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(preferencesRepository.findById(userId.toString())).thenReturn(Optional.empty());

        UserPreferencesResponse res = service.getPreferences(userId);

        assertThat(res.userId()).isEqualTo(userId);
        assertThat(res.theme()).isEqualTo("system");
        assertThat(res.language()).isEqualTo("es");
        assertThat(res.emailNotifications()).isTrue();
        assertThat(res.timezone()).isEqualTo("America/Bogota");
    }

    @Test
    void getPreferences_unknownUserThrows() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> service.getPreferences(userId))
                .isInstanceOf(NotFoundException.class);
        verify(preferencesRepository, never()).findById(any());
    }

    @Test
    void updatePreferences_savesAndLogsActivity() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(preferencesRepository.findById(userId.toString())).thenReturn(Optional.empty());
        when(preferencesRepository.save(any(UserPreferences.class))).thenAnswer(inv -> inv.getArgument(0));

        UserPreferencesResponse res = service.updatePreferences(userId,
                new UserPreferencesRequest("dark", "en", false, "UTC"));

        assertThat(res.theme()).isEqualTo("dark");
        assertThat(res.language()).isEqualTo("en");
        assertThat(res.emailNotifications()).isFalse();
        assertThat(res.timezone()).isEqualTo("UTC");
        assertThat(res.updatedAt()).isNotNull();

        ArgumentCaptor<UserActivity> captor = ArgumentCaptor.forClass(UserActivity.class);
        verify(activityRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(UserProfileService.ACTIVITY_PREFERENCES_UPDATE);
        assertThat(captor.getValue().getUserId()).isEqualTo(userId.toString());
    }

    @Test
    void recordActivity_swallowsMongoFailures() {
        when(activityRepository.save(any(UserActivity.class)))
                .thenThrow(new RuntimeException("mongo down"));

        // No debe propagar la excepción (best-effort).
        service.recordActivity(userId, UserProfileService.ACTIVITY_LOGIN, "login");

        verify(activityRepository).save(any(UserActivity.class));
    }

    @Test
    void listActivity_mapsDocuments() {
        when(userRepository.existsById(userId)).thenReturn(true);
        UserActivity activity = new UserActivity();
        activity.setId("abc");
        activity.setUserId(userId.toString());
        activity.setType(UserProfileService.ACTIVITY_LOGIN);
        activity.setDetail("login");
        activity.setTimestamp(Instant.now());
        when(activityRepository.findByUserIdOrderByTimestampDesc(any(), any()))
                .thenReturn(List.of(activity));

        List<UserActivityResponse> res = service.listActivity(userId);

        assertThat(res).hasSize(1);
        assertThat(res.get(0).id()).isEqualTo("abc");
        assertThat(res.get(0).type()).isEqualTo(UserProfileService.ACTIVITY_LOGIN);
    }
}
