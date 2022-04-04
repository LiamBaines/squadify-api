package com.squadify.app.squad;

import com.squadify.app.playlist.Playlist;
import com.squadify.app.user.SquadifyUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeExclude;
import org.apache.commons.lang3.builder.ToStringExclude;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Squad {

    @Id
    private String squadId;

    @NotBlank(message = "Squad name can''t be blank")
    @Size(max = 100, message = "Squad name can''t exceed 100 characters")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private final Set<SquadifyUser> members = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private final Set<SquadifyUser> requests = new LinkedHashSet<>();

    @ManyToOne
    @HashCodeExclude
    @ToStringExclude
    private SquadifyUser owner;

    private Playlist playlist;

    public Set<SquadifyUser> getAllUsers() {
        Set<SquadifyUser> users = new LinkedHashSet<>();
        users.add(owner);
        users.addAll(members);
        return users;
    }

    @Deprecated
    public void acceptMember(SquadifyUser member) {
        members.add(member);
        requests.remove(member);
    }

    public void addMember(SquadifyUser member) {
        members.add(member);
    }

    public void removeRequest(SquadifyUser member) {
        members.remove(member);
    }

    @Deprecated
    public void declineMember(SquadifyUser member) {
        requests.remove(member);
    }

    @Deprecated
    public void addMemberRequest(SquadifyUser member) {
        if (!members.contains(member)) {
            requests.add(member);
        }
    }

    public void addRequest(SquadifyUser squadifyUser) {
        requests.add(squadifyUser);
    }

    public void removeMember(SquadifyUser member) {
        members.remove(member);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, squadId);
    }

}
