package com.squadify.app.playlist;

import com.squadify.app.user.SquadifyUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.squadify.app.core.SquadifyUtils.getRepresentationPercentage;
import static com.squadify.app.playlist.TrackFixture.someTracksMap;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CommonTrackFinderTest {

    private CommonTrackFinder underTest = new CommonTrackFinder();


    @Test
    void canFindCommonTracks() {
        // given
        Map<SquadifyUser, Set<String>> tracksMap = someTracksMap();

        // when
        String[] commonTracks = underTest.findCommonTracks(tracksMap);

        // then
        assertThat(commonTracks).isNotEmpty();
        assertThat(commonTracks).allMatch(track -> appearsTwice(tracksMap, track));
    }

    @ParameterizedTest
    @MethodSource("usernameLists")
    void canRepresentAllUsers(List<String> usernames) {
        // given
        Map<SquadifyUser, Set<String>> tracksMap = someTracksMap(usernames);
        long minRepresentationPercentage = 100 / usernames.size();

        // when
        String[] commonTracks = underTest.findCommonTracks(tracksMap);

        // then
        assertThat(commonTracks).isNotEmpty();
        tracksMap.entrySet().forEach(userTracks -> {
            long representationPercentage = getRepresentationPercentage(userTracks, commonTracks);
            assertThat(representationPercentage)
                    .withFailMessage("%s accounted for %s%% of songs - minimum is %s", userTracks.getKey().getUsername(), representationPercentage, minRepresentationPercentage)
                    .isGreaterThan(minRepresentationPercentage);
        });
    }

    private boolean appearsTwice(Map<SquadifyUser, Set<String>> tracksMap, String track) {
        long count = tracksMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(track))
                .count();
        return (count >= 2);
    }

    private static Stream<Arguments> usernameLists() {
        return Stream.of(
                Arguments.of(Arrays.asList("liam_baines", "gonemissen", "lgbanter")),
                Arguments.of(Arrays.asList("liam_baines", "ayeishacvaze", "qprindc", "1140601192"))
        );
    }
}