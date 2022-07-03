package org.oopscraft.apps.core.menu;

import org.oopscraft.apps.core.data.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

	@Autowired
	private MenuRepository menuRepository;

	/**
	 * getMenus
	 * @param menuSearch
	 * @param pageRequest
	 * @return
	 * @throws Exception
	 */
	public List<Menu> getMenus(MenuSearch menuSearch, PageRequest pageRequest) throws Exception {
		
		// where clause
		Specification<Menu> specification = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<Predicate>();
			if(menuSearch.getId() != null) {
				predicates.add(builder.like(root.get(Menu_.ID), "%" + menuSearch.getId() + "%"));
			}
			if(menuSearch.getName() != null) {
				predicates.add(builder.like(root.get(Menu_.NAME), "%" + menuSearch.getName() + "%"));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

		// sort clause
		Sort sort = Sort.by(Order.desc(Menu_.SYSTEM_DATA).nullsLast(), Order.asc(Menu_.ID));
		pageRequest.setSort(sort);

		// retrieves
		Page<Menu> menusPage = menuRepository.findAll(specification, pageRequest);
		pageRequest.setTotalCount(menusPage.getTotalElements());
		return menusPage.getContent();
	}

	/**
	 * getMenu
	 * @param menu
	 * @return
	 * @throws Exception
	 */
	public Menu getMenu(Menu menu) throws Exception {
		return menuRepository.findById(menu.getId()).orElse(null);
	}

	/**
	 * saveMenu
	 * @param menu
	 * @throws Exception
	 */
	public void saveMenu(Menu menu) throws Exception {
		Menu one = menuRepository.findById(menu.getId()).orElse(null);
		if(one == null) {
			one = new Menu();
			menu.setId(menu.getId());
		}
		one.setName(menu.getName());
		one.setSort(menu.getSort());
		one.setName(menu.getName());
		one.setUrl(menu.getUrl());
		one.setOpenNew(menu.isOpenNew());
		one.setNote(menu.getNote());
		menuRepository.saveAndFlush(one);
	}

	/**
	 * deleteMenu
	 * @param menu
	 * @throws Exception
	 */
	public void deleteMenu(Menu menu) throws Exception {
		menuRepository.delete(menu);
	}

}
