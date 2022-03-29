package com.squadify.app.playlist;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.squadify.app.user.SquadifyUser;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommonTrackFinder {

    public String[] findCommonTracks(Map<SquadifyUser, Set<String>> tracksMap) {
        Set<String> commonTracks = new LinkedHashSet<>();
        for (SquadifyUser userA : tracksMap.keySet()) {
            Map<SquadifyUser, Set<String>> everyoneElse = Maps.filterKeys(tracksMap, member -> !userA.equals(member));
            for (SquadifyUser userB : everyoneElse.keySet()) {
                Set<String> userATracks = tracksMap.get(userA);
                Set<String> userBTracks = tracksMap.get(userB);
                Set<String> intersection = Sets.intersection(userATracks, userBTracks);
                commonTracks.addAll(intersection);
            }
        }

        return setToShuffledArray(commonTracks);
    }


    private static String[] setToShuffledArray(Set<String> set) {
        List<String> list = new ArrayList<>(set);
        Collections.shuffle(list);
        list = list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return list.toArray(String[]::new);
    }

}
