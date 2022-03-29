package com.squadify.app.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface SquadifyUserDao extends CrudRepository<SquadifyUser, Integer> {

    public Optional<SquadifyUser> findByUsername(String name);

    public SquadifyUser findById(int id);

    public boolean existsByUsername(String username);

}
