package com.squadify.app.playlist;

import com.squadify.app.squad.Squad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface PlaylistDao extends CrudRepository<Playlist, Integer> {
}
