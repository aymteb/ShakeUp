package com.shakeup.backend.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shakeup.backend.domain.ContactLead;
import com.shakeup.backend.repository.ContactLeadRepository;
import com.shakeup.backend.web.dto.ContactRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {

	private final ContactLeadRepository leads;

	public ContactController(ContactLeadRepository leads) {
		this.leads = leads;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void submit(@Valid @RequestBody ContactRequest request) {
		ContactLead lead = new ContactLead();
		lead.setName(request.name().trim());
		lead.setEmail(request.email().trim().toLowerCase());
		lead.setMessage(request.message().trim());
		leads.save(lead);
	}
}
