package org.oopscraft.apps.core.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.core.support.ThumbnailGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
	
	private final UserRepository userRepository;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * getUsers
     * 
     * @param userSearch userSearch
     * @param pageRequest pageRequest
     * @return list of users
	 */
    public List<User> getUsers(UserSearch userSearch, PageRequest pageRequest) {
    	
    	// where clause
		Specification<User> specification = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if(userSearch.getId() != null) {
				predicates.add(builder.like(root.get(User_.ID), '%' + userSearch.getId() + "%"));
			}
			if(userSearch.getName() != null) {
				predicates.add(builder.like(root.get(User_.NAME), '%' + userSearch.getName() + "%"));
			}
			if(userSearch.getEmail() != null) {
				predicates.add(builder.like(root.get(User_.EMAIL), '%' + userSearch.getEmail() + "%"));
			}
			if(userSearch.getMobile() != null) {
				predicates.add(builder.like(root.get(User_.MOBILE), '%' + userSearch.getMobile() + "%"));
			}
			if(userSearch.getStatus() != null) {
				predicates.add(builder.equal(root.get(User_.STATUS), userSearch.getStatus()));
			}
			if(userSearch.getType() != null) {
				predicates.add(builder.equal(root.get(User_.TYPE), userSearch.getType()));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

		// sort clause
		Sort sort = Sort.by(Order.desc(User_.SYSTEM_DATA).nullsLast(), Order.asc(User_.ID));
		pageRequest.setSort(sort);

		// retrieves
		Page<User> usersPage = userRepository.findAll(specification, pageRequest);
		pageRequest.setTotalCount(usersPage.getTotalElements());
		return usersPage.getContent();
    }

	/**
     * getUser
     * 
     * @param id id
     * @return user
     */
    public User getUser(String id) {
    	return userRepository.findById(id).orElse(null);
    }

    /**
     * saveUser
     * @param user user
     */
    public void saveUser(User user) {
    	User one = userRepository.findById(user.getId()).orElse(null);
    	if(one == null) {
    		one = new User();
    		one.setId(user.getId());
    		one.setPassword(passwordEncoder.encode(user.getPassword()));
    	}
    	one.setName(user.getName());
		one.setType(user.getType());
    	one.setStatus(user.getStatus());
    	one.setEmail(user.getEmail());
    	one.setMobile(user.getMobile());
    	one.setLocale(user.getLocale());
    	one.setProfile(user.getProfile());
    	
		// creates thumb nail
    	one.setPhoto(user.getPhoto());
		if(one.getPhoto() != null) {
			try {
				String iconDataUrl = ThumbnailGenerator.generate(one.getPhoto(), 32, 32);
				one.setIcon(iconDataUrl);
			}catch(Exception ignore) {}
		}else {
			one.setIcon(null);
		}

		// sets roles
    	one.setRoles(user.getRoles());
    	
		// persists
    	userRepository.saveAndFlush(one);
    }

    /**
     * deleteUser
     * @param id id
     */
    public void deleteUser(String id) {
    	userRepository.deleteById(id);
		userRepository.flush();
    }

	/**
	 * change password
	 *
	 * @param id
	 * @param password
	 * @throws Exception
	 */
	public void changePassword(String id, String password) throws Exception {
		User one = userRepository.findById(id).orElse(null);
		if (one != null) {
			one.setPassword(passwordEncoder.encode(password));
			userRepository.saveAndFlush(one);
		}
	}

}
