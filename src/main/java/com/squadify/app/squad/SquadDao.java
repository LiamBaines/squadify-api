package com.squadify.app.squad;


import com.squadify.app.user.SquadifyUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SquadDao extends CrudRepository<Squad, Integer> {

    Optional<Squad> findBySquadKey(String squadKey);

    List<Squad> findByOwner(SquadifyUser owner);

    List<Squad> findByMembersContains(SquadifyUser member);

    List<Squad> findByMemberRequestsContains(SquadifyUser member);

    void deleteBySquadKey(String squadKey);

    boolean existsBySquadKey(String squadKey);

}
