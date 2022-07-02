package org.oopscraft.apps.core.user;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.data.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;
	
	/**
	 * getRoles
	 * @param roleSearch search condition
	 * @param pageRequest pagination
	 * @return list of roles
	 */
    public List<Role> getRoles(RoleSearch roleSearch, PageRequest pageRequest) {
    	// where clause
		Specification<Role> specification = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if(roleSearch.getId() != null) {
				predicates.add(builder.like(root.get(Role_.ID), "%" + roleSearch.getId() + '%'));
			}
			if(roleSearch.getName() != null) {
				predicates.add(builder.like(root.get(Role_.NAME), "%" + roleSearch.getName() + '%'));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

		// sort clause
		Sort sort = Sort.by(Order.desc(Role_.SYSTEM_DATA).nullsLast(), Order.asc(Role_.ID));
		pageRequest.setSort(sort);

		// retrieves
		Page<Role> rolesPage = roleRepository.findAll(specification, pageRequest);
		pageRequest.setTotalCount(rolesPage.getTotalElements());
		return rolesPage.getContent();
    }

    /**
     * Returns role
     * @param id id
     * @return role
     */
    public Role getRole(String id) {
    	return roleRepository.findById(id).orElse(null);
    }

    /**
     * saveRole
     * @param role role
     */
    public void saveRole(Role role) {
    	Role one = roleRepository.findById(Optional.ofNullable(role.getId()).orElse("")).orElse(null);
    	if(one == null) {
    		one = new Role();
    		one.setId(role.getId());
    	}
    	one.setName(role.getName());
    	one.setIcon(role.getIcon());
    	one.setNote(role.getNote());
    	one.setAuthorities(role.getAuthorities());
    	roleRepository.saveAndFlush(one);
    }

    /**
     * Deletes role
     * @param role role
     */
    public void deleteRole(String id) {
    	roleRepository.deleteById(id);
		roleRepository.flush();
    }
    
}
