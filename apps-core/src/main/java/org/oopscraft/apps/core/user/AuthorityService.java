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

@Service
@RequiredArgsConstructor
public class AuthorityService {
	
	private final AuthorityRepository authorityRepository;

	/**
	 * Returns authorities
	 * @param authoritySearch authoritySearch
	 * @param pageRequest pageRequest
	 * @return list of authorities
	 */
    public List<Authority> getAuthorities(AuthoritySearch authoritySearch, PageRequest pageRequest) {

    	// where clause
		Specification<Authority> specification = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if(authoritySearch.getId() != null) {
				predicates.add(builder.like(root.get(Authority_.ID), "%" + authoritySearch.getId() + '%'));
			}
			if(authoritySearch.getName() != null) {
				predicates.add(builder.like(root.get(Authority_.NAME), "%" + authoritySearch.getName() + '%'));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

		// sort clause
		Sort sort = Sort.by(Order.desc(Authority_.SYSTEM_DATA).nullsLast(), Order.asc(Authority_.ID));
		pageRequest.setSort(sort);

		// retrieves
		Page<Authority> authoritiesPage = authorityRepository.findAll(specification, pageRequest);
		pageRequest.setTotalCount(authoritiesPage.getTotalElements());
		return authoritiesPage.getContent();
    }

	/**
	 * getAuthority
	 * @param id
	 * @return
	 */
	public Authority getAuthority(String id) {
    	return authorityRepository.findById(id).orElse(null);
    }

    /**
     * Saves authority
     * @param authority authority
     */
    public void saveAuthority(Authority authority) {
    	Authority one = authorityRepository.findById(authority.getId()).orElse(null);
    	if(one == null) {
			one = Authority.builder()
					.id(authority.getId())
					.build();
    	}
    	one.setName(authority.getName());
    	one.setIcon(authority.getIcon());
    	one.setNote(authority.getNote());
    	authorityRepository.saveAndFlush(one);
    }

    /**
     * Deletes authority
     * @param id id
     */
    public void deleteAuthority(String id) {
        authorityRepository.deleteById(id);
		authorityRepository.flush();
    }
}
