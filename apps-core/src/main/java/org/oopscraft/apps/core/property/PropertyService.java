package org.oopscraft.apps.core.property;

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
public class PropertyService {

	private final PropertyRepository propertyRepository;

	/**
	 * Returns properties
	 * @param propertySearch propertySearch
	 * @param pageRequest pagination info
	 * @return list of properties
	 * @throws Exception
	 */
    public List<Property> getProperties(PropertySearch propertySearch, PageRequest pageRequest) {
    	// where clause
		Specification<Property> specification = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if(propertySearch.getId() != null) {
				predicates.add(builder.like(root.get(Property_.ID), '%' + propertySearch.getId() + '%'));
			}
			if(propertySearch.getName() != null) {
				predicates.add(builder.like(root.get(Property_.NAME), '%' + propertySearch.getName() + '%'));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

		// sort clause
		Sort sort = Sort.by(Order.asc(Property_.SYSTEM_DATA).nullsLast(), Order.asc(Property_.ID));
		pageRequest.setSort(sort);

		// retrieves
		Page<Property> propertiesPage = propertyRepository.findAll(specification, pageRequest);
		pageRequest.setTotalCount(propertiesPage.getTotalElements());
		return propertiesPage.getContent();
    }

    /**
     * Returns property
     * @param id id
     * @return
     * @throws Exception
     */
    public Property getProperty(String id) {
    	return propertyRepository.findById(id).orElse(null);
    }

    /**
     * Saves property
     * @param property
     * @throws Exception
     */
    public void saveProperty(Property property) {
    	Property one = propertyRepository.findById(Optional.ofNullable(property.getId()).orElse("")).orElse(null);
    	if(one == null) {
    		one = Property.builder()
					.id(property.getId())
					.build();
    	}
    	one.setName(property.getName());
    	one.setValue(property.getValue());
    	one.setNote(property.getNote());
    	propertyRepository.saveAndFlush(one);
    }

    /**
     * Deletes property
     * @param id id
     * @throws Exception
     */
    public void deleteProperty(String id) {
        propertyRepository.deleteById(id);
		propertyRepository.flush();
    }

}
