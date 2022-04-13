package com.squadify.app.playlist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Accessors(chain = true)
public class Playlist {

    @Id
    private int id;

    private String url;

}
