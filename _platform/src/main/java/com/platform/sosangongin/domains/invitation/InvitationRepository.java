package com.platform.sosangongin.domains.invitation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query("SELECT i FROM Invitation i JOIN FETCH i.invitationRoles WHERE i.id = :id")
    Optional<Invitation> findByIdWithRoles(@Param("id") Long id);

    @Query("SELECT i FROM Invitation i JOIN FETCH i.invitationRoles WHERE i.invitationCode = :code")
    Optional<Invitation> findByInvitationCodeWithRoles(@Param("code") String invitationCode);
}
