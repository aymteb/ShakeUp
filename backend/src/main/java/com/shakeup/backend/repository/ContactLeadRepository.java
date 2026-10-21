package com.shakeup.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shakeup.backend.domain.ContactLead;

public interface ContactLeadRepository extends JpaRepository<ContactLead, Long> {

	List<ContactLead> findAllByOrderByCreatedAtDesc();
}
