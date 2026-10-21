package com.shakeup.backend.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shakeup.backend.service.EleveService;
import com.shakeup.backend.web.dto.CreateEleveRequest;
import com.shakeup.backend.web.dto.EleveResponse;
import com.shakeup.backend.web.dto.UpdateEleveRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/coach/eleves")
@PreAuthorize("hasRole('COACH')")
public class CoachEleveController {

	private final EleveService eleveService;

	public CoachEleveController(EleveService eleveService) {
		this.eleveService = eleveService;
	}

	@GetMapping
	public List<EleveResponse> list() {
		return eleveService.list();
	}

	@GetMapping("/{id}")
	public EleveResponse get(@PathVariable Long id) {
		return eleveService.get(id);
	}

	@PostMapping
	public EleveResponse create(@Valid @RequestBody CreateEleveRequest request) {
		return eleveService.create(request);
	}

	@PatchMapping("/{id}")
	public EleveResponse update(@PathVariable Long id, @Valid @RequestBody UpdateEleveRequest request) {
		return eleveService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		eleveService.delete(id);
	}
}
