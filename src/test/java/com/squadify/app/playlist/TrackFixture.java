package com.squadify.app.playlist;

import com.squadify.app.user.SquadifyUser;
import com.squadify.app.user.SquadifyUserTextFixture;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.squadify.app.core.SquadifyTestFixture.someString;
import static com.squadify.app.user.SquadifyUserTextFixture.someSquadifyUser;
import static java.util.stream.Collectors.toMap;

@UtilityClass
public class TrackFixture {

    public static Set<String> someTracks() {
        return someTracks(0, 20);
    }

    public static Set<String> someTracks(int start, int end) {
        Set<String> tracks = new LinkedHashSet<>();
        IntStream.range(start, end)
                .mapToObj(Integer::toString)
                .forEach(tracks::add);
        return tracks;
    }

    public static Map<SquadifyUser, Set<String>> someTracksMap() {
        return IntStream.range(0, 5)
                .mapToObj(i -> someTracks(0, 25 + i))
                .collect(toMap(tracks -> someSquadifyUser(), tracks -> tracks));
    }

    public static Map<SquadifyUser, Set<String>> someTracksMap(List<String> usernames) {
        return usernames.stream()
                .collect(toMap(SquadifyUserTextFixture::someSquadifyUser, TrackFixture::readTrackProfileFromFile));
    }

    private static Set<String> readTrackProfileFromFile(String username) {
        try {
            return new LinkedHashSet<>(Files.readAllLines(Path.of("src/test/resources/trackprofiles/" + username)));
        } catch (IOException e) {
            System.err.println(e);
            return new LinkedHashSet<>();
        }
    }

}
