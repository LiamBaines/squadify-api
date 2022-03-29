package com.squadify.app.user;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
public class SquadifyUser implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    private String displayName, accessToken, refreshToken;

    @Column(unique = true)
    private String username;

    public String getFirstName() {
        return displayName.split(" ")[0];
    }

    @Override
    public String toString() {
        return String.format("%s - %s", displayName, username);
    }

    @Override
    public boolean equals(Object object) {
        if (!object.getClass().equals(SquadifyUser.class)) {
            return false;
        }
        SquadifyUser user = (SquadifyUser) object;
        return user.getUsername().equals(this.getUsername());
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

}
