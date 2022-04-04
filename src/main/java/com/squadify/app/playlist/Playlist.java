package com.squadify.app.playlist;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Playlist {

    @Id
    private int id;

    private String url;

}
