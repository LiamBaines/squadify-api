package com.squadify.app.playlist;

import com.squadify.app.user.SquadifyUser;
import lombok.Value;

import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

class CompatibilityMap implements Iterator<String> {

    private int addedTracks = 0;
    private static final int MIN_PLAYLIST_SIZE = 200;

    private final List<List<WeightedTrack>> trackLists;
    private int currentUserIndex = 0;

    CompatibilityMap(Map<SquadifyUser, Set<String>> tracksMap) {
        FrequencyMap frequencyMap = new FrequencyMap(tracksMap);
        this.trackLists = tracksMap.values().stream()
                .map(list -> toWeightedTrackList(list, frequencyMap))
                .collect(toList());

    }

    public boolean hasNext() {
        if (addedTracks < MIN_PLAYLIST_SIZE) return true;
        currentUserIndex = (currentUserIndex + 1) % trackLists.size();
        return !trackLists.get(currentUserIndex).isEmpty();
    }

    public String next() {
        try {
            WeightedTrack track = trackLists.get(currentUserIndex).remove(0);
            trackLists.forEach(list -> list.remove(track));
            return track.getId();
        } catch(Exception e) {
            return null;
        }
    }

    private List<WeightedTrack> toWeightedTrackList(Set<String> tracks, FrequencyMap frequencyMap) {
        return tracks.stream()
                .map(track -> new WeightedTrack(track, frequencyMap.get(track)))
                .sorted(comparingInt(WeightedTrack::getScore))
                .collect(toList());
    }

    @Value
    private static class WeightedTrack {
        String id;
        int score;
    }

    private static class FrequencyMap {

        private final Map<String, Integer> frequencies = new LinkedHashMap<>();

        FrequencyMap(Map<SquadifyUser, Set<String>> tracksMap) {
            tracksMap.values().stream()
                    .flatMap(Collection::stream)
                    .forEach(this::add);
        }

        void add(String track) {
            int initialFrequency = frequencies.getOrDefault(track, 0);
            frequencies.put(track, initialFrequency + 1);
        }

        int get(String track) {
            return frequencies.get(track);
        }

    }


}
