package com.squadify.app.squad;


import com.squadify.app.user.SquadifyUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SquadDao extends CrudRepository<Squad, Integer> {

    Optional<Squad> findBySquadId(String squadKey);

    List<Squad> findByOwner(SquadifyUser owner);

    @Query(value = "SELECT u FROM Squad u where u.owner = ?1 or ?1 member of u.members")
    List<Squad> findSquads(SquadifyUser squadifyUser);
}
