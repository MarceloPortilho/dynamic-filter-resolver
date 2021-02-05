package net.apps.webapptest.controller;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.apps.webapptest.model.Department;
import net.apps.webapptest.repository.NoDeletionSpecification;
import net.dfr.core.annotation.Conjunction;
import net.dfr.core.annotation.Filter;
import net.dfr.core.operator.type.StartsWith;

@RestController
@RequestMapping("department")
public class DepartmentController {

	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public String test(
			@Conjunction(value = {
					@Filter(path = "name", parameters = "name", operator = StartsWith.class) }) Specification<Department> specification)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("name", "Blanka");
		node.put("foundSpec", specification != null);
		return mapper.writeValueAsString(node);
	}

	@GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public String test2(@PathVariable Long id,
			@Conjunction(value = {
					@Filter(path = "name", parameters = "name", operator = StartsWith.class) }) NoDeletionSpecification<Department> specification)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("name", "Blanka");
		node.put("foundSpec", specification != null);
		specification.toPredicate(null, null, null);
		return mapper.writeValueAsString(node);
	}

}
