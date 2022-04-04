package com.squadify.app.user;

import lombok.*;

import javax.persistence.Entity;
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
    private String username;

    private String name, accessToken, refreshToken;

    public String getFirstName() {
        return name.split(" ")[0];
    }

    @Override
    public String toString() {
        return String.format("%s - %s", name, username);
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
