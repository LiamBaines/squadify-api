package com.squadify.app.core;

import com.squadify.app.squad.Squad;
import com.squadify.app.user.SquadifyUser;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class SquadifyUtils {

    public static void printRepresentationPercentages(Squad squad, Map<SquadifyUser, Set<String>> userTracks, String[] commonTracks) {
        System.out.printf("\n%s - %s tracks\n", squad.getName(), commonTracks.length);
        userTracks.entrySet().forEach(entry -> System.out.printf("%s accounts for %s%% of tracks\n", entry.getKey().getDisplayName(), getRepresentationPercentage(entry, commonTracks)));
    }

    public long getRepresentationPercentage(Map.Entry<SquadifyUser, Set<String>> userTracks, String[] commonTracks) {
        long includedTracks = userTracks.getValue().stream()
                .filter(track -> Arrays.asList(commonTracks).contains(track))
                .count();
        return (100 * includedTracks) / commonTracks.length;
    }

    public static void writeTracksToFile(String username, Set<String> tracks) {
        try {
            String s = String.join("\n", tracks);
            Files.write(Path.of("src/test/resources/trackprofiles/" + username), s.getBytes());
        } catch (IOException e) {
            System.err.println(e);
        }

    }

}
