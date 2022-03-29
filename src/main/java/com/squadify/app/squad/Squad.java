package com.squadify.app.squad;

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
    @GeneratedValue
    private int id;

    private String squadKey;

    @NotBlank(message = "Squad name can''t be blank")
    @Size(max = 100, message = "Squad name can''t exceed 100 characters")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private final Set<SquadifyUser> members = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private final Set<SquadifyUser> memberRequests = new LinkedHashSet<>();

    @ManyToOne
    @HashCodeExclude
    @ToStringExclude
    private SquadifyUser owner;

    private String playlistUrl;

    public Set<SquadifyUser> getAllUsers() {
        Set<SquadifyUser> users = new LinkedHashSet<>();
        users.add(owner);
        users.addAll(members);
        return users;
    }

    public void acceptMember(SquadifyUser member) {
        members.add(member);
        memberRequests.remove(member);
    }

    public void declineMember(SquadifyUser member) {
        memberRequests.remove(member);
    }

    public void addMemberRequest(SquadifyUser member) {
        if (!members.contains(member)) {
            memberRequests.add(member);
        }
    }

    public void removeMember(SquadifyUser member) {
        members.remove(member);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, squadKey);
    }

}
